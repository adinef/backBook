package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getById(String id) {
        try {
            return userRepository.findById(id).orElseThrow( () -> new GetException("Cannot find user by id.") );
        } catch (final Exception e) {
            log.error("Error during getting User object by id, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        try {
            return userRepository.findAll();
        } catch (final Exception e) {
            log.error("Error during getting User objects, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public Page<User> getUsersByPage( int page, int limit) {
        try {
            return userRepository.findAll(PageRequest.of(page, limit));
        } catch (final Exception e) {
            log.error("Error during getting User objects by page, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            userRepository.deleteById(id);
        } catch (final Exception e) {
            log.error("Error during deleting User object, {}", e);
            throw new DeleteException(e.getMessage());
        }
    }

    @Override
    public User add(User user) {
        try {
            if(userRepository.existsByLoginOrEmail(user.getLogin(), user.getEmail())) {
                throw new AddException("User with that e-mail or login already exists.");
            }
            userRepository.insert(user);
        } catch (final Exception e) {
            log.error("Error during inserting User object, {}", e);
            throw new AddException(e.getMessage());
        }
        return user;
    }

    @Override
    public User modify(User user) {
        if(user.getId() == null)
            throw new ModifyException("Id cannot be null for user to be modified.");
        try {
            User fetched = userRepository.findByLogin(user.getLogin()).orElse(null);
            if(fetched != null && !fetched.getId().equals(user.getId()))
                throw new ModifyException("User with that login already exists.");
            fetched = userRepository.findByEmail(user.getEmail()).orElse(null);
            if(fetched != null && !fetched.getId().equals(user.getId()))
                throw new ModifyException("User with that e-mail already exists.");
            userRepository.save(user);
        } catch (final Exception e) {
            log.error("Error during saving User object, {}", e);
            throw new ModifyException(e.getMessage());
        }
        return user;
    }

    @Override
    public User getUserByLogin(String login) {
        try {
            return userRepository
                    .findByLogin(login)
                    .orElseThrow( ()-> new GetException("Cannot find user by login."));
        } catch (final Exception e) {
            log.error("Error during getting User objects by Login, {}", e);
            throw new GetException(e.getMessage());
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return userRepository
                    .findByEmail(email)
                    .orElseThrow( () -> new GetException("Cannot find user by email."));
        } catch (final Exception e) {
            log.error("Error during getting User objects by e-mail, {}", e);
            throw new GetException(e.getMessage());
        }
    }
}
