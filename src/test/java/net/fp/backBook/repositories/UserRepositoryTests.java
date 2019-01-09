package net.fp.backBook.repositories;

import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ComponentScan(basePackageClasses = {UserRepository.class})
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        userRepository.deleteAll();
    }

    @After
    public void tearDown() {
        setUp();
    }

    @Test
    public void testFindByLogin() {
        userRepository.insert( User.builder().login("test").build() );
        User fetched = userRepository.findByLogin("test").get();
        Assert.assertNotNull(fetched);
        Assert.assertEquals("test", fetched.getLogin());
    }

    @Test
    public void testFindByEmail() {
        userRepository.insert( User.builder().email("hello@world.com").build() );
        User fetched = userRepository.findByEmail("hello@world.com").get();
        Assert.assertNotNull(fetched);
        Assert.assertEquals("hello@world.com", fetched.getEmail());
    }

    @Test
    public void testFindByLoginAndPassword() {
        userRepository.insert( User.builder().login("test").password("pass").build() );
        User fetched = userRepository.findByLoginAndPassword("test", "pass").get();
        Assert.assertNotNull(fetched);
        Assert.assertEquals("test", fetched.getLogin());
        Assert.assertEquals("pass", fetched.getPassword());
    }
}
