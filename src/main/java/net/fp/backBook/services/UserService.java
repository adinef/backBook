package net.fp.backBook.services;

import net.fp.backBook.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User getUserById(String id);
    List<User> getAllUsers();
    void deleteUser(String id);
    User addUser(User user);
    User updateUser(User user);
    User getUserByLogin(String login);
    User getUserByLoginAndPassword(String login, String password);
    User getUserByEmail(String email);
}
