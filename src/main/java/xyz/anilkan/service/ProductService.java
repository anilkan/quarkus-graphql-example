package xyz.anilkan.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import xyz.anilkan.entity.Product;
import xyz.anilkan.graphql.input.create.CreateProductInput;
import xyz.anilkan.graphql.input.update.UpdateProductInput;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class ProductService {

    @Inject
    CategoryService categoryService;

    private List<Product> productList = new ArrayList<>();

    public ProductService() {
        final Product p1 = new Product();
        p1.setId(UUID.randomUUID());
        p1.setName("Product 01");
        productList.add(p1);

        final Product p2 = new Product();
        p2.setId(UUID.randomUUID());
        p2.setName("Product 02");
        productList.add(p2);
    }

    public List<Product> getAllProduct() {
        return productList;
    }

    public Product getProduct(UUID id) {
        return productList.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public Product createProduct (CreateProductInput input) {
        Objects.requireNonNull(input);

        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(input.getName());
        product.setCategory(categoryService.getCategory(input.getCategoryId()));

        productList.add(product);

        return product;
    }

    public Product updateProduct (UUID id, UpdateProductInput input) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(input);

        int index = productList.indexOf(productList.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null));

        final Product product = productList.get(index);
        product.setId(id);
        product.setName(input.getName());
        product.setCategory(categoryService.getCategory(input.getCategoryId()));

        productList.set(index, product);

        return product;
    }

    public boolean deleteProduct(UUID id) {
        return productList.removeIf(p -> p.getId().equals(id));
    }
}
