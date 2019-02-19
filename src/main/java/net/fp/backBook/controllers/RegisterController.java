package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.exceptions.RegisterException;
import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.services.EmailSenderService;
import net.fp.backBook.services.EmailVerificationTokenService;
import net.fp.backBook.services.RoleService;
import net.fp.backBook.services.UserService;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/register")
@Slf4j
public class RegisterController {

    @Value("${net.fp.backBook.email_token_exp}")
    private int EXPIRATION;

    private EmailSenderService emailSenderService;

    private EmailVerificationTokenService verificationTokenService;

    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private RoleService roleService;

    private ModelMapper modelMapper;

    @Autowired
    public RegisterController(final UserService userService,
                              final RoleService roleService,
                              final ModelMapper modelMapper,
                              final PasswordEncoder passwordEncoder,
                              final EmailSenderService emailSenderService,
                              final EmailVerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailSenderService = emailSenderService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody UserDto userDto) {
        try {
            User user = this.modelMapper.map(userDto, User.class);
            Role role = this.roleService.getByName("ROLE_USER");
            user.setRoles(Collections.singletonList(role));
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user = this.userService.add(user);

            String verifyTokenGenerated = user.getId();
            EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                    .token(verifyTokenGenerated)
                    .user(user)
                    .expires(LocalDateTime.now().plusMinutes(EXPIRATION))
                    .build();
            verificationToken = verificationTokenService.add(verificationToken);

            //MAIL SENDING LOGIC:
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userDto.getEmail());
            message.setSubject(userDto.getLogin() +  " - please confirm your registration!");
            message.setFrom("noreply@fp.net");
            message.setText("Please confirm you account registration. To do so click on the link below or" +
                    " copy it into your browser's address bar: http://localhost:8080/account/verify/" +
                    verificationToken.getToken());
            this.emailSenderService.sendSimpleMail(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegisterException("Registration failed. " + e.getMessage());
        }
    }
}
