package net.fp.backBook.services;

import net.fp.backBook.model.Role;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService extends BasicCrudService<Role, String>{

    @Secured({"ROLE_USER"})
    Role getById(String id);

    @Secured({"ROLE_USER"})
    List<Role> getAll();

    @Secured({"ROLE_ADMIN"})
    void delete(String id);

    @Secured({"ROLE_ADMIN"})
    Role add(Role offer);

    @Secured({"ROLE_ADMIN"})
    Role modify(Role offer);

    @Secured({"ROLE_USER"})
    Role getByName(String name);
}
