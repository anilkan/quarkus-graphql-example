package xyz.anilkan.graphql.input.create;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;

import java.util.UUID;

@Input("CreateProductInput")
public class CreateProductInput {
    @NonNull
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
