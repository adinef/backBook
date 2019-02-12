package net.fp.backBook.controllers;

import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.exceptions.RegisterException;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.services.RoleService;
import net.fp.backBook.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private UserService userService;

    private RoleService roleService;

    private ModelMapper modelMapper;

    @Autowired
    public RegisterController(UserService userService, RoleService roleService, ModelMapper modelMapper) {
        this.userService = userService;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody UserDto userDto) {
        try {
            User user = this.modelMapper.map(userDto, User.class);
            Role role = this.roleService.getByName("ROLE_USER");
            user.setRoles(Collections.singletonList(role));
            this.userService.add(user);
        } catch (Exception e) {
            throw new RegisterException("Registration failed. " + e.getMessage());
        }
    }
}
