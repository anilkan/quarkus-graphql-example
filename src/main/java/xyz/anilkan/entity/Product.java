package xyz.anilkan.entity;

import io.vertx.mutiny.sqlclient.Row;
import org.eclipse.microprofile.graphql.Ignore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

public class Product extends Entity {
    @NotBlank
    @Size(min = 5, max = 255, message = "Product name length can not be less than 5 or more than 255.")
    private String name;

    @Ignore
    private UUID categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public static Product from (Row row) {
        final Product product = new Product();
        product.setId(row.getUUID("id"));
        product.setName(row.getString("name"));
        product.setCategoryId(row.getUUID("category_id"));

        return product;
    }
}
