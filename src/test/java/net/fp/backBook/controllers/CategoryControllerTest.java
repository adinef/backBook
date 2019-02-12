package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.CategoryDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Category;
import net.fp.backBook.services.CategoryService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    @Autowired
    private ModelMapper modelMapper;

    @Mock
    private ModelMapper modelMapperMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setControllerAdvice(restResponseExceptionHandler).build();
    }

    @Test
    public void addCategorySuccess() throws Exception {

        Category category = Category.builder()
                .id("1")
                .name("name").build();

        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);

        when(this.categoryService.add(category)).thenReturn(category);
        when(this.modelMapperMock.map(any(CategoryDto.class), eq(Category.class))).thenReturn(category);
        when(this.modelMapperMock.map(any(Category.class), eq(CategoryDto.class))).thenReturn(categoryDto);

        this.mockMvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void addCategoryFailure() throws Exception {

        Category category = Category.builder()
                .id("1")
                .name("name").build();

        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);

        when(this.modelMapperMock.map(any(CategoryDto.class), eq(Category.class))).thenReturn(category);
        when(this.categoryService.add(category)).thenThrow(AddException.class);

        this.mockMvc.perform(
                post("/categories")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void modifyCategorySuccess() throws Exception {

        Category category = Category.builder()
                .id("1")
                .name("name").build();

        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);

        when(this.categoryService.modify(category)).thenReturn(category);
        when(this.modelMapperMock.map(any(CategoryDto.class), eq(Category.class))).thenReturn(category);
        when(this.modelMapperMock.map(any(Category.class), eq(CategoryDto.class))).thenReturn(categoryDto);

        this.mockMvc.perform(
                put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void modifyCategoryWrongIdFailure() throws Exception {

        Category category = Category.builder()
                .id("1")
                .name("name").build();

        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);

        this.mockMvc.perform(
                put("/categories/2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void modifyCategoryDatabaseExceptionFailure() throws Exception {

        Category category = Category.builder()
                .id("1")
                .name("name").build();

        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);

        when(this.categoryService.modify(category)).thenThrow(ModifyException.class);
        when(this.modelMapperMock.map(any(CategoryDto.class), eq(Category.class))).thenReturn(category);

        this.mockMvc.perform(
                put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getAllCategoriesSuccess() throws Exception {

        Category category1 = Category.builder()
                .id("1")
                .name("name1").build();

        Category category2 = Category.builder()
                .id("2")
                .name("name2").build();

        CategoryDto categoryDto1 = this.modelMapper.map(category1, CategoryDto.class);
        CategoryDto categoryDto2 = this.modelMapper.map(category2, CategoryDto.class);

        when(this.categoryService.getAll()).thenReturn(Lists.list(category1, category2));
        when(this.modelMapperMock.map(category1, CategoryDto.class)).thenReturn(categoryDto1);
        when(this.modelMapperMock.map(category2, CategoryDto.class)).thenReturn(categoryDto2);

        this.mockMvc.perform(
                get("/categories"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("name2"));
    }

    @Test
    public void getAllCategoriesFailure() throws Exception {

        when(this.categoryService.getAll()).thenThrow(GetException.class);

        this.mockMvc.perform(
                get("/categories"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getCategoryByIdSuccess() throws Exception {

        Category category = Category.builder()
                .id("1")
                .name("name").build();

        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);

        when(this.categoryService.getById(category.getId())).thenReturn(category);
        when(this.modelMapperMock.map(category, CategoryDto.class)).thenReturn(categoryDto);

        this.mockMvc.perform(
                get("/categories/" + category.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void getCategoryByIdFailure() throws Exception {

        String id = "1";

        when(this.categoryService.getById(id)).thenThrow(GetException.class);

        this.mockMvc.perform(
                get("/categories/" + id))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void deleteCategorySuccess() throws Exception {

        String id = "1";

        doNothing().when(this.categoryService).delete(id);

        this.mockMvc.perform(
                delete("/categories/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void deleteCategoryFailure() throws Exception {

        String id = "1";

        doThrow(DeleteException.class).when(this.categoryService).delete(id);

        this.mockMvc.perform(
                delete("/categories/" + id))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    public void getCategoryByNameSuccess() throws Exception {

        Category category = Category.builder()
                .id("1")
                .name("name").build();

        CategoryDto categoryDto = this.modelMapper.map(category, CategoryDto.class);

        when(this.categoryService.getByName(category.getName())).thenReturn(category);
        when(this.modelMapperMock.map(category, CategoryDto.class)).thenReturn(categoryDto);

        this.mockMvc.perform(
                get("/categories/name/" + category.getName()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void getCategoryByNameFailure() throws Exception {

        String name = "name";

        when(this.categoryService.getByName(name)).thenThrow(GetException.class);

        this.mockMvc.perform(
                get("/categories/name/" + name))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
