package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Role;
import net.fp.backBook.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        try {
            return this.roleRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public Role getRole(String id) {
        try {
            return this.roleRepository.findById(id).orElseThrow( () -> new GetException("No role with specified id"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public Role addRole(Role role) {
        try {
            return this.roleRepository.insert(role);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AddException(e);
        }
    }

    @Override
    public Role updateRole(Role role) {
        if(role.getId() == null)
            throw new ModifyException("Id cannot be null for role to be modified.");
        try {
            return this.roleRepository.save(role);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ModifyException(e);
        }
    }

    @Override
    public void deleteRole(String id) {
        try {
            this.roleRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DeleteException(e);
        }
    }
}
