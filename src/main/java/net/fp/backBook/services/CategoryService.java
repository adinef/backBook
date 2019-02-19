package net.fp.backBook.services;

import net.fp.backBook.model.Category;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService extends BasicCrudService<Category, String> {
    @Secured({"ROLE_USER"})
    Category getById(String id);

    @Secured({"ROLE_USER"})
    List<Category> getAll();

    @Secured({"ROLE_ADMIN"})
    void delete(String id);

    @Secured({"ROLE_ADMIN"})
    Category add(Category category);

    @Secured({"ROLE_ADMIN"})
    Category modify(Category category);

    @Secured({"ROLE_USER"})
    Category getByName(String name);
}
