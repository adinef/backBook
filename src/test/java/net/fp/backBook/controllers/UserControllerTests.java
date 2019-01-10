package net.fp.backBook.controllers;

import net.fp.backBook.dtos.Credentials;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.User;
import net.fp.backBook.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"net.fp.backBook.repositories",
        "net.fp.backBook.services", "net.fp.backBook.controllers", "net.fp.backBook.configuration"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    private MockMvc mockMvc;

    @Before
    public void setServerAddress() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
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
        try {
            mockMvc.perform(get("/users"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.error").isNotEmpty());
            verify(userService).getAll();
        } catch (Exception e) {

        }
    }

    @Test
    public void testGetByIdReturns() throws Exception {
        User user = User.builder().name("test").build();
        when(userService.getById(anyString())).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(UserDto.builder().name("test").build());
        String path = "/users/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name").value("test"));
        verify(userService).getById(anyString());
    }

    @Test
    public void testGetUserByIdBadRequestOnGetException() throws Exception {
        when(userService.getById(anyString())).thenThrow(GetException.class);
        try {
            String path = "/users/xxx";
            mockMvc.perform(get(path))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.error").isNotEmpty());
            verify(userService).getById(anyString());
        } catch (Exception e) {

        }
    }

    @Test
    public void testGetByLoginReturns() throws Exception {
        User user = User.builder().login("test").build();
        when(userService.getUserByLogin(anyString())).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(UserDto.builder().login("test").build());
        String path = "/users/login/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.login").value("test"));
        verify(userService).getUserByLogin(anyString());
    }

    @Test
    public void testGetByLoginBadRequestOnGetException() throws Exception {
        when(userService.getUserByLogin(anyString())).thenThrow(GetException.class);
        try {
            String path = "/users/login/xxx";
            mockMvc.perform(get(path))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.error").isNotEmpty());
            verify(userService).getUserByLogin(anyString());
        } catch (Exception e) {

        }
    }

    @Test
    public void testGetByEmailReturns() throws Exception {
        User user = User.builder().email("test").build();
        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(UserDto.builder().email("test").build());
        String path = "/users/email/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.email").value("test"));
        verify(userService).getUserByEmail(anyString());
    }

    @Test
    public void testGetByEmailBadRequestOnGetException() throws Exception {
        when(userService.getUserByEmail(anyString())).thenThrow(GetException.class);
        try {
            String path = "/users/email/xxx";
            mockMvc.perform(get(path))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.error").isNotEmpty());
            verify(userService).getUserByEmail(anyString());
        } catch (Exception e) {

        }
    }

    @Test
    public void testGetByCredentialsReturns() throws Exception {
        User user = User.builder().login("test").password("test").build();
        when(userService.getUserByLoginAndPassword(anyString(), anyString())).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(UserDto.builder().login("test").password("test").build());
        String path = "/users/credentials";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\": \"test\", \"password\": \"test\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.password").value("test"));
        verify(userService).getUserByLoginAndPassword(anyString(), anyString());
    }

    @Test
    public void testGetByCredentialsBadRequestOnGetException() throws Exception {
        when(userService.getUserByLoginAndPassword(anyString(), anyString())).thenThrow(GetException.class);
        try {
            String path = "/users/credentials";
            mockMvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"login\": \"test\", \"password\": \"test\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$.error").isNotEmpty());
            verify(userService).getUserByLoginAndPassword(anyString(), anyString());
        } catch (Exception e) {

        }
    }
}
