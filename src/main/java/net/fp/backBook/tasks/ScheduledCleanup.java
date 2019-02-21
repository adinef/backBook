package net.fp.backBook.tasks;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.EmailVerificationTokenRepository;
import net.fp.backBook.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ScheduledCleanup {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final Example<EmailVerificationToken> matchExample;

    @Autowired
    public ScheduledCleanup(
            final UserRepository userRepository,
            final EmailVerificationTokenRepository verificationTokenRepository
    ) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        User matchUser = User
                .builder()
                .enabled(false)
                .build();
        EmailVerificationToken matchToken = EmailVerificationToken
                .builder()
                .user(matchUser)
                .build();
        matchExample = Example.of(matchToken);
    }

    @Scheduled(fixedDelay = 86400000) //once everyday
    public void clearDisabledUsersAndTokens() {
        log.info("Starting standard tokens cleanup procedure.");
        try {
            List<EmailVerificationToken> tokens =
                    verificationTokenRepository.findAllByExpiresBefore(LocalDateTime.now(), matchExample);

            verificationTokenRepository.deleteAll(tokens);
            List<User> users = tokens
                    .stream()
                    .map(EmailVerificationToken::getUser)
                    .collect(Collectors.toList());
            userRepository.deleteAll(users);
            int tokensCount = tokens.size();
            int usersCount = users.size();
            log.info("Finished cleanup procedure. Deleted " + usersCount + " users and " + tokensCount + " tokens.");
        } catch (final Exception e) {
            log.error("Error during cleanup procedure: " + e.getMessage());
        }
    }
}
