package xyz.anilkan.resource;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.entity.Product;
import xyz.anilkan.service.ProductService;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;


@GraphQLApi
public class ProductResource {

    @Inject
    ProductService productService;

    @Query("products")
    @Description("Get all products")
    public Uni<List<Product>> getAllProducts() {
        return productService.getAllProduct().toUni();
    }

    @Query("product")
    @Description("Get product")
    public Uni<Product> getProduct(@Name("id") UUID id) {
        return productService.getProduct(id);
    }

    @Mutation("addProduct")
    @Description("Add product")
    public Product addProduct(Product product) {
        return productService.addProduct(product);
    }

    @Mutation("deleteProduct")
    @Description("Delete product")
    public boolean deleteProduct(@Name("id") UUID id) {
        return productService.deleteProduct(id);
    }

    @Mutation("updateProduct")
    @Description("Update product")
    public Uni<Product> updateProduct(@Name("id") UUID id, Product product) {
        return productService.updateProduct(id, product);
    }
}
