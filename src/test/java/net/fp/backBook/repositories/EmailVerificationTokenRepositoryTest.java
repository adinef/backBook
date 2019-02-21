package net.fp.backBook.repositories;

import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataMongoTest
@ComponentScan(basePackages = {"net.fp.backBook.repositories"})
public class EmailVerificationTokenRepositoryTest {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        this.clean();
    }

    @After
    public void tearDown() {
        this.clean();
    }

    private void clean() {
        this.tokenRepository.deleteAll();
    }

    @Test
    public void testFindByToken() {
        EmailVerificationToken token = EmailVerificationToken
                .builder()
                .token("abcdefg")
                .build();


        token = this.tokenRepository.insert(token);

        Optional<EmailVerificationToken> optionalToken = this.tokenRepository.findByToken("abcdefg");
        Assert.assertTrue(optionalToken.isPresent());
        Assert.assertEquals(token, optionalToken.get());
    }

    @Test
    public void testFindByTokenFails() {
        Optional<EmailVerificationToken> optionalToken = this.tokenRepository.findByToken("xd");
        Assert.assertFalse(optionalToken.isPresent());
    }

    @Test
    public void testFindAllByExpiresBefore() {
        User user = User
                .builder()
                .enabled(false)
                .build();
        User user2 = User
                .builder()
                .enabled(false)
                .build();

        user = userRepository.insert(user);
        user2 = userRepository.insert(user2);

        EmailVerificationToken token = EmailVerificationToken
                .builder()
                .expires(LocalDateTime.now().plusMinutes(2))
                .user(user)
                .build();
        EmailVerificationToken token2 = EmailVerificationToken
                .builder()
                .user(user2)
                .expires(LocalDateTime.now().minusMinutes(1))
                .build();
        Example<EmailVerificationToken> exmaple = Example.of(token);

        tokenRepository.insert(Arrays.asList(token, token2));

        List<EmailVerificationToken> retrievedTokens =
                tokenRepository.findAllByExpiresBefore(LocalDateTime.now(), exmaple);

        Assert.assertEquals(retrievedTokens.size(), 1);
    }
}
