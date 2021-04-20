package xyz.anilkan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import xyz.anilkan.entity.Product;
import xyz.anilkan.exception.EntityValidationException;
import xyz.anilkan.graphql.input.create.CreateProductInput;
import xyz.anilkan.graphql.input.update.UpdateProductInput;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

@ApplicationScoped
public class ProductService {

    @Inject
    Validator validator;

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    @Inject
    CategoryService categoryService;

    public ProductService() {
    }

    public Multi<Product> getAllProduct() {
        return client.preparedQuery("SELECT p.id, p.name, p.category_id FROM product p")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Product::from);
    }

    public Uni<Product> getProduct(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery("SELECT p.id, p.name, p.category_id FROM product p WHERE p.id=$1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? Product.from(iterator.next()) : null);
    }

    public Uni<Product> createProduct(CreateProductInput input) {
        Objects.requireNonNull(input);

        return client.preparedQuery("INSERT INTO product(name, category_id) VALUES($1, $2) RETURNING id")
                .execute(Tuple.of(input.getName(), input.getCategoryId()))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.next().getUUID("id"))
                .onItem().transform(id -> {
                    final Product product = new Product();
                    product.setId(id);
                    product.setName(input.getName());
                    product.setCategoryId(input.getCategoryId());

                    return product;
                });
    }

    public Uni<Product> updateProduct(UUID id, UpdateProductInput input) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(input);

        final ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked") final LinkedHashMap<String, Object> vars = objectMapper.convertValue(input, LinkedHashMap.class);

        return updateProduct(id, vars);
    }

    public Uni<Product> updateProduct(UUID id, LinkedHashMap<String, Object> vars) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(vars);

        return getProduct(id)
                .onItem().transform(p -> {
                    if (vars.containsKey("name"))
                        p.setName((String)vars.get("name"));

                    if (vars.containsKey("categoryId"))
                        p.setCategoryId(UUID.fromString((String)vars.get("categoryId")));

                    return p;
        })
                .onItem().transformToUni(p ->
                    client.preparedQuery("UPDATE product SET name=$1, categoryId=$2 WHERE id=$3")
                            .execute(Tuple.of(p.getName(), p.getCategoryId(), p.getId()))
                .onItem().transform(rs -> rs.rowCount() > 0 ? p : null)
        );
    }

    public Uni<Boolean> deleteProduct(UUID id) {
        Objects.requireNonNull(id);

        return client.preparedQuery("DELETE FROM product WHERE id=$1")
                .execute(Tuple.of(id))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }

    private void validateProduct(Product product) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(product);

        if (!violations.isEmpty())
            throw new EntityValidationException(violations);
    }
}
