package xyz.anilkan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import xyz.anilkan.entity.Category;
import xyz.anilkan.graphql.input.create.CreateCategoryInput;
import xyz.anilkan.graphql.input.update.UpdateCategoryInput;
import xyz.anilkan.repository.CategoryRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.*;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepo;

    public Multi<Category> getAllCategories() {
        return categoryRepo.getAll();
    }

    public Uni<Category> findById(UUID id) {
        Objects.requireNonNull(id);

        return categoryRepo.findById(id);
    }

    public Uni<Category> createCategory(Category category) {
        Objects.requireNonNull(category);

        return categoryRepo.create(category);
    }

    public Uni<Category> updateCategory(Category category) {
        Objects.requireNonNull(category);

        return categoryRepo.update(category)
                .onItem().transform(r -> r ? category : null)
                .onItem().ifNull().failWith(new RuntimeException("An error occurred when update"));
    }

    public Uni<Boolean> deleteCategory(UUID id) {
        Objects.requireNonNull(id);

        return categoryRepo.delete(id);
    }
}
