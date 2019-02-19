package net.fp.backBook.repositories;

import net.fp.backBook.model.EmailVerificationToken;
import net.fp.backBook.model.Role;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataMongoTest
@ComponentScan(basePackages = {"net.fp.backBook.repositories"})
public class EmailVerificationTokenRepositoryTest {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

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
}
