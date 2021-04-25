package xyz.anilkan.graphql.resource;

import graphql.com.google.common.base.Strings;
import graphql.relay.*;
import io.smallrye.common.constraint.Nullable;
import io.smallrye.graphql.api.Context;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.graphql.CursorHelper;
import xyz.anilkan.graphql.input.create.CreateCategoryInput;
import xyz.anilkan.graphql.input.update.UpdateCategoryInput;
import xyz.anilkan.graphql.payload.create.CreateCategoryPayload;
import xyz.anilkan.graphql.payload.delete.DeleteCategoryPayload;
import xyz.anilkan.graphql.payload.update.UpdateCategoryPayload;
import xyz.anilkan.graphql.type.Category;
import xyz.anilkan.helper.PageRequest;
import xyz.anilkan.service.CategoryService;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GraphQLApi
public class CategoryResource {

    @Inject
    Context context;

    @Inject
    CategoryService categoryService;

    @Query("categories")
    @Description("Get all categories.")
    public Uni<Connection<Category>> getAllCategory(@Nullable @Name("first") final Integer first, @Nullable @Name("after") final String after) {
        final Integer _first = first == null || first > 5 ? 5 : first;
        final UUID _after = Strings.isNullOrEmpty(after) ? null : CursorHelper.decode(after);

        return categoryService.getAllCategory(PageRequest.of(_first, _after))
                .onItem().transform(page -> {
                    List<Edge<Category>> edges = page.getResult().stream()
                            .map(Category::fromEntity)
                            .map(c -> new DefaultEdge<>(c, CursorHelper.encode(c.getId())))
                            .collect(Collectors.toList());

                    DefaultPageInfo defaultPageInfo = new DefaultPageInfo(
                            (edges.size() > 0) ? edges.get(0).getCursor() : null,
                            (edges.size() > 0) ? edges.get(edges.size() - 1).getCursor() : null,
                            page.isHasPreviousPage(),
                            page.isHasNextPage()
                    );

                    return new DefaultConnection<>(edges, defaultPageInfo);
        });
    }

    /*
    @Query("categories")
    @Description("Get all categories.")
    public Uni<List<Category>> getAllCategory() {
        return categoryService.getAllCategory()
                .onItem().transform(Category::fromEntity)
                .collect().asList();
    }
     */

    @Query("category")
    @Description("Find category by id.")
    public Uni<Category> getCategory(@NonNull @Name("id") UUID id) {
        return categoryService.findCategoryById(id)
                .onItem().transform(Category::fromEntity);
    }

    @Mutation("createCategory")
    @Description("Create new category.")
    public Uni<CreateCategoryPayload> createCategory(@NonNull @Name("input") CreateCategoryInput input) {
        final xyz.anilkan.entity.Category entity = new xyz.anilkan.entity.Category();
        entity.setName(input.getName());

        return categoryService.createCategory(entity)
                .onItem().transform(Category::fromEntity)
                .onItem().transform(CreateCategoryPayload::new);
    }

    @Mutation("updateCategory")
    @Description("Update category.")
    public Uni<UpdateCategoryPayload> updateCategory(@NonNull @Name("input") UpdateCategoryInput input) {
        final LinkedHashMap<String, Object> inputArgs = context.getArgument("input");

        return categoryService.findCategoryById(input.getId())
                .onItem().transform(c -> {
                    if (inputArgs.containsKey("name"))
                        c.setName(input.getName());

                    return c;
        })
                .onItem().transformToUni(c -> categoryService.updateCategory(c))
                .onItem().transform(Category::fromEntity)
                .onItem().transform(UpdateCategoryPayload::new);
    }

    @Mutation("deleteCategory")
    @Description("Delete category")
    public Uni<DeleteCategoryPayload> deleteCategory(@NonNull @Name("id") UUID id) {
        return categoryService.deleteCategory(id)
                .onItem().transform(DeleteCategoryPayload::new);
    }


}
