package net.fp.backBook.services;

import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Category;
import net.fp.backBook.repositories.CategoryRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void addTestSuccess() {
        Category category = mock(Category.class);

        when(this.categoryRepository.insert(category)).thenReturn(category);

        Assert.assertEquals(category, this.categoryService.add(category));
        verify(this.categoryRepository, times(1)).insert(category);
    }

    @Test(expected = AddException.class)
    public void addTestFailure() {
        Category category = mock(Category.class);

        when(this.categoryRepository.insert(category)).thenThrow(RuntimeException.class);

        this.categoryService.add(category);
    }

    @Test
    public void modifyTestSuccess() {
        Category category = Category.builder().id("1").build();

        when(this.categoryRepository.save(category)).thenReturn(category);

        Assert.assertEquals(category, this.categoryService.modify(category));
        verify(this.categoryRepository, times(1)).save(category);
    }

    @Test(expected = ModifyException.class)
    public void modifyTestFailureIdNull() {
        Category category = Category.builder().id(null).build();

        this.categoryService.modify(category);
    }

    @Test(expected = ModifyException.class)
    public void modifyTestFailureDatabaseException() {
        Category category = Category.builder().id("1").build();

        when(this.categoryRepository.save(any(Category.class))).thenThrow(RuntimeException.class);

        this.categoryService.modify(category);
    }

    @Test
    public void getAllTestSuccess() {
        List<Category> counterOffers = Arrays.asList(mock(Category.class), mock(Category.class));

        when(this.categoryRepository.findAll()).thenReturn(counterOffers);

        Assert.assertEquals(counterOffers, this.categoryService.getAll());
        verify(this.categoryRepository, times(1)).findAll();
    }

    @Test(expected = GetException.class)
    public void getAllTestFailure() {
        when(this.categoryRepository.findAll()).thenThrow(RuntimeException.class);

        this.categoryService.getAll();
    }

    @Test
    public void getByIdTestSuccess() {
        Category category = mock(Category.class);

        when(this.categoryRepository.findById(anyString())).thenReturn(Optional.of(category));

        Assert.assertEquals(category, this.categoryService.getById(anyString()));
        verify(this.categoryRepository, times(1)).findById(anyString());
    }

    @Test(expected = GetException.class)
    public void getByIdTestFailureNotPresent() {
        when(this.categoryRepository.findById(anyString())).thenReturn(Optional.empty());

        this.categoryService.getById(anyString());
    }

    @Test(expected = GetException.class)
    public void getByIdTestFailureDatabaseException() {
        when(this.categoryRepository.findById(anyString())).thenThrow(RuntimeException.class);

        this.categoryService.getById(anyString());
    }

    @Test
    public void deleteTestSuccess() {
        Category category = Category.builder().id("1").build();

        doNothing().when(this.categoryRepository).deleteById(category.getId());

        this.categoryService.delete(category.getId());
        verify(this.categoryRepository, times(1)).deleteById(category.getId());
    }

    @Test(expected = DeleteException.class)
    public void deleteTestFailure() {
        Category category = Category.builder().id("1").build();

        doThrow(RuntimeException.class).when(this.categoryRepository).deleteById(category.getId());

        this.categoryService.delete(category.getId());
    }

    @Test
    public void getByNameTestSuccess() {
        Category category = mock(Category.class);

        when(this.categoryRepository.findByName(anyString())).thenReturn(Optional.of(category));

        Assert.assertEquals(category, this.categoryService.getByName(anyString()));
        verify(this.categoryRepository, times(1)).findByName(anyString());
    }

    @Test(expected = GetException.class)
    public void getByNameTestFailureNotPresent() {
        when(this.categoryRepository.findByName(anyString())).thenReturn(Optional.empty());

        this.categoryService.getByName(anyString());
    }

    @Test(expected = GetException.class)
    public void getByNameTestFailureDatabaseException() {
        when(this.categoryRepository.findByName(anyString())).thenThrow(RuntimeException.class);

        this.categoryService.getByName(anyString());
    }
}
