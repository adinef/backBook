package net.fp.backBook.controllers;

import net.fp.backBook.dtos.Credentials;
import net.fp.backBook.dtos.TokenDto;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.AuthenticationException;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.services.RoleService;
import net.fp.backBook.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            value = ""
    )
    public void register(@RequestBody UserDto userDto) {
        try {
            User user = this.modelMapper.map(userDto, User.class);
            Role role = this.roleService.getByName("ROLE_USER");
            user.setRoles(Collections.singletonList(role));
            this.userService.add(user);
        } catch (Exception e) {
            throw new AddException("Registration failed. " + e.getMessage());
        }
    }
}
