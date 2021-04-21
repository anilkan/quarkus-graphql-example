package xyz.anilkan.graphql.resource;

import io.smallrye.graphql.api.Context;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.entity.Category;
import xyz.anilkan.graphql.input.create.CreateCategoryInput;
import xyz.anilkan.graphql.input.update.UpdateCategoryInput;
import xyz.anilkan.graphql.payload.create.CreateCategoryPayload;
import xyz.anilkan.graphql.payload.delete.DeleteCategoryPayload;
import xyz.anilkan.graphql.payload.update.UpdateCategoryPayload;
import xyz.anilkan.service.CategoryService;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@GraphQLApi
public class CategoryResource {

    @Inject
    Context context;

    @Inject
    CategoryService categoryService;

    @Query("categories")
    @Description("Get all categories.")
    public Uni<List<Category>> getAllCategories() {
        return categoryService.getAllCategories().collect().asList();
    }

    @Query("category")
    @Description("Find category by id.")
    public Uni<Category> getCategory(@NonNull @Name("id") UUID id) {
        return categoryService.findById(id);
    }

    @Mutation("createCategory")
    @Description("Create new category.")
    public Uni<CreateCategoryPayload> createCategory(@NonNull @Name("input") CreateCategoryInput input) {
        final Category category = new Category();
        category.setName(input.getName());

        return categoryService.createCategory(category)
                .onItem().transform(CreateCategoryPayload::new);
    }

    @Mutation("updateCategory")
    @Description("Update category.")
    public Uni<UpdateCategoryPayload> updateCategory(@NonNull @Name("input") UpdateCategoryInput input) {
        final LinkedHashMap<String, Object> inputArgs = context.getArgument("input");

        return categoryService.findById(input.getId())
                .onItem().transform(c -> {
                    if (inputArgs.containsKey("name"))
                        c.setName(input.getName());

                    return c;
        })
                .onItem().transformToUni(c -> categoryService.updateCategory(c))
                .onItem().transform(UpdateCategoryPayload::new);
    }

    @Mutation("deleteCategory")
    @Description("Delete category")
    public Uni<DeleteCategoryPayload> deleteCategory(@NonNull @Name("id") UUID id) {
        return categoryService.deleteCategory(id)
                .onItem().transform(DeleteCategoryPayload::new);
    }
}
