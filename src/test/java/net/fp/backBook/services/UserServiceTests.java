package net.fp.backBook.services;

import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.UserRepository;
import net.fp.backBook.services.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"net.fp.backBook.repositories",
        "net.fp.backBook.services"})
@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private ArrayList<User> usersList;

    @Before
    public void setUp() {
        userRepository.deleteAll();

        User testUser = User.builder().name("name").lastName("lastName").password("test").build();
        User testUser2 = User.builder().name("name1").lastName("lastName1").password("test2").build();
        User testUser3 = User.builder().name("name2").lastName("lastName2").password("test").build();
        usersList = new ArrayList<>();
        usersList.add(testUser);
        usersList.add(testUser2);
        usersList.add(testUser3);
        userRepository.insert(usersList);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testGetById() {
        User user = usersList.get(0);
        User userFetched = userService.getById(user.getId());
        Assert.assertNotNull(userFetched);
        Assert.assertEquals(user.getName(), userFetched.getName());
    }

    @Test(expected = GetException.class)
    public void testGetByIdThrows() {
        userService.getById("-1");
    }

    @Test
    public void testGetAllOffers() {
        int usersSize = usersList.size();
        int fetechedUsersSize = userService.getAll().size();
        Assert.assertNotNull(fetechedUsersSize);
        Assert.assertEquals(usersSize, fetechedUsersSize);
    }

    /*
    @Test
    public void
*/

}
