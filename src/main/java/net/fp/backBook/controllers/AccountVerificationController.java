package net.fp.backBook.controllers;

import net.fp.backBook.dtos.StatusDto;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.TokenExpiredException;
import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.User;
import net.fp.backBook.services.EmailSenderService;
import net.fp.backBook.services.EmailVerificationTokenService;
import net.fp.backBook.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/account")
public class AccountVerificationController {

    private final UserService userService;
    private final EmailVerificationTokenService tokenService;
    private final EmailSenderService emailSenderService;

    @Autowired
    public AccountVerificationController(final UserService userService,
                                         final EmailVerificationTokenService tokenService,
                                         final EmailSenderService emailSenderService
                                         ) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping(value = "/verify/{token}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public StatusDto verifyAccount(@PathVariable("token") String token) {
        EmailVerificationToken verificationToken =
                tokenService.getVerificationTokenByRequested(token);
        if(verificationToken.getExpires() != null) {
            if(verificationToken.getExpires().isBefore(LocalDateTime.now())) {
                throw new GetException("E-Mail verification token expired.");
            }
        }
        User user = verificationToken.getUser();
        if(user.isEnabled()) {
            tokenService.delete(verificationToken.getId());
            return new StatusDto("Already activated.", false);
        }
        user.setEnabled(true);
        user = userService.activate(user.getId());
        tokenService.delete(verificationToken.getId());
        //MAIL SENDING LOGIC:
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(user.getLogin() +  " - welcome on board!");
        message.setFrom("noreply@fp.net");
        message.setText("Your account has been successfully activated. You can use website from now on :)");
        this.emailSenderService.sendSimpleMail(message);
        return new StatusDto("Verification success.", true);
    }
}
