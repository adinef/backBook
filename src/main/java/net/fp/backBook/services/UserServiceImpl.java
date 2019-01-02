package net.fp.backBook.services;

import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(String id) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return userRepository.findById(id).orElseThrow( () -> new Exception("Cannot find user by id.") );
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return userRepository.findAll();
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteUser(String id) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            userRepository.deleteById(id);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
        }
    }

    @Override
    public User addUser(User user) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            userRepository.insert(user);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            userRepository.save(user);
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User getUserByLogin(String login) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return userRepository
                    .findByLogin(login)
                    .orElseThrow( ()-> new Exception("Cannot find user by login."));
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUserByLoginAndPassword(String login, String password) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return userRepository
                    .findByLoginAndPassword(login, password)
                    .orElseThrow( () -> new Exception("Cannot find user by login and password."));
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            //THROW CUSTOM EXCEPTION HERE
            return userRepository
                    .findByEmail(email)
                    .orElseThrow( () -> new Exception("Cannot find user by email."));
        } catch (final Exception e) {
            //THROW CUSTOM EXCEPTION HERE
            e.printStackTrace();
            return null;
        }
    }
}
