package net.fp.backBook.services;

import net.fp.backBook.authentication.AuthenticationFacade;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDetectionServiceImpl implements UserDetectionService {

    private AuthenticationFacade authenticationFacade;

    private UserRepository userRepository;

    @Autowired
    public UserDetectionServiceImpl(AuthenticationFacade authenticationFacade, UserRepository userRepository) {
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
    }

    @Override
    public User getAuthenticatedUser() {
        String login = this.authenticationFacade.getAuthentication().getName();
        return this.userRepository.findByLogin(login).orElseThrow(() -> new GetException("Cannot get authenticated user"));
    }
}
