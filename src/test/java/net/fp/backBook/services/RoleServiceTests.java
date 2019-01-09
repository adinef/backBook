package net.fp.backBook.services;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.RoleRepository;
import net.fp.backBook.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"net.fp.backBook.repositories",
        "net.fp.backBook.services"})
@SpringBootTest
public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    public void testGetById() {
        Role role = mock(Role.class);
        when(this.roleRepository.findById(anyString())).thenReturn(Optional.of(role));
        Assert.assertEquals(role, this.roleService.getById(anyString()));
        verify(this.roleRepository, times(1)).findById(anyString());
    }

    @Test(expected = GetException.class)
    public void testGetByIdThrowsOnRuntimeException() {
        when(this.roleRepository.findById(anyString())).thenThrow(RuntimeException.class);
        this.roleService.getById(anyString());
    }

    @Test
    public void testGetAllRoles() {
        List<Role> roles = Arrays.asList(mock(Role.class), mock(Role.class));
        when(this.roleRepository.findAll()).thenReturn(roles);
        Assert.assertEquals(roles, this.roleService.getAll());
        verify(this.roleRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllRolesReturnEmpty() {
        List<Role> roles = Collections.emptyList();
        when(this.roleRepository.findAll()).thenReturn(roles);
        Assert.assertEquals(roles, this.roleService.getAll());
        verify(this.roleRepository, times(1)).findAll();
    }

    @Test(expected = GetException.class)
    public void testGetAllRolesThrowsOnRuntimeException() {
        when(this.roleRepository.findAll()).thenThrow(RuntimeException.class);
        this.roleService.getAll();
    }

    @Test
    public void testAddRole() {
        Role role = mock(Role.class);
        when(this.roleRepository.insert(role)).thenReturn(role);
        Assert.assertEquals(role, this.roleService.add(role));
        verify(this.roleRepository, times(1)).insert(role);
    }

    @Test(expected = AddException.class)
    public void testAddRoleThrowsGetOnRuntimeException() {
        Role role = mock(Role.class);
        when(this.roleRepository.insert(role)).thenThrow(RuntimeException.class);
        this.roleService.add(role);
    }


    @Test
    public void testDeleteRole() {
        Role role = new Role();
        role.setId("1");
        doNothing().when(this.roleRepository).deleteById(role.getId());
        this.roleService.delete(role.getId());
        verify(this.roleRepository, times(1)).deleteById(role.getId());
    }

    @Test(expected = DeleteException.class)
    public void testDeleteRoleThrowsOnRuntimeException() {
        Role role = new Role();
        role.setId("1");
        doThrow(RuntimeException.class).when(this.roleRepository).deleteById(role.getId());
        this.roleService.delete(role.getId());
    }

    @Test
    public void testModifyRole() {
        Role role = new Role();
        role.setId("1");
        when(this.roleRepository.save(role)).thenReturn(role);
        Assert.assertEquals(role, this.roleService.modify(role));
        verify(this.roleRepository, times(1)).save(role);
    }

    @Test(expected = ModifyException.class)
    public void testModifyRoleThrowsOnIdNull() {
        Role role = new Role();
        Assert.assertEquals(role, this.roleService.modify(role));
    }

    @Test(expected = ModifyException.class)
    public void testModifyRoleThrowsOnRuntimeException() {
        Role role = mock(Role.class);
        when(roleRepository.insert(role)).thenThrow(RuntimeException.class);
        this.roleService.modify(role);
    }
}
