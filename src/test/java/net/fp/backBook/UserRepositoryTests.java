package net.fp.backBook;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import de.bwaldvogel.mongo.MongoServer;
import net.fp.backBook.configuration.TestMongoConfiguration;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = {TestMongoConfiguration.class})
@ComponentScan(basePackageClasses = {UserRepository.class})
public class UserRepositoryTests {

    @Autowired
    private MongoClient client;
    @Autowired
    private MongoServer server;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindByLogin() {
        userRepository.insert( User.builder().login("test").build() );
        User fetched = userRepository.findByLogin("test").get();
        assert fetched != null;
        assert fetched.getLogin().equals("test");
    }

    @Test
    public void testFindByEmail() {
        userRepository.insert( User.builder().email("hello@world.com").build() );
        User fetched = userRepository.findByEmail("hello@world.com").get();
        assert fetched != null;
        assert fetched.getEmail().equals("hello@world.com");
    }

    @Test
    public void testFindByLoginAndPassword() {
        userRepository.insert( User.builder().login("test").password("pass").build() );
        User fetched = userRepository.findByLoginAndPassword("test", "pass").get();
        assert fetched != null;
        assert fetched.getLogin().equals("test");
        assert fetched.getPassword().equals("pass");
    }
}
