package xyz.anilkan.graphql.payload.delete;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Type;

@Type
public class DeleteCategoryPayload {
    @NotNull
    private boolean isDeleted;

    public DeleteCategoryPayload(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
