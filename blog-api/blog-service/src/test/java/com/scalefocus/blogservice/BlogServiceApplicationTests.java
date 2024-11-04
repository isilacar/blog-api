package com.scalefocus.blogservice;

import com.scalefocus.blogservice.dto.BlogDto;
import com.scalefocus.blogservice.dto.TagDto;
import com.scalefocus.blogservice.dto.UserClientDto;
import com.scalefocus.blogservice.entity.ElasticBlogDocument;
import com.scalefocus.blogservice.entity.Tag;
import com.scalefocus.blogservice.producer.KafkaElasticBlogProducer;
import com.scalefocus.blogservice.repository.BlogRepository;
import com.scalefocus.blogservice.repository.ElasticBlogRepository;
import com.scalefocus.blogservice.request.BlogCreationRequest;
import com.scalefocus.blogservice.request.BlogUpdateRequest;
import com.scalefocus.blogservice.request.TagAddRequest;
import com.scalefocus.blogservice.response.SimplifiedBlogResponsePagination;
import com.scalefocus.blogservice.response.UserBlogResponse;
import com.scalefocus.blogservice.utils.UserClientUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
class BlogServiceApplicationTests extends AbstractMysqlContainer {

    @LocalServerPort
    private int portNumber;

    @SpyBean
    private UserClientUtil userClientUtil;

    private String baseUrl;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ElasticBlogRepository elasticBlogRepository;

    @MockBean
    private KafkaElasticBlogProducer kafkaElasticBlogProducer;

    private static TestRestTemplate testRestTemplate;
    private Long userId;

    private UserClientDto userClientDto;

    @BeforeAll
    public static void init() {
        testRestTemplate = new TestRestTemplate();

    }

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + portNumber + "/api/blogs";
        userId = 1L;
        userClientDto = new UserClientDto(1L, "test-client-user");
        doReturn(userClientDto).when(userClientUtil).getAuthenticatedUser();
        doReturn(userClientDto).when(userClientUtil).findUser(userId);
        doNothing().when(kafkaElasticBlogProducer).createEvent(any(ElasticBlogDocument.class));
    }

    @Test
    public void testAddingBlog() {
        Set<TagDto> tagDtoSet = new HashSet<>();
        TagDto tagDto = TagDto.builder()
                .name("integration tag")
                .build();
        tagDtoSet.add(tagDto);
        BlogDto blogDto = BlogDto.builder()
                .title("integration title")
                .text("integration text")
                .tagDtoSet(tagDtoSet)
                .build();
        BlogCreationRequest blogCreationRequest = BlogCreationRequest.builder()
                .title(blogDto.title())
                .text(blogDto.text())
                .userId(userId)
                .tags(tagDtoSet.stream().map(t -> new Tag()).collect(Collectors.toSet()))
                .build();

        BlogDto savedBlogDto = testRestTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(blogCreationRequest, null), BlogDto.class).getBody();

        assertEquals("integration title", savedBlogDto.title());
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testGettingAllBlogs() {

        List<BlogDto> blogDtoListResponse = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BlogDto>>() {
                }
        ).getBody();

        assertThat(blogDtoListResponse.size()).isGreaterThanOrEqualTo(1);
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);

    }

    @Test
    public void testGettingUserBlogs() {
        String userBlogsUrl = baseUrl + "/{userId}";

        UserBlogResponse userBlogResponse = testRestTemplate.exchange(userBlogsUrl, HttpMethod.GET, null, UserBlogResponse.class, userId).getBody();

        assertAll(
                () -> assertNotNull(userBlogResponse),
                () -> assertThat(userBlogResponse.getBlogs().size()).isGreaterThanOrEqualTo(2)
        );
    }

    @Test
    public void testUpdatingBlog() {
        String updateBlogUrl = baseUrl + "/{blogId}";

        BlogUpdateRequest blogUpdateRequest = new BlogUpdateRequest("updated title", "updated text");

        BlogDto blogFromDB = testRestTemplate.exchange(updateBlogUrl, HttpMethod.PUT, new HttpEntity<>(blogUpdateRequest, null), BlogDto.class, 2L).getBody();

        assertAll(
                () -> assertNotNull(blogFromDB),
                () -> assertEquals("updated text", blogFromDB.text()),
                () -> assertEquals("updated title", blogFromDB.title())
        );
    }

    @Test
    public void testAddingTagToBlog() {
        String addTagUrl = baseUrl + "/{blogId}/tags";

        TagAddRequest tagAddRequest = new TagAddRequest("test tag");
        BlogDto blogFromDB = testRestTemplate.exchange(addTagUrl, HttpMethod.PUT, new HttpEntity<>(tagAddRequest, null), BlogDto.class, 2L).getBody();

        boolean tagFromDB = blogFromDB.tagDtoSet().stream().anyMatch(tag -> tag.name().equals("test tag"));

        assertAll(
                () -> assertTrue(tagFromDB),
                () -> assertNotNull(blogFromDB)
        );

    }

    @Test
    public void deletingTagFromBlog() {
        String deleteTagUrl = baseUrl + "/{blogId}/tags/{tagId}";

        BlogDto foundedBlogDto = testRestTemplate.exchange(deleteTagUrl, HttpMethod.DELETE, null, BlogDto.class, 2L, 2L).getBody();

        boolean tagFromDB = foundedBlogDto.tagDtoSet().stream().anyMatch(tag -> tag.name().equals("Test Tag2"));

        assertAll(
                () -> assertFalse(tagFromDB),
                () -> assertNotNull(foundedBlogDto)
        );
    }

    @Test
    public void testGettingBlogsBySpecificTagName() {
        String specificTagUrl = baseUrl + "/tagName/{tagName}";

        List<BlogDto> blogDtoListResponse = testRestTemplate.exchange(
                specificTagUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BlogDto>>() {
                },
                "Test Tag3"
        ).getBody();

        assertThat(blogDtoListResponse.size()).isGreaterThanOrEqualTo(1);
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);

    }


    @Test
    public void testGettingSimplifiedBlogResponse() {
        String simplifiedBlogUrl = baseUrl + "/simplified?pageNumber={pageNumber}&pageSize={pageSize}";

        SimplifiedBlogResponsePagination simplifiedBlogResponses = testRestTemplate.exchange(
                        simplifiedBlogUrl,
                        HttpMethod.GET,
                        null,
                        SimplifiedBlogResponsePagination.class, 1, 1)
                .getBody();

        assertNotNull(simplifiedBlogResponses);
        assertThat(simplifiedBlogResponses.getSimplifiedBlogResponseList().size()).isGreaterThanOrEqualTo(1);
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);

    }

    @Test
    public void testingDeleteUserBlog() {
        String deleteUserBlogUrl = baseUrl + "/{blogId}/{userId}";

        ResponseEntity<Void> deleteResponseEntity = testRestTemplate.exchange(deleteUserBlogUrl, HttpMethod.DELETE, null, Void.class, 4L, userId);
        assertThat(deleteResponseEntity).isNotNull();
        assertThat(deleteResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

}
