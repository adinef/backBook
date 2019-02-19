package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.RoleDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.services.EmailSenderService;
import net.fp.backBook.services.EmailVerificationTokenService;
import net.fp.backBook.services.RoleService;
import net.fp.backBook.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
 * @author Adrian Fijalkowski
 */

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class AccountVerificationControllerTests {

    @InjectMocks
    private AccountVerificationController verificationController;

    @Mock
    private UserService userService;

    @Mock
    private EmailVerificationTokenService tokenService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    private MockMvc mockMvc;

    @Before
    public void setupTests() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(verificationController)
                .setControllerAdvice(restResponseExceptionHandler)
                .build();
    }

    @Test
    public void testVerifySuccess() throws Exception {
        User user = User.builder()
                .id("1")
                .enabled(false)
                .email("test@test.com")
                .build();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .token("test")
                .build();
        when(tokenService.getVerificationTokenByRequested("test")).thenReturn(token);
        when(userService.activate(user.getId())).thenReturn(user);
        doNothing().when(emailSenderService).sendSimpleMail(any(SimpleMailMessage.class));
        doNothing().when(tokenService).delete(anyString());

        mockMvc.perform(get("/account/verify/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("success").value("true"));
    }

    @Test
    public void testVerifyFailureOnAlreadyEnabled() throws Exception {
        User user = User.builder()
                .enabled(true)
                .build();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .token("test")
                .build();
        when(tokenService.getVerificationTokenByRequested("test")).thenReturn(token);
        when(userService.modify(any(User.class))).thenReturn(user);

        mockMvc.perform(get("/account/verify/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("success").value("false"));
    }

    @Test
    public void testVerifyFailureOnExpiredToken() throws Exception {
        User user = User.builder()
                .enabled(false)
                .build();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .token("test")
                .expires(LocalDateTime.now().minusDays(1))
                .build();

        when(tokenService.getVerificationTokenByRequested("test")).thenReturn(token);
        when(userService.modify(any(User.class))).thenReturn(user);

        mockMvc.perform(get("/account/verify/test"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }
}
