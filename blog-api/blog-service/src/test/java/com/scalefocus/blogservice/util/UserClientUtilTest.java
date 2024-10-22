package com.scalefocus.blogservice.util;

import com.scalefocus.blogservice.dto.UserClientDto;
import com.scalefocus.blogservice.utils.UserClientUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;

public class UserClientUtilTest {

    public static final String USER_DETAILS = "http://user-service/api/users/getUserDetails";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserClientUtil userClientUtil;

    private UserClientDto userClientDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userClientDto = new UserClientDto(1L, "test-client-user");
        doReturn(userClientDto).when(restTemplate).getForObject(USER_DETAILS, UserClientDto.class);
    }

    @Test
    public void testGettingAuthenticatedUser() {
        UserClientDto foundedUser = userClientUtil.getAuthenticatedUser();

        assertThat(foundedUser).isNotNull();
        assertThat(foundedUser.getId()).isEqualTo(userClientDto.getId());
        assertThat(foundedUser.getUsername()).isEqualTo(userClientDto.getUsername());

    }

}
