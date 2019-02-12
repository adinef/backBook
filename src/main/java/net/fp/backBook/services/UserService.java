package net.fp.backBook.services;

import net.fp.backBook.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends BasicCrudService<User, String> {

    User getById(String id);

    List<User> getAll();

    void delete(String id);

    User add(User offer);

    User modify(User offer);

    User getUserByLogin(String login);

    User getUserByEmail(String email);

    Page<User> getUsersByPage(int page, int limit);
}
