package net.fp.backBook.controllers;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.dtos.CategoryDto;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Category;
import net.fp.backBook.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    private CategoryService categoryService;

    private ModelMapper modelMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody CategoryDto categoryDto) {
        Category category = this.modelMapper.map(categoryDto, Category.class);
        category = this.categoryService.add(category);
        return this.modelMapper.map(category, CategoryDto.class);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto modifyCategory(@PathVariable("id") String id, @RequestBody CategoryDto categoryDto) {
        if (!categoryDto.getId().equals(id)) {
            throw new ModifyException("Ids are not the same");
        } else {
            Category category = this.modelMapper.map(categoryDto, Category.class);
            category = this.categoryService.modify(category);
            return this.modelMapper.map(category, CategoryDto.class);
        }
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable("id") String id) {
        Category category = this.categoryService.getById(id);
        return this.modelMapper.map(category, CategoryDto.class);
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = this.categoryService.getAll();
        return this.getDtosList(categories);
    }

    @DeleteMapping(
            value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(@PathVariable("id") String id) {
        this.categoryService.delete(id);
    }

    @GetMapping(
            value = "/name/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryByName(@PathVariable("name") String name) {
        Category category = this.categoryService.getByName(name);
        return this.modelMapper.map(category, CategoryDto.class);
    }

    private List<CategoryDto> getDtosList(List<Category> categories) {
        List<CategoryDto> categoryDtos = new LinkedList<>();
        categories.forEach(category -> categoryDtos.add(this.modelMapper.map(category, CategoryDto.class)));
        return categoryDtos;
    }
}
