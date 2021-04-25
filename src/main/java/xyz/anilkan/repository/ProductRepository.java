package xyz.anilkan.repository;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import xyz.anilkan.entity.Product;
import xyz.anilkan.exception.EntityNotFoundException;
import xyz.anilkan.exception.EntityValidationException;
import xyz.anilkan.helper.Page;
import xyz.anilkan.helper.PageRequest;

@ApplicationScoped
public class ProductRepository {
    private static String q_findAll = "SELECT p.id, p.name, p.category_id FROM product p ORDER BY id";
    private static String q_findFirst = "SELECT p.id, p.name, p.category_id FROM product p ORDER BY id LIMIT $1";
    private static String q_findFirstAfter = "SELECT p.id, p.name, p.category_id FROM product p WHERE p.id > $1 ORDER BY id LIMIT $2";
    private static String q_findById = "SELECT p.id, p.name, p.category_id FROM product p WHERE p.id = $1";
    private static String q_create = "INSERT INTO product(name, category_id) VALUES($1, $2) RETURNING id";
    private static String q_update = "UPDATE product SET name = $1, category_id = $2 WHERE id = $3";
    private static String q_delete = "DELETE FROM product WHERE id = $1";

    @Inject
    Validator validator;

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @Deprecated
    public Multi<Product> getAll() {
        return client.preparedQuery(q_findAll)
            .execute()
            .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
            .onItem().transform(ProductRepository::from);
    }

    public Uni<Page<Product>> getAll(final PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest);

        return getAll(pageRequest.getFirst() + 1, pageRequest.getAfter())
                .collect().asList()
                .onItem().transform(list -> {
                    final boolean hasNext = list.size() > pageRequest.getFirst();
                    final boolean hasPrevious = pageRequest.getAfter() != null;

                    if (list.size() == pageRequest.getFirst() + 1)
                        list.remove(list.size() - 1);

                    final Page<Product> page = new Page<>();
                    page.setResult(list);
                    page.setHasNextPage(hasNext);
                    page.setHasPreviousPage(hasPrevious);

                    return page;
        });
    }

    public Multi<Product> getAll(final Integer first, final UUID after) {
        Objects.requireNonNull(first);

        if (first < 0)
            throw new RuntimeException("First cannot be less than zero!");

        final String query = after == null ? q_findFirst : q_findFirstAfter;
        final Tuple params = after == null ? Tuple.of(first) : Tuple.of(after, first);

        return client.preparedQuery(query)
                .execute(params)
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(ProductRepository::from);
    }

    public Uni<Product> findById(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery(q_findById)
            .execute(Tuple.of(id))
            .onItem().transform(RowSet::iterator)
            .onItem().transform(rs -> rs.hasNext() ? from(rs.next()) : null)
            .onItem().ifNull().failWith(new EntityNotFoundException("Product not found!"));
    }

    public Uni<Product> create(Product product) {
        Objects.requireNonNull(product);

        return client.preparedQuery(q_create)
                .execute(Tuple.of(product.getName(), product.getCategoryId()))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(r -> r.next().getUUID("id"))
                .onItem().transform(id -> {
                    product.setId(id);

                    return product;
        });
    }

    public Uni<Boolean> update(Product product) {
        Objects.requireNonNull(product);

        return client.preparedQuery(q_update)
                .execute(Tuple.of(product.getName(), product.getCategoryId(), product.getId()))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }

    public Uni<Boolean> delete(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery(q_delete)
                .execute(Tuple.of(id))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }

    private static Product from(Row row) {
        final Product product = new Product();
        product.setId(row.getUUID("id"));
        product.setName(row.getString("name"));
        product.setCategoryId(row.getUUID("category_id"));

        return product;
    }

    private boolean validate(Product product) {
        Set<ConstraintViolation<Object>> violations = validator.validate(product);

        if (!violations.isEmpty())
            throw new EntityValidationException(violations);

        return true;
    }
}
