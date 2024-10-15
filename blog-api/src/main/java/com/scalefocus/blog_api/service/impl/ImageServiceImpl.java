package com.scalefocus.blog_api.service.impl;

import com.scalefocus.blog_api.entity.Blog;
import com.scalefocus.blog_api.entity.Image;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.exception.ResourceNotFound;
import com.scalefocus.blog_api.exception.TypeNotMatchedException;
import com.scalefocus.blog_api.repository.ImageRepository;
import com.scalefocus.blog_api.response.ImageResourceResponse;
import com.scalefocus.blog_api.service.ImageService;
import com.scalefocus.blog_api.utils.BlogUtil;
import com.scalefocus.blog_api.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Value(("${file.path}"))
    private String uploadDir;

    private final ImageRepository imageRepository;
    private final SecurityUtil securityUtil;
    private final BlogUtil blogUtils;

    @Override
    public String saveImage(MultipartFile file, Long blogId, Integer imageWidth, Integer imageHeight) {
        Optional<User> requestingUser = securityUtil.getRequestingUser();

        log.info("Checking if uploading file is image");

        try {
            BufferedImage imageFile = ImageIO.read(file.getInputStream());
            if (Objects.isNull(imageFile)) {
                log.error("Uploading File is not an image");
                throw new TypeNotMatchedException("You have to upload only images");
            }
            log.info("Uploading file type is image");

            Blog foundBlog = blogUtils.checkUserHasSpecificBlog(blogId, requestingUser.get());

            Path directory = Paths.get(System.getProperty("user.dir") + "/" + uploadDir);

            if (!Files.exists(directory)) {
                log.info("Directory does not exist,creating new one with path: {}", directory);
                Files.createDirectories(directory);
            }

            log.info("Resizing image");
            BufferedImage resizedImage = getResizedImage(file, imageWidth, imageHeight);

            File savedImageFile = new File(directory + "/" + file.getOriginalFilename());
            ImageIO.write(resizedImage, "jpeg", savedImageFile);

            Image image = Image.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .blog(foundBlog)
                    .filePath(savedImageFile.toString())
                    .build();

            log.info("Image with file path:{}Saving image", savedImageFile);
            imageRepository.save(image);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getMessage());

        }
        return "Image file uploaded successfully: " + file.getOriginalFilename();
    }

    public void deleteImage(Long imageId, Long blogId) {
        Optional<User> requestingUser = securityUtil.getRequestingUser();

        blogUtils.checkUserHasSpecificBlog(blogId, requestingUser.get());

        Image foundImage = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFound("image with id: " + imageId + " is not exist"));

        boolean doesImageHaveSpecificBlog = foundImage.getBlog().getId().equals(blogId);
        if (!doesImageHaveSpecificBlog) {
            throw new ResourceNotFound("image with id: " + imageId + " doesn't have specific blog with id: " + blogId);
        }

        Path filepath = Paths.get(foundImage.getFilePath()).normalize();
        if (Files.exists(filepath)) {
            imageRepository.delete(foundImage);
            try {
                Files.delete(filepath);
            } catch (IOException e) {
                throw new RuntimeException("Image with id:" + imageId + " cannot be deleted");
            }
        } else {
            throw new ResourceNotFound("File url path does not exist: " + filepath);
        }
    }

    /**
     * Retrieves existing image
     *
     * @param imageId the image id which is existing in image database
     * @return image itself
     */
    @Override
    public ImageResourceResponse getImage(Long imageId) {
        try {
            Image foundImage = imageRepository.findById(imageId)
                    .orElseThrow(() -> new ResourceNotFound("image: " + imageId + " is not exist"));
            Path filePath = Paths.get(foundImage.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFound("File not found with imageId: " + imageId);
            }
            return new ImageResourceResponse(Files.probeContentType(filePath), resource);
        } catch (IOException e) {
            throw new RuntimeException("File url path is not valid");
        }
    }


    private BufferedImage getResizedImage(MultipartFile file, Integer imageWidth, Integer imageHeight) {
        try {
            BufferedImage original = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(original, 0, 0, imageWidth, imageHeight, null);
            graphics2D.dispose();
            return resizedImage;
        } catch (IOException e) {
            throw new RuntimeException("Can not read resized image");
        }
    }

}