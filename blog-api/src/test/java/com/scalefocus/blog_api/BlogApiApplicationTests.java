package com.scalefocus.blog_api;

import com.scalefocus.blog_api.dto.BlogDto;
import com.scalefocus.blog_api.dto.TagDto;
import com.scalefocus.blog_api.entity.Tag;
import com.scalefocus.blog_api.entity.Token;
import com.scalefocus.blog_api.entity.User;
import com.scalefocus.blog_api.repository.BlogRepository;
import com.scalefocus.blog_api.repository.ElasticBlogRepository;
import com.scalefocus.blog_api.repository.TokenRepository;
import com.scalefocus.blog_api.repository.UserRepository;
import com.scalefocus.blog_api.request.*;
import com.scalefocus.blog_api.response.SimplifiedBlogResponse;
import com.scalefocus.blog_api.response.TokenResponse;
import com.scalefocus.blog_api.response.UserBlogResponse;
import com.scalefocus.blog_api.service.impl.UserServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
class BlogApiApplicationTests extends AbstractMysqlContainer {

    public static final String SECRET_KEY = "f07afe0e45657f1df3d7cf9141c39185527363b9e7b47225af954d6ed6a801db";

    @LocalServerPort
    private int portNumber;

    private String baseUrl;
    private String jwtToken;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ElasticBlogRepository elasticBlogRepository;

    private static TestRestTemplate restTemplate;
    private User user;

    private HttpHeaders headers;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @BeforeAll
    public static void init() {
        restTemplate = new TestRestTemplate();

    }

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + portNumber + "/api";
        user = userRepository.findById(3L).get();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        jwtToken = generateToken(user.getUsername());
        Token token = new Token();
        token.setUser(user);
        token.setExpired(false);
        token.setToken(jwtToken);

        tokenRepository.save(token);

        headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getToken());
    }

    @AfterEach
    public void tearDown() {
        tokenRepository.deleteAll();
    }

    @Test
    public void testAddingBlog() {
        String addingBlogUrl = baseUrl + "/blogs";

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
                .userId(userRepository.findById(3L).get().getId())
                .tags(tagDtoSet.stream().map(t -> new Tag()).collect(Collectors.toSet()))
                .build();

        BlogDto savedBlogDto = restTemplate.exchange(addingBlogUrl, HttpMethod.POST, new HttpEntity<>(blogCreationRequest, headers), BlogDto.class).getBody();

        assertEquals("integration title", savedBlogDto.title());
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testGettingAllBlogs() {
        String allBlogsUrl = baseUrl + "/blogs";

        List<BlogDto> blogDtoListResponse = restTemplate.exchange(
                allBlogsUrl,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<List<BlogDto>>() {
                }
        ).getBody();

        assertThat(blogDtoListResponse.size()).isGreaterThanOrEqualTo(1);
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);

    }

    @Test
    public void testGettingUserBlogs() {
        String userBlogsUrl = baseUrl + "/blogs/users/{username}";

        UserBlogResponse userBlogResponse = restTemplate.exchange(userBlogsUrl, HttpMethod.GET, new HttpEntity<>(null, headers), UserBlogResponse.class, user.getUsername()).getBody();

        assertAll(
                () -> assertNotNull(userBlogResponse),
                () -> assertEquals(user.getDisplayName(), userBlogResponse.getDisplayName()),
                () -> assertThat(userBlogResponse.getBlogs().size()).isGreaterThanOrEqualTo(2)
        );

    }

    @Test
    public void testUpdatingBlog() {
        String updateBlogUrl = baseUrl + "/blogs/{blogId}";

        BlogUpdateRequest blogUpdateRequest = new BlogUpdateRequest("updated title", "updated text");

        BlogDto blogFromDB = restTemplate.exchange(updateBlogUrl, HttpMethod.PUT, new HttpEntity<>(blogUpdateRequest, headers), BlogDto.class, 2L).getBody();

        assertAll(
                () -> assertNotNull(blogFromDB),
                () -> assertEquals("updated text", blogFromDB.text()),
                () -> assertEquals("updated title", blogFromDB.title())
        );
    }

    @Test
    public void testAddingTagToBlog() {
        String addTagUrl = baseUrl + "/blogs/{blogId}/tags";

        TagAddRequest tagAddRequest = new TagAddRequest("test tag");
        BlogDto blogFromDB = restTemplate.exchange(addTagUrl, HttpMethod.PUT, new HttpEntity<>(tagAddRequest, headers), BlogDto.class, 2L).getBody();

        boolean tagFromDB = blogFromDB.tagDtoSet().stream().anyMatch(tag -> tag.name().equals("test tag"));

        assertAll(
                () -> assertTrue(tagFromDB),
                () -> assertNotNull(blogFromDB)
        );

    }

    @Test
    public void deletingTagFromBlog() {
        String deleteTagUrl = baseUrl + "/blogs/{blogId}/tags/{tagId}";

        BlogDto foundedBlogDto = restTemplate.exchange(deleteTagUrl, HttpMethod.DELETE, new HttpEntity<>(null, headers), BlogDto.class, 2L, 2L).getBody();

        boolean tagFromDB = foundedBlogDto.tagDtoSet().stream().anyMatch(tag -> tag.name().equals("Test Tag2"));

        assertAll(
                () -> assertFalse(tagFromDB),
                () -> assertNotNull(foundedBlogDto)
        );
    }

    @Test
    public void testGettingBlogsBySpecificTagName() {
        String specificTagUrl = baseUrl + "/blogs/tagName/{tagName}";

        List<BlogDto> blogDtoListResponse = restTemplate.exchange(
                specificTagUrl,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<List<BlogDto>>() {
                },
                "Test Tag3"
        ).getBody();

        assertThat(blogDtoListResponse.size()).isGreaterThanOrEqualTo(1);
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);

    }


    @Test
    public void testGettingSimplifiedBlogResponse() {
        String simplifiedBlogUrl = baseUrl + "/blogs/simplified";

        List<SimplifiedBlogResponse> simplifiedBlogResponses = restTemplate.exchange(
                simplifiedBlogUrl,
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<List<SimplifiedBlogResponse>>() {
                }
        ).getBody();

        assertNotNull(simplifiedBlogResponses);
        assertThat(simplifiedBlogResponses.size()).isGreaterThanOrEqualTo(1);
        assertThat(blogRepository.findAll().size()).isGreaterThanOrEqualTo(1);

    }

    @Test
    public void testAuthenticateUser() {
        String authenticationUserUrl = baseUrl + "/users/authenticate";

        RegisterRequest registerRequest = new RegisterRequest("test user", "test password", "test display name");
        userServiceImpl.register(registerRequest);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(registerRequest.getUsername(), registerRequest.getPassword());
        ResponseEntity<TokenResponse> returnedToken =
                restTemplate.postForEntity(authenticationUserUrl, authenticationRequest, TokenResponse.class);

        String[] tokenPart = returnedToken.getBody().getToken().split("\\.");

        assertThat(returnedToken).isNotNull();
        assertEquals(returnedToken.getStatusCode(), HttpStatus.valueOf(200));
        assertThat(tokenPart.length).isEqualTo(3);

    }

    @Test
    public void testRegisterUser() {
        String registerUserUrl = baseUrl + "/users/register";
        RegisterRequest registerRequest = new RegisterRequest("username", "password", "displayName");
        ResponseEntity<TokenResponse> token = restTemplate
                .postForEntity(registerUserUrl, registerRequest, TokenResponse.class);

        String[] tokenPart = token.getBody().getToken().split("\\.");

        assertNotNull(token);
        assertEquals(token.getStatusCode(), HttpStatus.valueOf(201));
        assertThat(tokenPart.length).isEqualTo(3);
    }

    @Test
    public void testingDeleteUserBlog(){
        String deleteUserBlogUrl = baseUrl + "/blogs/{blogId}/users/{username}";

        ResponseEntity<Void> deleteResponseEntity = restTemplate.exchange(deleteUserBlogUrl, HttpMethod.DELETE, new HttpEntity<>(null, headers), Void.class,4L,user.getUsername());
        assertThat(deleteResponseEntity).isNotNull();
        assertThat(deleteResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);


    }

    private static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2073600000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

}
