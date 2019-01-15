package net.fp.backBook.services;

import net.fp.backBook.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends BasicCrudService<User, String> {
    User getUserByLogin(String login);
    User getUserByLoginAndPassword(String login, String password);
    User getUserByEmail(String email);
}
