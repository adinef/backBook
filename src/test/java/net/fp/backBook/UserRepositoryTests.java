package net.fp.backBook;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import de.bwaldvogel.mongo.MongoServer;
import net.fp.backBook.configuration.TestMongoConfiguration;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestMongoConfiguration.class})
public class UserRepositoryTests {

    private MongoCollection<User> ussersColl;
    private MongoClient client;
    private MongoServer server;

    private UserRepository userRepository;

    @Autowired
    public UserRepositoryTests(
            final UserRepository userRepository,
            final MongoClient mongoClient,
            final MongoServer mongoServer
    ) {
        this.userRepository = userRepository;
        this.client = mongoClient;
        this.server = mongoServer;
    }

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

}
