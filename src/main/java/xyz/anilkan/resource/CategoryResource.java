package xyz.anilkan.resource;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.entity.Category;
import xyz.anilkan.graphql.input.create.CreateCategoryInput;
import xyz.anilkan.graphql.input.update.UpdateCategoryInput;
import xyz.anilkan.graphql.payload.create.CreateCategoryPayload;
import xyz.anilkan.graphql.payload.delete.DeleteCategoryPayload;
import xyz.anilkan.graphql.payload.update.UpdateCategoryPayload;
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
    public Category getCategory(@NonNull @Name("id") UUID id) {
        return categoryService.getCategory(id);
    }

    @Mutation("createCategory")
    @Description("Create new category.")
    public CreateCategoryPayload createCategory (@NonNull @Name("input") CreateCategoryInput input) {
        return new CreateCategoryPayload(categoryService.createCategory(input));
    }

    @Mutation("updateCategory")
    @Description("updateCategory")
    public UpdateCategoryPayload updateCategory (@NonNull @Name("id") UUID id, @NonNull @Name("input") UpdateCategoryInput input) {
        return new UpdateCategoryPayload(categoryService.updateCategory(id, input));

    }

    @Mutation("deleteCategory")
    @Description("Delete category")
    public DeleteCategoryPayload deleteCategory (@NonNull @Name("id") UUID id) {
        return new DeleteCategoryPayload(categoryService.deleteCategory(id));
    }
}
