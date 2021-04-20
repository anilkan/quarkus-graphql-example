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
        return client.preparedQuery("SELECT * FROM category c")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Category::from);
    }

    public Uni<Category> getCategory(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery("SELECT id, name FROM category c WHERE id = $1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Category.from(iterator.next()): null);
    }

    public Uni<Category> createCategory(CreateCategoryInput input) {
        Objects.requireNonNull(input);

        return client.preparedQuery("INSERT INTO category(name) VALUES($1) RETURNING id")
                .execute(Tuple.of(input.getName()))
                .onItem().transform(rowSet -> rowSet.iterator().next().getUUID("id"))
                .onItem().transform(id -> {
                    final Category category = new Category();
                    category.setId(id);
                    category.setName(input.getName());
                    return category;
                });
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
