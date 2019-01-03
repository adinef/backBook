package net.fp.backBook.controllers;

import net.fp.backBook.dtos.Credentials;
import net.fp.backBook.dtos.TokenDto;
import net.fp.backBook.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final TokenService tokenService;

    @Autowired
    public AuthenticationController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping(
            value = ""
    )
    public ResponseEntity<?> authenticate(@RequestBody Credentials credentials) {
        String token = tokenService.getToken(credentials.getLogin(), credentials.getPassword());
        if (token != null) {
            TokenDto response = new TokenDto();
            response.setToken(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Authentication failed", HttpStatus.BAD_REQUEST);
        }
    }
}
