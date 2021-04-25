package xyz.anilkan.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import xyz.anilkan.entity.Product;
import xyz.anilkan.helper.Page;
import xyz.anilkan.helper.PageRequest;
import xyz.anilkan.repository.ProductRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepo;

    public ProductService() {
    }

    @Deprecated
    public Multi<Product> getAllProduct() {
        return productRepo.getAll();
    }

    public Uni<Page<Product>> getAllProduct(PageRequest pageRequest) {
        return productRepo.getAll(pageRequest);
    }

    public Multi<Product> getAllProduct(final Integer first, final UUID after) {
        return productRepo.getAll(first, after);
    }

    public Uni<Product> findProductById(UUID id) {
        Objects.requireNonNull(id);

        return productRepo.findById(id);
    }

    public Uni<Product> createProduct(Product product) {
        Objects.requireNonNull(product);

        return productRepo.create(product);
    }

    public Uni<Product> updateProduct(Product product) {
        Objects.requireNonNull(product);

        return productRepo.update(product)
                .onItem().transform(r -> r ? product : null)
                .onItem().ifNull().failWith(new RuntimeException("An error occurred when update"));
    }

    public Uni<Boolean> deleteProduct(UUID id) {
        Objects.requireNonNull(id);

        return productRepo.delete(id);
    }
}
