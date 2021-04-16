package xyz.anilkan.graphql.payload.update;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Type;
import xyz.anilkan.entity.Product;

@Type
public class UpdateProductPayload {
    @NotNull
    private Product product;

    public UpdateProductPayload(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
