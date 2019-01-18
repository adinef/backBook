package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Category;
import net.fp.backBook.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {


    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getById(String id) {
        try {
            return this.categoryRepository.findById(id)
                    .orElseThrow(() -> new GetException("Cannot find category by id."));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public List<Category> getAll() {
        try {
            return this.categoryRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            this.categoryRepository.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DeleteException(e);
        }
    }

    @Override
    public Category add(Category category) {
        try {
            return this.categoryRepository.insert(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AddException(e);
        }
    }

    @Override
    public Category modify(Category category) {
        if(category.getId() == null)
            throw new ModifyException("Id cannot be null for a category to be modified.");
        try {
            return this.categoryRepository.save(category);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ModifyException(e);
        }
    }

    @Override
    public Category getByName(String name) {
        try {
            return this.categoryRepository.findByName(name)
                    .orElseThrow(() -> new GetException("Cannot find category by name."));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GetException(e);
        }
    }
}
