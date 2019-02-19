package net.fp.backBook.services;

import net.fp.backBook.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends BasicCrudService<User, String> {
    User getById(String id);

    List<User> getAll();

    @Secured({"ROLE_USER"})
    void delete(String id);

    User add(User offer);

    @Secured({"ROLE_USER"})
    User modify(User offer);

    User activate(String id);

    User getUserByLogin(String login);

    User getUserByEmail(String email);

    Page<User> getUsersByPage(int page, int limit);
}
