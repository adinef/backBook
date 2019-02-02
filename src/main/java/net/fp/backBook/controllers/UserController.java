package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.Credentials;
import net.fp.backBook.dtos.UserDto;
import net.fp.backBook.dtos.UserViewDto;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.User;
import net.fp.backBook.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private ModelMapper modelMapper;

    @Autowired
    public UserController(
            final ModelMapper modelMapper,
            final UserService userService
    ) {
        this.modelMapper = modelMapper;
        this.userService = userService;
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

    @PostMapping(value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserViewDto addUser(@RequestBody UserDto userDto) {
        User user = this.modelMapper.map(userDto, User.class);
        // set user from context here
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
        // set user from context here
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
        Page<UserViewDto> page = new PageImpl<>(usersDto);
        return page;
    }
}
