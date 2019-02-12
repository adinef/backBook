package net.fp.backBook.services;

import net.fp.backBook.exceptions.AuthenticationException;
import net.fp.backBook.model.User;
import net.fp.backBook.security.service.JsonWebTokenService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class TokenServiceTest {

    @InjectMocks
    private JsonWebTokenService tokenService;

    @Mock
    UserDetailsService userDetailsService;

    @Test
    public void testGetTokenReturnsNullOnUserNameOrPasswordNull() {
        String token = tokenService.getToken(null, null);
        Assert.assertNull(token);
    }

    @Test(expected = AuthenticationException.class)
    public void testGetTokenThrowsOnPasswordNotMatching() {
        UserDetails userDetails = User.builder().login("test").password("pass").build();
        when(userDetailsService.loadUserByUsername("test")).thenReturn(userDetails);
        tokenService.getToken("test", "wrong_pass");
    }

    @Test(expected = AuthenticationException.class)
    public void testGetTokenThrowsOnProblemDuringGettingUserDetails() {
        when(userDetailsService.loadUserByUsername("test")).thenThrow(RuntimeException.class);
        tokenService.getToken("test", "pass");
    }
}
