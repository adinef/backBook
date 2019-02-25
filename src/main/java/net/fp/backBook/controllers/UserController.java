package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.PasswordChangeDto;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.dtos.UserViewDto;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.User;
import net.fp.backBook.services.UserService;
import org.apache.catalina.connector.Response;
import org.bouncycastle.openssl.PasswordException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(
            final ModelMapper modelMapper,
            final UserService userService,
            final PasswordEncoder passwordEncoder
            ) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public List<UserViewDto> getUsers() {
        List<User> users =  userService.getAll();
        return MapToDto(users);
    }

    @GetMapping(
            value = "p",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public Page<UserViewDto> getUsersByPage(@RequestParam("limit") int limit, @RequestParam("page") int page) {
        Page<User> usersPage =  userService.getUsersByPage(page, limit);
        return MapToPageDto(usersPage);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserViewDto getUser(@PathVariable String id) {
        User user = this.userService.getById(id);
        return MapSingleToDto(user);
    }

    @GetMapping(value = "/login/{login}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserViewDto getUserByLogin(@PathVariable String login) {
        User user = this.userService.getUserByLogin(login);
        return MapSingleToDto(user);
    }

    @GetMapping(value = "/email/{email}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserViewDto getUserByEmail(@PathVariable String email) {
        User user = this.userService.getUserByEmail(email);
        return MapSingleToDto(user);
    }

    @PutMapping(value = "/change_password/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public int changePassword(@PathVariable("id") String id, @RequestBody PasswordChangeDto passwordDto) {
        User user = userService.getById(id);
        if(passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())) {
            this.userService.updatePassword(id, this.passwordEncoder.encode(passwordDto.getNewPassword()));
        } else {
            throw new ModifyException("Can't change password, previous password doesn't match.");
        }
        return Response.SC_OK;
    }


    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserViewDto addUser(@RequestBody UserDto userDto) {
        User user = this.modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user = this.userService.add(user);
        return MapSingleToDto(user);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserViewDto updateUser(@PathVariable String id, @RequestBody UserDto userDto) {
        if(!id.equals(userDto.getId())) {
            throw new ModifyException("Unmatching ids");
        }
        User user = this.modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user = this.userService.modify(user);
        return MapSingleToDto(user);
    }

    @DeleteMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable String id) {
        userService.delete(id);
    }

    private UserViewDto MapSingleToDto(User offer) {
        return modelMapper.map(offer, UserViewDto.class);
    }

    private List<UserViewDto> MapToDto(List<User> userList) {
        List<UserViewDto> usersDto = new ArrayList<>();
        for(User user : userList) {
            UserViewDto mappedOfferDto = MapSingleToDto(user);
            usersDto.add(mappedOfferDto);
        }
        return usersDto;
    }

    private Page<UserViewDto> MapToPageDto(Page<User> usersPage) {
        List<UserViewDto> usersDto = new ArrayList<>();
        for(User user : usersPage) {
            UserViewDto mappedOfferDto = MapSingleToDto(user);
            usersDto.add(mappedOfferDto);
        }
        return new PageImpl<>(usersDto);
    }
}
