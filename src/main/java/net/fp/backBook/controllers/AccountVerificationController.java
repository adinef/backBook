package net.fp.backBook.controllers;

import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.User;
import net.fp.backBook.services.EmailVerificationTokenService;
import net.fp.backBook.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountVerificationController {

    private final UserService userService;
    private final EmailVerificationTokenService tokenService;

    @Autowired
    public AccountVerificationController(final UserService userService,
                                         final EmailVerificationTokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping(value = "/verify/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String verifyAccount(@RequestParam("token") String token) {
        EmailVerificationToken verificationToken =
                tokenService.getVerificationTokenByRequested(token);
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userService.modify(user);
        return "{\"status\": \"Verification success.\"}";
    }
}
