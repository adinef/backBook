package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.RoleDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Role;
import net.fp.backBook.services.RoleService;
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
public class RoleControllerTests {

    @InjectMocks
    private RoleController roleController;

    @Mock
    private RoleService roleService;

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
                .standaloneSetup(roleController)
                .setControllerAdvice(restResponseExceptionHandler)
                .build();
    }

    @Test
    public void testGetAllRolesReturns() throws Exception {
        List<Role> roles = Arrays.asList(mock(Role.class), mock(Role.class));
        when(roleService.getAll()).thenReturn(roles);
        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").value(hasSize(2)));
        verify(roleService).getAll();
    }

    @Test
    public void testGetAllRolesReturnsEmptyList() throws Exception {
        List<Role> roles = Arrays.asList();
        when(roleService.getAll()).thenReturn(roles);
        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$").isEmpty());
        verify(roleService).getAll();
    }


    @Test
    public void testGetAllRolesBadRequestOnGetException() throws Exception {
        when(roleService.getAll()).thenThrow(GetException.class);
        mockMvc.perform(get("/roles"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(roleService).getAll();
    }

    @Test
    public void testGetByIdReturns() throws Exception {
        Role role = new Role("1", "role");
        RoleDto roleDto = new RoleDto("1", "role");
        when(roleService.getById("1")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);
        String path = "/roles/1";
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("role"));
        verify(roleService).getById("1");
    }

    @Test
    public void testGetByIdBadRequestOnGetException() throws Exception {
        when(roleService.getById("xxx")).thenThrow(GetException.class);
        String path = "/roles/xxx";
        mockMvc.perform(get(path))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
        verify(roleService).getById(anyString());
    }

    @Test
    public void testAddRoleReturns() throws Exception {
        Role role = new Role("1", "role");
        RoleDto roleDto = new RoleDto("1", "role");
        when(modelMapper.map(any(Role.class), eq(RoleDto.class))).thenReturn(roleDto);
        when(modelMapper.map(any(RoleDto.class), eq(Role.class))).thenReturn(role);
        when(roleService.add(role)).thenReturn(role);
        String path = "/roles";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("role"));
        verify(roleService).add(role);
    }

    @Test
    public void testAddRoleIsNotAcceptableAddException() throws Exception {
        when(roleService.add(any(Role.class))).thenThrow(AddException.class);
        when(modelMapper.map(new RoleDto(), Role.class))
                .thenReturn(new Role());
        String path = "/roles";
        mockMvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(new RoleDto())))
                .andDo(print())
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testModifyRoleReturns() throws Exception {

        Role role = new Role("1", "role");
        RoleDto roleDto = new RoleDto("1", "role");
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        when(roleService.modify(role)).thenReturn(role);
        String path = "/roles/1";
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(roleDto)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("role"));
        verify(roleService).modify(role);
    }

    @Test
    public void testModifyRoleWrongId() throws Exception {
        Role role = new Role("1", "role");
        when(modelMapper.map(new RoleDto(), Role.class)).thenReturn(role);
        String path = "/roles/2";
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(new RoleDto())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testModifyUserIsConflictModifyException() throws Exception {
        when(roleService.modify(any(Role.class))).thenThrow(ModifyException.class);
        when(modelMapper.map(new RoleDto(), Role.class))
                .thenReturn(new Role());
        String path = "/roles/1";
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(new RoleDto())))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void testDeleteRoleSuccess() throws Exception {
        doNothing().when(roleService).delete(anyString());
        String path = "/roles/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isOk());
        verify(roleService).delete(anyString());
    }

    @Test
    public void testDeleteUserIsBadRequestDeleteException() throws Exception {
        doThrow(DeleteException.class).when(roleService).delete(anyString());
        String path = "/roles/1";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
