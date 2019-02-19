package net.fp.backBook.services;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/*
 * @author Adrian Fijalkowski
 */

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
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
        verify(this.userRepository).findById(anyString());
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
        verify(this.userRepository).findAll();
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
        verify(this.userRepository).findByLogin(anyString());
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
        verify(this.userRepository).findByEmail(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByEmailThrowsOnRuntimeException() {
        when(this.userRepository.findByEmail(anyString())).thenThrow(RuntimeException.class);
        this.userService.getUserByEmail(anyString());
    }

    @Test
    public void testGetByPage() {
        User user = mock(User.class);
        Page<User> page = new PageImpl<>(Arrays.asList(user));
        PageRequest pageRequest = PageRequest.of(1, 2);
        when(this.userRepository.findAll(pageRequest)).thenReturn(page);
        Assert.assertEquals(page, this.userService.getUsersByPage(1, 2));
        verify(this.userRepository).findAll(pageRequest);
    }

    @Test(expected = GetException.class)
    public void testGetByPageThrowsOnRuntimeException() {
        when(this.userRepository.findAll(any(PageRequest.class))).thenThrow(RuntimeException.class);
        this.userService.getUsersByPage(1, 2);
    }

    @Test
    public void testAddUser() {
        User user = mock(User.class);
        when(this.userRepository.insert(user)).thenReturn(user);
        Assert.assertEquals(user, this.userService.add(user));
        verify(this.userRepository).insert(user);
    }

    @Test(expected = AddException.class)
    public void testAddUserThrowsGetOnRuntimeException() {
        User user = mock(User.class);
        when(this.userRepository.insert(user)).thenThrow(RuntimeException.class);
        this.userService.add(user);
    }

    @Test(expected = AddException.class)
    public void testAddUserThrowsGetOnUserWithLoginOrEmailExists() {
        User user = User.builder().login("test").email("email").build();
        when(this.userRepository.existsByLoginOrEmail(user.getLogin(), user.getEmail())).thenReturn(true);
        this.userService.add(user);
    }

    @Test
    public void testDeleteUser() {
        User user = User.builder().id("1").build();
        doNothing().when(this.userRepository).deleteById(user.getId());
        this.userService.delete(user.getId());
        verify(this.userRepository).deleteById(user.getId());
    }

    @Test(expected = DeleteException.class)
    public void testDeleteUserThrowsOnRuntimeException() {
        User user = User.builder().id("1").build();
        doThrow(RuntimeException.class).when(this.userRepository).deleteById(user.getId());
        this.userService.delete(user.getId());
    }

    @Test
    public void testModifyUser() {
        User user = User.builder().id("1").build();
        when(this.userRepository.save(user)).thenReturn(user);
        when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Assert.assertEquals(user, this.userService.modify(user));
        verify(this.userRepository).save(user);
    }

    @Test
    public void testModifyUserReturnsWhenIdTheSameAndLoginChanged() {
        User user = User.builder().id("1").login("test").email("test").build();
        when(this.userRepository.existsByLoginOrEmail(user.getLogin(), user.getEmail())).thenReturn(false);
        when(this.userRepository.save(user)).thenReturn(user);
        when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        this.userService.modify(user);
    }

    @Test(expected = ModifyException.class)
    public void testModifyUserThrowsModifyOnUserWithLoginExists() {
        User user = User
                .builder()
                .id("1")
                .login("test")
                .email("test@test.com")
                .build();
        when(this.userRepository.existsByLoginOrEmail(user.getLogin(), user.getEmail())).thenReturn(true);
        this.userService.modify(user);
    }

    @Test(expected = ModifyException.class)
    public void testModifyUserThrowsGetOnUserWithEmailExists() {
        User user = User
                .builder()
                .id("1")
                .email("test@test.com")
                .login("test")
                .build();
        when(this.userRepository.existsByLoginOrEmail(user.getLogin(), user.getEmail())).thenReturn(true);
        this.userService.modify(user);
    }

    @Test(expected = ModifyException.class)
    public void testModifyUserThrowsOnIdNull() {
        User user = User.builder().build();
        Assert.assertEquals(user, this.userService.modify(user));
    }

    @Test(expected = ModifyException.class)
    public void testModifyUserThrowsOnRuntimeException() {
        User user = User
                .builder()
                .id("1")
                .login("test")
                .email("test@test.com")
                .enabled(false)
                .build();
        when(userRepository.existsByLoginOrEmail(user.getLogin(), user.getEmail())).thenReturn(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);
        this.userService.modify(user);
    }

    @Test
    public void testActivateUser() {
        User user = User.builder().id("1").build();
        when(this.userRepository.save(user)).thenReturn(user);
        when(this.userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Assert.assertEquals(user, this.userService.activate(user.getId()));
        verify(this.userRepository).save(user);
    }

    @Test(expected = ModifyException.class)
    public void testActivateUserThrowsModifyOnUserAlreadyActive() {
        User user = User
                .builder()
                .id("1")
                .login("test")
                .email("test@test.com")
                .enabled(true)
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        this.userService.activate(user.getId());
    }

    @Test(expected = ModifyException.class)
    public void testActivateUserThrowsOnIdNull() {
        User user = User.builder().build();
        Assert.assertEquals(user, this.userService.activate(user.getId()));
    }

    @Test(expected = ModifyException.class)
    public void testActivateUserThrowsOnRuntimeException() {
        User user = User
                .builder()
                .id("1")
                .login("test")
                .email("test@test.com")
                .enabled(false)
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenThrow(RuntimeException.class);
        this.userService.activate(user.getId());
    }
}
