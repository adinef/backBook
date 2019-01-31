package net.fp.backBook.repositories;

import net.fp.backBook.model.Role;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataMongoTest
@ComponentScan(basePackages = {"net.fp.backBook.repositories"})
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void setUp() {
        this.clean();
    }

    @After
    public void tearDown() {
        this.clean();
    }

    private void clean() {
        this.roleRepository.deleteAll();
    }

    @Test
    public void testFindByName() {
        Role role = Role.builder()
                .name("name").build();

        role = this.roleRepository.insert(role);

        Optional<Role> optionalRole = this.roleRepository.findByName("name");
        Assert.assertTrue(optionalRole.isPresent());
        Assert.assertEquals(role, optionalRole.get());

        optionalRole = this.roleRepository.findByName("fakeName");
        Assert.assertFalse(optionalRole.isPresent());
    }
}
