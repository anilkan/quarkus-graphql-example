package xyz.anilkan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.anilkan.entity.Category;
import xyz.anilkan.graphql.input.create.CreateCategoryInput;
import xyz.anilkan.graphql.input.update.UpdateCategoryInput;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class CategoryService {

    private final List<Category> categoryList = new ArrayList<>();

    public CategoryService() {
        final Category c1 = new Category();
        c1.setId(UUID.randomUUID());
        c1.setName("Category 01");
        categoryList.add(c1);
    }

    public List<Category> getAllCategories() {
        return categoryList;
    }

    public Category getCategory(UUID id) {
        return categoryList.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    public Category createCategory(CreateCategoryInput input) {
        Objects.requireNonNull(input);

        final Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName(input.getName());

        categoryList.add(category);

        return category;
    }

    public Category updateCategory(UUID id, UpdateCategoryInput input) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(input);

        final ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked") final LinkedHashMap<String, Object> vars = objectMapper.convertValue(input, LinkedHashMap.class);

        return updateCategory(id, vars);
    }

    public Category updateCategory(UUID id, LinkedHashMap<String, Object> vars) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(vars);

        int index = categoryList.indexOf(categoryList.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null));

        final Category category = categoryList.get(index);

        if (vars.containsKey("name"))
            category.setName((String) vars.get("name"));

        categoryList.set(index, category);

        return category;
    }

    public boolean deleteCategory(UUID id) {
        return categoryList.removeIf(c -> c.getId().equals(id));
    }

}
