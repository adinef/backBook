package net.fp.backBook.services;

import net.fp.backBook.authentication.AuthenticationFacade;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.User;
import net.fp.backBook.model.UserAuthentication;
import net.fp.backBook.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class UserDetectionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private UserDetectionServiceImpl userDetectionService;

    @Test
    public void testGetAuthenticatedUserSuccess() {
        String login = "login";
        User user = User.builder()
                .login(login).build();
        when(this.authenticationFacade.getAuthentication()).thenReturn(new UserAuthentication(user));
        when(this.userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        Assert.assertEquals(user, this.userDetectionService.getAuthenticatedUser());

        verify(userRepository).findByLogin(login);
    }

    @Test(expected = GetException.class)
    public void testGetAuthenticatedUserFailure() {
        String login = "login";
        User user = User.builder()
                .login(login).build();
        when(this.authenticationFacade.getAuthentication()).thenReturn(new UserAuthentication(user));
        when(this.userRepository.findByLogin(login)).thenReturn(Optional.empty());

        this.userDetectionService.getAuthenticatedUser();
    }
}
