package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.repositories.EmailVerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService{

    private final EmailVerificationTokenRepository tokenRepository;

    public EmailVerificationTokenServiceImpl(
            final EmailVerificationTokenRepository tokenRepository
    ) {
        this.tokenRepository = tokenRepository;
    }

    public EmailVerificationToken getVerificationTokenByRequested(String token) {
        try {
            return this.tokenRepository
                    .findByToken(token)
                    .orElseThrow(() -> new GetException("No token found for requested token body.") );
        } catch (final Exception e) {
            log.error(e.getMessage());
            throw new GetException("Could not retrieve token. " + e.getMessage());
        }
    }

    @Override
    public EmailVerificationToken getById(String id) {
        try {
            return this.tokenRepository
                    .findById(id)
                    .orElseThrow(() -> new GetException("No token found by id.") );
        } catch (final Exception e) {
            log.error(e.getMessage());
            throw new GetException("Could not retrieve token. " + e.getMessage());
        }
    }

    @Override
    public List<EmailVerificationToken> getAll() {
        try {
            List<EmailVerificationToken> tokens = this.tokenRepository.findAll();
            if(tokens == null) {
                throw new GetException("No tokens found.");
            }
            return tokens;
        } catch (final Exception e) {
            log.error(e.getMessage());
            throw new GetException("Could not retrieve tokens. " + e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        try {
            this.tokenRepository.deleteById(id);
        } catch (final Exception e) {
            log.error(e.getMessage());
            throw new DeleteException("Could not delete token. " + e.getMessage());
        }
    }

    @Override
    public EmailVerificationToken add(EmailVerificationToken token) {
        try {
            return this.tokenRepository.insert(token);
        } catch (final Exception e) {
            log.error(e.getMessage());
            throw new AddException("Could not add token. " + e.getMessage());
        }
    }

    @Override
    public EmailVerificationToken modify(EmailVerificationToken token) {
        try {
            return this.tokenRepository.save(token);
        } catch (final Exception e) {
            log.error(e.getMessage());
            throw new ModifyException("Could not modify token. " + e.getMessage());
        }
    }
}
