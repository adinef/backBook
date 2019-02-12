package net.fp.backBook.services;

import net.fp.backBook.model.User;
import net.fp.backBook.security.service.BasicUserDetailsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class UserDetailsServiceTest {

    @InjectMocks
    private BasicUserDetailsService userDetailsService;

    @Mock
    UserService userService;

    @Test
    public void testLoadUserByUsernameReturns() {
        User user = User.builder().login("test").build();
        when(userService.getUserByLogin("test")).thenReturn(user);
        Assert.assertEquals("test",
                this.userDetailsService.loadUserByUsername("test")
                        .getUsername());
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsernameThrowsUsernameNotFoundOnRuntimeException() {
        when(userService.getUserByLogin("test")).thenThrow(RuntimeException.class);
        this.userDetailsService.loadUserByUsername("test");
    }

}
