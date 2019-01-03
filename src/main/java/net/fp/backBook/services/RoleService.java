package net.fp.backBook.services;

import net.fp.backBook.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    List<Role> getAllRoles();
    Role getRole(String id);
    Role addRole(Role role);
    Role updateRole(Role role);
    void deleteRole(String id);
}
