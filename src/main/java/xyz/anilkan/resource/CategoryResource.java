package xyz.anilkan.resource;

import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.entity.Category;
import xyz.anilkan.service.CategoryService;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@GraphQLApi
public class CategoryResource {

    @Inject
    CategoryService categoryService;

    @Query("categories")
    @Description("Get all categories")
    public List<Category> getAllCategories () {
        return categoryService.getAllCategories();
    }

    @Query("category")
    @Description("Get category")
    public Category getCategory(@Name("id") UUID id) {
        return categoryService.getCategory(id);
    }

    @Mutation("addCategory")
    @Description("Add category")
    public Category addCategory (Category category) {
        return categoryService.addCategory(category);
    }

    @Mutation("deleteCategory")
    @Description("Delete category")
    public boolean deleteCategory (@Name("id") UUID id) {
        return categoryService.deleteCategory(id);
    }

    @Mutation("updateCategory")
    @Description("updateCategory")
    public Category updateCategory (@Name("id") UUID id, Category category) {
        return categoryService.updateCategory(id, category);
    }
}
