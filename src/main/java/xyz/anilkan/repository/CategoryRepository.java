package xyz.anilkan.repository;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import xyz.anilkan.entity.Category;
import xyz.anilkan.exception.EntityNotFoundException;
import xyz.anilkan.exception.EntityValidationException;
import xyz.anilkan.helper.Page;
import xyz.anilkan.helper.PageRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class CategoryRepository {

    private static final String q_findAll = "SELECT c.id, c.name FROM category c ORDER BY id";
    private static final String q_findFirst = "SELECT c.id, c.name FROM category c ORDER BY id LIMIT $1";
    private static final String q_findFirstAfter = "SELECT c.id, c.name FROM category c WHERE c.id > $1 ORDER BY id LIMIT $2";
    private static final String q_findById = "SELECT c.id, c.name FROM category c WHERE id=$1";
    private static final String q_create = "INSERT INTO category(name) VALUES($1) RETURNING id";
    private static final String q_update = "UPDATE category SET name=$1 WHERE id=$2";
    private static final String q_delete = "DELETE FROM category WHERE id=$1";

    @Inject
    Validator validator;

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @Deprecated
    public Multi<Category> getAll() {
        return client.preparedQuery(q_findAll)
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(CategoryRepository::from);
    }

    public Uni<Page<Category>> getAll(final PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest);

        return getAll(pageRequest.getFirst() + 1, pageRequest.getAfter())
                .collect().asList()
                .onItem().transform(list -> {
                    final boolean hasNext = list.size() > pageRequest.getFirst();
                    final boolean hasPrevious = pageRequest.getAfter() != null;

                    if (list.size() == pageRequest.getFirst() + 1)
                        list.remove(list.size() -1);

                    final Page<Category> page = new Page<>();
                    page.setResult(list);
                    page.setHasNextPage(hasNext);
                    page.setHasPreviousPage(hasPrevious);

                    return page;
                });
    }

    public Multi<Category> getAll(final Integer first, final UUID after) {
        Objects.requireNonNull(first);

        if (first < 0)
            throw new RuntimeException("first cannot be less than zero!");

        final String query = after == null ? q_findFirst : q_findFirstAfter;
        final Tuple params = after == null ? Tuple.of(first) : Tuple.of(after, first);

        return client.preparedQuery(query)
                .execute(params)
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(CategoryRepository::from);
    }

    public Uni<Category> findById(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery(q_findById)
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(rs -> rs.hasNext() ? from(rs.next()) : null)
                .onItem().ifNull().failWith(new EntityNotFoundException("Category not found!"));
    }

    public Uni<Category> create(Category category) {
        Objects.requireNonNull(category);

        validate(category);

        return client.preparedQuery(q_create)
                .execute(Tuple.of(category.getName()))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(r -> r.next().getUUID("id"))
                .onItem().transform(id -> {
                    category.setId(id);

                    return category;
                });
    }

    public Uni<Boolean> update(Category category) {
        Objects.requireNonNull(category);

        validate(category);

        return client.preparedQuery(q_update)
                .execute(Tuple.of(category.getName(), category.getId()))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }

    public Uni<Boolean> delete(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery(q_delete)
                .execute(Tuple.of(id))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }

    private static Category from(Row row) {
        final Category category = new Category();
        category.setId(row.getUUID("id"));
        category.setName(row.getString("name"));

        return category;
    }

    private Boolean validate(Category category) {
        Set<ConstraintViolation<Object>> violations = validator.validate(category);

        if (!violations.isEmpty())
            throw new EntityValidationException(violations);

        return true;
    }
}
