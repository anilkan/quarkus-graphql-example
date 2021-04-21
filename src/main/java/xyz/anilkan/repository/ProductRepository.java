package xyz.anilkan.repository;

import java.util.Objects;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Validator;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import xyz.anilkan.entity.Product;
import xyz.anilkan.exception.EntityNotFoundException;

@ApplicationScoped
public class ProductRepository {
    private static String q_findAll = "SELECT p.id, p.name, p.category_id FROM product p";
    private static String q_findById = "SELECT p.id, p.name, p.category_id FROM product p WHERE p.id = $1";
    
    @Inject
    Validator validator;

    @Inject
    io.vertx.mutiny.pgclient.PgPool client;

    public Multi<Product> getAll() {
        return client.preparedQuery(q_findAll)
        .execute()
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

    private static Product from(Row row) {
        final Product product = new Product();
        product.setId(row.getUUID("id"));
        product.setName(row.getString("name"));
        product.setCategoryId(row.getUUID("category_id"));

        return product;
    }
}
