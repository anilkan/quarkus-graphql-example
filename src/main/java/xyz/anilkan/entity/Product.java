package xyz.anilkan.entity;

import org.eclipse.microprofile.graphql.Type;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Type
public class Product extends Entity {
    @NotBlank
    @Size(min = 5, max = 255, message = "Product name length can not be less than 5 or more than 255.")
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
