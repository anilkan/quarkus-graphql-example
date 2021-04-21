package xyz.anilkan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import xyz.anilkan.entity.Category;
import xyz.anilkan.graphql.input.create.CreateCategoryInput;
import xyz.anilkan.graphql.input.update.UpdateCategoryInput;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Validator;
import java.util.*;

@ApplicationScoped
public class CategoryService {

    @Inject
    Validator validator;

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    public CategoryService() {
    }

    public Multi<Category> getAllCategories() {
        return Category.findAll(client);
    }

    public Uni<Category> getCategory(UUID id) {
        Objects.requireNonNull(id);

        return Category.findById(client, id);
    }

    public Uni<Category> createCategory(CreateCategoryInput input) {
        Objects.requireNonNull(input);

        final Category category = new Category();
        category.setName(input.getName());
        return category.save(client);
    }

    public Uni<Category> updateCategory(UUID id, UpdateCategoryInput input) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(input);

        final ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked") final LinkedHashMap<String, Object> vars = objectMapper.convertValue(input, LinkedHashMap.class);

        return updateCategory(id, vars);
    }

    public Uni<Category> updateCategory(UUID id, LinkedHashMap<String, Object> vars) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(vars);

        return getCategory(id)
                .onItem().transform(c -> {
                    if (vars.containsKey("name"))
                        c.setName((String) vars.get("name"));

                    return c;
        })
                .onItem().transformToUni(c ->
                        client.preparedQuery("UPDATE category SET name=$1 WHERE id=$2").execute(Tuple.of(c.getName(), c.getId()))
                            .onItem().transform(rs -> rs.rowCount() > 0 ? c : null)
        );
    }

    public Uni<Boolean> deleteCategory(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery("DELETE FROM category WHERE id=$1")
                .execute(Tuple.of(id))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }
}
