package xyz.anilkan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.anilkan.entity.Product;
import xyz.anilkan.exception.EntityValidationException;
import xyz.anilkan.graphql.input.create.CreateProductInput;
import xyz.anilkan.graphql.input.update.UpdateProductInput;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

@ApplicationScoped
public class ProductService {

    @Inject
    Validator validator;

    @Inject
    CategoryService categoryService;

    private final List<Product> productList = new ArrayList<>();

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

    public Product createProduct(CreateProductInput input) {
        Objects.requireNonNull(input);

        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(input.getName());
        product.setCategory(categoryService.getCategory(input.getCategoryId()));

        validateProduct(product);

        productList.add(product);

        return product;
    }

    public Product updateProduct(UUID id, UpdateProductInput input) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(input);

        final ObjectMapper objectMapper = new ObjectMapper();
        @SuppressWarnings("unchecked") final LinkedHashMap<String, Object> vars = objectMapper.convertValue(input, LinkedHashMap.class);

        return updateProduct(id, vars);
    }

    public Product updateProduct(UUID id, LinkedHashMap<String, Object> vars) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(vars);

        int index = productList.indexOf(productList.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null));

        final Product product = productList.get(index);

        if (vars.containsKey("name"))
            product.setName((String) vars.get("name"));

        if (vars.containsKey("categoryId"))
            product.setCategory(categoryService.getCategory(UUID.fromString((String) vars.get("categoryId"))));

        validateProduct(product);

        productList.set(index, product);

        return product;
    }

    public boolean deleteProduct(UUID id) {
        return productList.removeIf(p -> p.getId().equals(id));
    }

    private void validateProduct(Product product) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(product);

        if (!violations.isEmpty())
            throw new EntityValidationException(violations);
    }
}
