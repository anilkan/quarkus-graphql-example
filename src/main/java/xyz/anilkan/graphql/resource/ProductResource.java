package xyz.anilkan.graphql.resource;

import io.smallrye.graphql.api.Context;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.entity.Category;
import xyz.anilkan.entity.Product;
import xyz.anilkan.graphql.input.create.CreateProductInput;
import xyz.anilkan.graphql.input.update.UpdateProductInput;
import xyz.anilkan.graphql.payload.create.CreateProductPayload;
import xyz.anilkan.graphql.payload.delete.DeleteProductPayload;
import xyz.anilkan.graphql.payload.update.UpdateProductPayload;
import xyz.anilkan.service.CategoryService;
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

    @Inject
    CategoryService categoryService;

    @Query("products")
    @Description("Get all products")
    public Uni<List<Product>> getAllProducts() {
        return productService.getAllProduct().collect().asList();
    }

    @Query("product")
    @Description("Get product")
    public Uni<Product> getProduct(@NonNull @Name("id") UUID id) {
        return productService.getProduct(id);
    }

    @Mutation("createProduct")
    @Description("Create new product.")
    public Uni<CreateProductPayload> createProduct(@NonNull @Name("input") CreateProductInput input) {
        return productService.createProduct(input)
                .onItem().transform(CreateProductPayload::new);
    }

    @Mutation("updateProduct")
    @Description("Update product")
    @SuppressWarnings("unchecked")
    public Uni<UpdateProductPayload> updateProduct(@NonNull @Name("id") UUID id, @NonNull @Name("input") UpdateProductInput input) {
        return productService.updateProduct(id, (LinkedHashMap<String, Object>) context.getArgument("input"))
                .onItem().transform(UpdateProductPayload::new);
    }

    @Mutation("deleteProduct")
    @Description("Delete product")
    public Uni<DeleteProductPayload> deleteProduct(@NonNull @Name("id") UUID id) {
        return productService.deleteProduct(id)
                .onItem().transform(DeleteProductPayload::new);
    }

    public Uni<Category> category(@Source Product product) {
        return categoryService.findById(product.getCategoryId());
    }
}
