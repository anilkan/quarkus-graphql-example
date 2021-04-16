package xyz.anilkan.entity;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Type;

@Type
public class Product extends Entity {
    @NotNull
    private String name;
    private Category category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
