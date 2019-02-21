package net.fp.backBook.repositories;

import net.fp.backBook.model.EmailVerificationToken;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends MongoRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByToken(String token);
    List<EmailVerificationToken> findAllByExpiresBefore(LocalDateTime time, Example<EmailVerificationToken> example);
}
