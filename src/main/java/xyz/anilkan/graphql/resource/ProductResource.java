package xyz.anilkan.graphql.resource;

import io.smallrye.graphql.api.Context;
import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.entity.Product;
import xyz.anilkan.graphql.input.create.CreateProductInput;
import xyz.anilkan.graphql.input.update.UpdateProductInput;
import xyz.anilkan.graphql.payload.create.CreateProductPayload;
import xyz.anilkan.graphql.payload.delete.DeleteProductPayload;
import xyz.anilkan.graphql.payload.update.UpdateProductPayload;
import xyz.anilkan.service.ProductService;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;


@GraphQLApi
public class ProductResource {

    @Inject
    Context context;

    @Inject
    ProductService productService;

    @Query("products")
    @Description("Get all products")
    public List<Product> getAllProducts() {
        return productService.getAllProduct();
    }

    @Query("product")
    @Description("Get product")
    public Product getProduct(@NonNull @Name("id") UUID id) {
        return productService.getProduct(id);
    }

    @Mutation("createProduct")
    @Description("Create new product.")
    public CreateProductPayload createProduct(@NonNull @Name("input") CreateProductInput input) {
        return new CreateProductPayload(productService.createProduct(input));
    }

    @Mutation("updateProduct")
    @Description("Update product")
    @SuppressWarnings("unchecked")
    public UpdateProductPayload updateProduct(@NonNull @Name("id") UUID id, @NonNull @Name("input") UpdateProductInput input) {
        return new UpdateProductPayload(productService.updateProduct(id, (LinkedHashMap<String, Object>) context.getArgument("input")));
    }

    @Mutation("deleteProduct")
    @Description("Delete product")
    public DeleteProductPayload deleteProduct(@NonNull @Name("id") UUID id) {
        return new DeleteProductPayload(productService.deleteProduct(id));
    }
}
