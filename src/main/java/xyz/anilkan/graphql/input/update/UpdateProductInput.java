package xyz.anilkan.graphql.input.update;

import org.eclipse.microprofile.graphql.Input;

import java.util.UUID;

@Input("UpdateProductInput")
public class UpdateProductInput {
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
