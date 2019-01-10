package net.fp.backBook.controllers;

import net.fp.backBook.dtos.Credentials;
import net.fp.backBook.dtos.TokenDto;
import net.fp.backBook.security.service.TokenService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
                new ResponseEntity<>(new TokenDto("1"), HttpStatus.OK),
                this.authenticationController.authenticate(Credentials.builder()
                        .login(anyString())
                        .password(anyString())
                        .build())
        );
    }

    @Test
    public void testTokenOnWrongDataBadRequest() {
        when(this.tokenService.getToken(anyString(), anyString())).thenThrow(RuntimeException.class);
        Assert.assertEquals(
                new ResponseEntity<>(HttpStatus.BAD_REQUEST).getStatusCode(),
                this.authenticationController.authenticate(Credentials.builder()
                        .login(anyString())
                        .password(anyString())
                        .build()).getStatusCode()
        );
    }
}
