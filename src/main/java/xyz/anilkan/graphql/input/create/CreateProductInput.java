package xyz.anilkan.graphql.input.create;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Input;

import java.util.UUID;

@Input("CreateProductInput")
public class CreateProductInput {
    @NotNull
    private String name;
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
}
