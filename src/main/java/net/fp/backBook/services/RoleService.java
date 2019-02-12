package net.fp.backBook.services;

import net.fp.backBook.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService extends BasicCrudService<Role, String>{

    Role getById(String id);

    List<Role> getAll();

    void delete(String id);

    Role add(Role offer);

    Role modify(Role offer);
    
    Role getByName(String name);
}
