package xyz.anilkan.graphql.payload.update;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Type;
import xyz.anilkan.graphql.type.Category;

@Type
public class UpdateCategoryPayload {
    @NotNull
    private Category category;

    public UpdateCategoryPayload(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
