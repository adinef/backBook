package net.fp.backBook.services;

import net.fp.backBook.model.User;
import org.springframework.security.access.annotation.Secured;

public interface UserDetectionService {

    @Secured({"ROLE_USER"})
    User getAuthenticatedUser();
}
