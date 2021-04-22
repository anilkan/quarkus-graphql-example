package xyz.anilkan.graphql.resource;

import io.smallrye.graphql.api.Context;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.graphql.*;
import xyz.anilkan.graphql.input.create.CreateProductInput;
import xyz.anilkan.graphql.input.update.UpdateProductInput;
import xyz.anilkan.graphql.payload.create.CreateProductPayload;
import xyz.anilkan.graphql.payload.delete.DeleteProductPayload;
import xyz.anilkan.graphql.payload.update.UpdateProductPayload;
import xyz.anilkan.graphql.type.Category;
import xyz.anilkan.graphql.type.Product;
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
        return productService.getAllProduct()
                .onItem().transform(Product::fromEntity)
                .collect().asList();
    }

    @Query("product")
    @Description("Get product")
    public Uni<Product> getProduct(@NonNull @Name("id") UUID id) {
        return productService.findProductById(id)
                .onItem().transform(Product::fromEntity);
    }

    @Mutation("createProduct")
    @Description("Create new product.")
    public Uni<CreateProductPayload> createProduct(@NonNull @Name("input") CreateProductInput input) {
        final xyz.anilkan.entity.Product entity = new xyz.anilkan.entity.Product();
        entity.setName(input.getName());
        entity.setCategoryId(input.getCategoryId());

        return productService.createProduct(entity)
                .onItem().transform(Product::fromEntity)
                .onItem().transform(CreateProductPayload::new);
    }

    @Mutation("updateProduct")
    @Description("Update product.")
    public Uni<UpdateProductPayload> updateProduct(@NonNull @Name("input") UpdateProductInput input) {
        final LinkedHashMap<String, Object> inputArgs = context.getArgument("input");

        return productService.findProductById(input.getId())
                .onItem().transform(p -> {
                    if (inputArgs.containsKey("name"))
                        p.setName(input.getName());

                    if (inputArgs.containsKey("categoryId"))
                        p.setCategoryId(input.getCategoryId());

                    return p;
        })
                .onItem().transformToUni(p -> productService.updateProduct(p))
                .onItem().transform(Product::fromEntity)
                .onItem().transform(UpdateProductPayload::new);
    }

    @Mutation("deleteProduct")
    @Description("Delete product")
    public Uni<DeleteProductPayload> deleteProduct(@NonNull @Name("id") UUID id) {
        return productService.deleteProduct(id)
                .onItem().transform(DeleteProductPayload::new);
    }

    public Uni<Category> category(@Source Product product) {
        return categoryService.findCategoryById(product.getCategoryId())
                .onItem().transform(Category::fromEntity);
    }
}
