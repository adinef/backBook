package net.fp.backBook.controllers;

import net.fp.backBook.dtos.Credentials;
import net.fp.backBook.dtos.TokenDto;
import net.fp.backBook.exceptions.AuthenticationException;
import net.fp.backBook.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final TokenService tokenService;

    @Autowired
    public AuthenticationController(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping(
            value = ""
    )
    public TokenDto authenticate(@RequestBody Credentials credentials) {
        String token;
        try {
            token = tokenService.getToken(
                    credentials.getLogin(),
                    credentials.getPassword()
            );
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed. " + e.getMessage());
        }
        if (token == null) {
            throw new AuthenticationException("Authentication failed. No token granted.");
        }
        return TokenDto.builder().token(token).build();
    }

}
