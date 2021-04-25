package xyz.anilkan.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import xyz.anilkan.entity.Category;
import xyz.anilkan.helper.Page;
import xyz.anilkan.helper.PageRequest;
import xyz.anilkan.repository.CategoryRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepo;

    @Deprecated
    public Multi<Category> getAllCategory() {
        return categoryRepo.getAll();
    }

    public Uni<Page<Category>> getAllCategory(PageRequest pageRequest) {
        return categoryRepo.getAll(pageRequest);
    }

    public Multi<Category> getAllCategory(final Integer first, final UUID after) {
        return categoryRepo.getAll(first, after);
    }

    public Uni<Category> findCategoryById(UUID id) {
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
