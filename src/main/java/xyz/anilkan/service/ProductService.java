package xyz.anilkan.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import xyz.anilkan.entity.Product;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProductService {

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

    public Multi<List<Product>> getAllProduct() {
        return Multi.createFrom().items(productList);
    }

    public Uni<Product> getProduct(UUID id) {
        return Uni.createFrom().item(id)
                .onItem().transform(i -> productList.stream().filter(p -> p.getId().equals(i)).findFirst().orElse(null));
    }

    public Product addProduct(Product product) {
        product.setId(UUID.randomUUID());
        productList.add(product);

        return product;
    }

    public boolean deleteProduct(UUID id) {
        return productList.removeIf(p -> p.getId().equals(id));
    }

    public Uni<Product> updateProduct (UUID id, Product product) {
        int index = productList.indexOf(productList.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null));
        product.setId(id);
        productList.set(index, product);

        return Uni.createFrom().item(product);
    }
}
