package xyz.anilkan.graphql.payload.create;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Type;
import xyz.anilkan.graphql.type.Category;

@Type
public class CreateCategoryPayload {
    @NotNull
    private Category category;

    public CreateCategoryPayload(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
