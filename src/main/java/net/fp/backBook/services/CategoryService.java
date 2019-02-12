package net.fp.backBook.services;

import net.fp.backBook.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService extends BasicCrudService<Category, String> {
    Category getById(String id);

    List<Category> getAll();

    void delete(String id);

    Category add(Category category);

    Category modify(Category category);

    Category getByName(String name);
}
