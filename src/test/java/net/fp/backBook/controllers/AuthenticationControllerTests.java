package net.fp.backBook.controllers;

import com.sun.deploy.net.HttpResponse;
import net.fp.backBook.dtos.Credentials;
import net.fp.backBook.dtos.TokenDto;
import net.fp.backBook.exceptions.AuthenticationException;
import net.fp.backBook.security.service.TokenService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"net.fp.backBook.repositories",
        "net.fp.backBook.services", "net.fp.backBook.controllers",
        "net.fp.backBook.security.service"})
@SpringBootTest
public class AuthenticationControllerTests {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private TokenService tokenService;

    @Test
    public void testTokenOnLoginReturned() {
        when(this.tokenService.getToken(anyString(), anyString())).thenReturn("1");
        Assert.assertEquals(
                new ResponseEntity<TokenDto>(new TokenDto("1"), HttpStatus.OK),
                this.authenticationController.authenticate(Credentials.builder()
                        .login(anyString())
                        .password(anyString())
                        .build())
        );
    }

    @Test(expected = AuthenticationException.class)
    public void testTokenOnWrongDataThrow() {
        when(this.tokenService.getToken(anyString(), anyString())).thenThrow(RuntimeException.class);

        this.authenticationController.authenticate(Credentials.builder()
                .login(anyString())
                .password(anyString())
                .build());
    }
}
