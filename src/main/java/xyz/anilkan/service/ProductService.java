package xyz.anilkan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import xyz.anilkan.entity.Product;
import xyz.anilkan.exception.EntityValidationException;
import xyz.anilkan.graphql.input.create.CreateProductInput;
import xyz.anilkan.graphql.input.update.UpdateProductInput;
import xyz.anilkan.repository.ProductRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.*;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepo;

    public ProductService() {
    }

    public Multi<Product> getAllProduct() {
        return productRepo.getAll();
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
