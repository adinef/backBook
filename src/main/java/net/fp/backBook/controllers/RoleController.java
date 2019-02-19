package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.RoleDto;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Role;
import net.fp.backBook.services.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
public class RoleController {

    private RoleService roleService;
    private ModelMapper modelMapper;

    @Autowired
    public RoleController(RoleService roleService, ModelMapper modelMapper) {
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<RoleDto> getAllRoles() {
        List<Role> roles = this.roleService.getAll();
        return MapToDto(roles);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public RoleDto getRole(@PathVariable String id) {
        Role role = this.roleService.getById(id);
        return MapSingleToDto(role);
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto addRole(@RequestBody RoleDto roleDto) {
        Role role = this.modelMapper.map(roleDto, Role.class);
        return MapSingleToDto(roleService.add(role));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public RoleDto updateRole(@PathVariable String id, @RequestBody RoleDto roleDto) {
        if(!id.equals(roleDto.getId())) {
            throw new ModifyException("Ids aren't matching.");
        }
        Role role = this.modelMapper.map(roleDto, Role.class);
        return MapSingleToDto(roleService.modify(role));
    }

    @DeleteMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public void deleteRole(@PathVariable String id) {
        this.roleService.delete(id);
    }

    private RoleDto MapSingleToDto(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

    private List<RoleDto> MapToDto(List<Role> roleList) {
        List<RoleDto> rolesDto = new ArrayList<>();
        for(Role role : roleList) {
            RoleDto mappedRoleDto = MapSingleToDto(role);
            rolesDto.add(mappedRoleDto);
        }
        return rolesDto;
    }
}
