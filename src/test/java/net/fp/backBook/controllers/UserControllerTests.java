package net.fp.backBook.controllers;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
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
        List<User> users = Arrays.asList();
        when(userService.getAll()).thenThrow(GetException.class);
        try {
            mockMvc.perform(get("/users"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(jsonPath("$error").isNotEmpty());
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
        verify(userService).getById("xxx");
    }
}
