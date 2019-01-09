package net.fp.backBook.services;

import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import net.fp.backBook.services.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"net.fp.backBook.repositories",
        "net.fp.backBook.services"})
@SpringBootTest
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testGetById() {
        User user = mock(User.class);
        when(this.userRepository.findById(anyString())).thenReturn(Optional.of(user));
        Assert.assertEquals(user, this.userService.getById(anyString()));
        verify(this.userRepository, times(1)).findById(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByIdThrowsOnRuntimeException() {
        when(this.userRepository.findById(anyString())).thenThrow(RuntimeException.class);
        this.userService.getById(anyString());
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(mock(User.class), mock(User.class));
        when(this.userRepository.findAll()).thenReturn(users);
        Assert.assertEquals(users, this.userService.getAll());
        verify(this.userRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllUsersReturnEmpty() {
        List<User> users = Collections.emptyList();
        when(this.userRepository.findAll()).thenReturn(users);
        Assert.assertEquals(users, this.userService.getAll());
        verify(this.userRepository, times(1)).findAll();
    }

    @Test(expected = GetException.class)
    public void testGetAllUsersThrowsOnRuntimeException() {
        when(this.userRepository.findAll()).thenThrow(RuntimeException.class);
        this.userService.getAll();
    }


    @Test
    public void testGetByLogin() {
        User user = mock(User.class);
        when(this.userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));
        Assert.assertEquals(user, this.userService.getUserByLogin(anyString()));
        verify(this.userRepository, times(1)).findByLogin(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByLoginThrowsOnRuntimeException() {
        when(this.userRepository.findByLogin(anyString())).thenThrow(RuntimeException.class);
        this.userService.getUserByLogin(anyString());
    }

    @Test
    public void testGetByEmail() {
        User user = mock(User.class);
        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        Assert.assertEquals(user, this.userService.getUserByEmail(anyString()));
        verify(this.userRepository, times(1)).findByEmail(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByEmailThrowsOnRuntimeException() {
        when(this.userRepository.findByEmail(anyString())).thenThrow(RuntimeException.class);
        this.userService.getUserByEmail(anyString());
    }

    @Test
    public void testGetByLoginAndPassword() {
        User user = mock(User.class);
        when(this.userRepository.findByLoginAndPassword(anyString(), anyString())).thenReturn(Optional.of(user));
        Assert.assertEquals(user, this.userService.getUserByLoginAndPassword(anyString(), anyString()));
        verify(this.userRepository, times(1)).findByLoginAndPassword(anyString(), anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByLoginAndPasswordThrowsOnRuntimeException() {
        when(this.userRepository.findByLoginAndPassword(anyString(), anyString())).thenThrow(RuntimeException.class);
        this.userService.getUserByLoginAndPassword(anyString(), anyString());
    }

}
