package net.fp.backBook.authentication;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication(scanBasePackages = {"net.fp.backBook.authentication"})
public class AuthenticationFacadeTest {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @WithMockUser(username="username")
    @Test
    public void testGetAuthentication() {
        Assert.assertEquals("username", this.authenticationFacade.getAuthentication().getName());
    }
}
