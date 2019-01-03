package net.fp.backBook.security.service;

import net.fp.backBook.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Primary
@Service
public class BasicUserDetailsService implements UserDetailsService {

    private UserService userService;

    @Autowired
    public BasicUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userService.getUserByLogin(username);
        } catch (Exception e) {
            String message = new StringBuilder()
                    .append("User with username: ")
                    .append(username)
                    .append(" not found").toString();
            throw new UsernameNotFoundException(message);
        }
    }
}
