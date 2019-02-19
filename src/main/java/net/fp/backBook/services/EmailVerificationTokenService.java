package net.fp.backBook.services;

import net.fp.backBook.model.EmailVerificationToken;
import org.springframework.stereotype.Service;

public interface EmailVerificationTokenService extends BasicCrudService<EmailVerificationToken, String> {
    EmailVerificationToken getVerificationTokenByRequested(String token);
}
