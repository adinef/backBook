package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.services.RoleService;
import net.fp.backBook.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class RegisterControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    @Autowired
    private ModelMapper modelMapper;

    @Mock
    private ModelMapper modelMapperMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterController registerController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(registerController)
                .setControllerAdvice(restResponseExceptionHandler).build();
    }

    @Test
    public void registerSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("password")
                .email("email")
                .build();

        Role role = Role.builder()
                .id("1")
                .name("test")
                .build();

        UserDto userDto = this.modelMapper.map(user, UserDto.class);

        when(this.modelMapperMock.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(this.roleService.getByName("ROLE_USER")).thenReturn(role);


        this.mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        user.setRoles(Collections.singletonList(role));
        verify(this.userService).add(user);
    }

    @Test
    public void registerUserServiceFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("password")
                .email("email")
                .build();

        UserDto userDto = this.modelMapper.map(user, UserDto.class);

        when(this.modelMapperMock.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(this.roleService.getByName("ROLE_USER")).thenThrow(RuntimeException.class);

        this.mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerRoleServiceFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("password")
                .email("email")
                .build();

        Role role = Role.builder()
                .id("1")
                .name("test")
                .build();

        UserDto userDto = this.modelMapper.map(user, UserDto.class);

        when(this.modelMapperMock.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(this.roleService.getByName("ROLE_USER")).thenReturn(role);
        when(this.userService.add(any(User.class))).thenThrow(RuntimeException.class);

        this.mockMvc.perform(
                post("/register")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
