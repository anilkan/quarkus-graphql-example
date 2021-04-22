package xyz.anilkan.graphql.type;

import org.eclipse.microprofile.graphql.Ignore;
import org.eclipse.microprofile.graphql.Type;

import java.util.UUID;

@Type
public class Product {
    private UUID id;
    private String name;
    @Ignore
    private UUID categoryId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public static Product fromEntity(xyz.anilkan.entity.Product entity) {
        final Product product = new Product();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setCategoryId(entity.getCategoryId());

        return product;
    }
}
