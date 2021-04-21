package xyz.anilkan.entity;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.eclipse.microprofile.graphql.Type;
import xyz.anilkan.exception.EntityNotFoundException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Type
public class Category extends Entity {
    @NotBlank
    @Size(min = 5, max = 255, message = "Category name length can not be less than 5 or more than 255.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uni<Category> save(io.vertx.mutiny.pgclient.PgPool client) {
        return client.preparedQuery("INSERT INTO category(name) VALUES($1) RETURNING id")
                .execute(Tuple.of(name))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(i -> i.next().getUUID("id"))
                .onItem().transform(id -> {this.setId(id); return this;});
    }

    public Uni<Boolean> update(io.vertx.mutiny.pgclient.PgPool client) {
        return client.preparedQuery("UPDATE category SET name=$1 WHERE id=$2")
                .execute(Tuple.of(name, getId()))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }

    public static Uni<Boolean> delete(io.vertx.mutiny.pgclient.PgPool client, UUID id) {
        return client.preparedQuery("DELETE FROM category c WHERE c.id=$1")
                .execute(Tuple.of(id))
                .onItem().transform(rs -> rs.rowCount() == 1);
    }

    public static Multi<Category> findAll (io.vertx.mutiny.pgclient.PgPool client) {
        return client.preparedQuery("SELECT c.id, c.name FROM category c")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(Category::from);
    }

    public static Uni<Category> findById (io.vertx.mutiny.pgclient.PgPool client, UUID id) {
        return client.preparedQuery("SELECT c.id, c.name FROM category c WHERE c.id=$1")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(r -> r.hasNext() ? from(r.next()) : null);
    }

    private static Category from(Row row) {
        final Category category = new Category();
        category.setId(row.getUUID("id"));
        category.setName(row.getString("name"));

        return category;
    }
}
