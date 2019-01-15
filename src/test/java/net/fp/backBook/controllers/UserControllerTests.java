package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.dtos.UserViewDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.User;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
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
public class UserControllerTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    private MockMvc mockMvc;

    @Before
    public void setServerAddress() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(restResponseExceptionHandler)
                .build();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.objectMapper.setDateFormat(simpleDateFormat);
    }

    @Test
    public void testGetUsersReturns() throws Exception {
        List<User> users = Arrays.asList(mock(User.class), mock(User.class));
        when(userService.getAll()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").value(hasSize(2)));
        verify(userService).getAll();
    }

    @Test
    public void testGetUsersReturnsEmptyList() throws Exception {
        List<User> users = Arrays.asList();
        when(userService.getAll()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").isEmpty());
        verify(userService).getAll();
    }

    @Test
    public void testGetUsersBadRequestOnGetException() throws Exception {
        when(userService.getAll()).thenThrow(GetException.class);
        mockMvc.perform(get("/users"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(userService).getAll();
    }

    @Test
    public void testGetByIdReturns() throws Exception {
        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("password")
                .email("email")
                .build();
        UserViewDto userViewDto = UserViewDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .email("email")
                .build();
        when(userService.getById(anyString())).thenReturn(user);
        when(modelMapper.map(user, UserViewDto.class)).thenReturn(userViewDto);
        String path = "/users/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.email").value("email"));
        verify(userService).getById(anyString());
    }

    @Test
    public void testGetUserByIdBadRequestOnGetException() throws Exception {
        when(userService.getById(anyString())).thenThrow(GetException.class);
        String path = "/users/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(userService).getById(anyString());
    }

    @Test
    public void testGetByLoginReturns() throws Exception {
        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("password")
                .email("email")
                .build();
        UserViewDto userViewDto = UserViewDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .email("email")
                .build();
        when(userService.getUserByLogin(anyString())).thenReturn(user);
        when(modelMapper.map(user, UserViewDto.class)).thenReturn(userViewDto);
        String path = "/users/login/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.email").value("email"));
        verify(userService).getUserByLogin(anyString());
    }

    @Test
    public void testGetByLoginBadRequestOnGetException() throws Exception {
        when(userService.getUserByLogin(anyString())).thenThrow(GetException.class);
        String path = "/users/login/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(userService).getUserByLogin(anyString());
    }

    @Test
    public void testGetByEmailReturns() throws Exception {
        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("password")
                .email("email")
                .build();
        UserViewDto userViewDto = UserViewDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .email("email")
                .build();
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(modelMapper.map(user, UserViewDto.class)).thenReturn(userViewDto);
        String path = "/users/email/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.email").value("email"));
        verify(userService).getUserByEmail(anyString());
    }

    @Test
    public void testGetByEmailBadRequestOnGetException() throws Exception {
        when(userService.getUserByEmail(anyString())).thenThrow(GetException.class);
        String path = "/users/email/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(userService).getUserByEmail(anyString());
    }

    @Test
    public void testGetByCredentialsReturns() throws Exception {
        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("test")
                .email("email")
                .build();
        UserViewDto userViewDto = UserViewDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .email("email")
                .build();
        when(userService.getUserByLoginAndPassword(anyString(), anyString())).thenReturn(user);
        when(modelMapper.map(user, UserViewDto.class)).thenReturn(userViewDto);
        String path = "/users/credentials";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        objectMapper.writeValueAsString(
                                UserDto.builder().login("test").password("test").build()
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("email"));
        verify(userService).getUserByLoginAndPassword(anyString(), anyString());
    }

    @Test
    public void testGetByCredentialsBadRequestOnGetException() throws Exception {
        when(userService.getUserByLoginAndPassword(anyString(), anyString())).thenThrow(GetException.class);
        String path = "/users/credentials";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        UserDto.builder().login("x").password("x").build()
                )))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(userService).getUserByLoginAndPassword(anyString(), anyString());
    }

    @Test
    public void testGetByCredentialsBadRequestOnNullValues() throws Exception {
        when(userService.getUserByLoginAndPassword(null, null)).thenThrow(GetException.class);
        String path = "/users/credentials";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new UserDto()
                )))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testAddUserReturns() throws Exception {
        UserDto userDto = UserDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("test")
                .email("email")
                .build();
        UserViewDto userViewDto = UserViewDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .email("email")
                .build();
        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("test")
                .email("email")
                .build();
        when(modelMapper.map(user, UserViewDto.class)).thenReturn(userViewDto);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userService.add(user)).thenReturn(user);
        String path = "/users";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("email"));
        verify(userService).add(user);
    }

    @Test
    public void testAddUserIsNotAcceptableAddException() throws Exception {
        when(userService.add(any(User.class))).thenThrow(AddException.class);
        when(modelMapper.map(new UserDto(), User.class))
                .thenReturn(new User());
        String path = "/users";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(new UserDto())))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testModifyUserReturns() throws Exception {
        UserDto userDto = UserDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("test")
                .email("email")
                .build();
        UserViewDto userViewDto = UserViewDto.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .email("email")
                .build();
        User user = User.builder()
                .id("1")
                .name("test")
                .lastName("lastName")
                .login("login")
                .password("test")
                .email("email")
                .build();
        when(modelMapper.map(user, UserViewDto.class)).thenReturn(userViewDto);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userService.modify(any(User.class))).thenReturn(user);
        String path = "/users/" + user.getId();
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.lastName").value("lastName"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("email"));
        verify(userService).modify(user);
    }

    @Test
    public void testModifyUserIsConflictModifyException() throws Exception {
        when(userService.modify(any(User.class))).thenThrow(ModifyException.class);
        String path = "/users/1";
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(new UserDto())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        doNothing().when(userService).delete(anyString());
        String path = "/users/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).delete(anyString());
    }

    @Test
    public void testDeleteUserIsBadRequestDeleteException() throws Exception {
        doThrow(DeleteException.class).when(userService).delete(anyString());
        String path = "/users/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
