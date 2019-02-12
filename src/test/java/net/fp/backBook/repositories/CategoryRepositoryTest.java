package net.fp.backBook.repositories;

import net.fp.backBook.model.Category;
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
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Before
    public void setUp() {
        this.clean();
    }

    @After
    public void tearDown() {
        this.clean();
    }

    private void clean() {
        this.categoryRepository.deleteAll();
    }

    @Test
    public void testFindByName() {
        Category category = Category.builder()
                .name("name").build();

        category = this.categoryRepository.insert(category);

        Optional<Category> optionalCategory = this.categoryRepository.findByName("name");
        Assert.assertTrue(optionalCategory.isPresent());
        Assert.assertEquals(category, optionalCategory.get());

        optionalCategory = this.categoryRepository.findByName("fakeName");
        Assert.assertFalse(optionalCategory.isPresent());
    }
}
