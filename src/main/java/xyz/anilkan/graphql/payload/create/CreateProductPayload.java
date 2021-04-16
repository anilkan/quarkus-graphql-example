package xyz.anilkan.graphql.payload.create;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Type;
import xyz.anilkan.entity.Product;

@Type
public class CreateProductPayload {
    @NotNull
    private Product product;

    public CreateProductPayload(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
