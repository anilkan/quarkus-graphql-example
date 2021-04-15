package xyz.anilkan.service;

import xyz.anilkan.entity.Category;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
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

    public Category addCategory(Category category) {
        category.setId(UUID.randomUUID());
        categoryList.add(category);

        return category;
    }

    public boolean deleteCategory (UUID id) {
        return categoryList.removeIf(c -> c.getId().equals(id));
    }

    public Category updateCategory (UUID id, Category category) {
        int index = categoryList.indexOf(categoryList.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null));
        category.setId(id);
        categoryList.set(index, category);

        return category;
    }
}
