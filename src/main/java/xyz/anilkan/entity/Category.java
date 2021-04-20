package xyz.anilkan.entity;

import io.vertx.mutiny.sqlclient.Row;
import org.eclipse.microprofile.graphql.Type;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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

    public static Category from(Row row) {
        final Category category = new Category();
        category.setId(row.getUUID("id"));
        category.setName(row.getString("name"));

        return category;
    }
}
