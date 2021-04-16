package xyz.anilkan.service;

import xyz.anilkan.entity.Category;
import xyz.anilkan.graphql.input.create.CreateCategoryInput;
import xyz.anilkan.graphql.input.update.UpdateCategoryInput;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class CategoryService {

    private List<Category> categoryList = new ArrayList<>();

    public CategoryService() {
        final Category c1 = new Category();
        c1.setId(UUID.randomUUID());
        c1.setName("Category 01");
        categoryList.add(c1);
    }

    public List<Category> getAllCategories () {
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

    public Category updateCategory (UUID id, UpdateCategoryInput input) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(input);

        int index = categoryList.indexOf(categoryList.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null));

        final Category category = categoryList.get(index);
        category.setName(input.getName());

        categoryList.set(index, category);

        return category;
    }

    public boolean deleteCategory (UUID id) {
        return categoryList.removeIf(c -> c.getId().equals(id));
    }

}
