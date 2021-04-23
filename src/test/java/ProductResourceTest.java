import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@QuarkusTest
public class ProductResourceTest {

    @Test
    public void test_createProduct_onlyNonNullFields_success() throws IOException {
        final String productName = "Product Name";

        createProduct(productName, null).then().statusCode(200)
                .body("data.createProduct.product.id", Matchers.notNullValue())
                .body("data.createProduct.product.name", Matchers.equalTo(productName))
                .body("data.createProduct.product.category", Matchers.nullValue());
    }

    @Test
    public void test_createProduct_allFields_success() throws IOException {
        final String productName = "Product Name";
        final String categoryName = "Category 001";

        final UUID categoryId = CategoryResourceTest.createCategory(categoryName)
                .jsonPath().getUUID("data.createCategory.category.id");

        createProduct(productName, categoryId).then().statusCode(200)
                .body("data.createProduct.product.id", Matchers.notNullValue())
                .body("data.createProduct.product.name", Matchers.equalTo(productName))
                .body("data.createProduct.product.category", Matchers.notNullValue())
                .body("data.createProduct.product.category.id", Matchers.equalTo(categoryId.toString()));
    }

    @Test
    public void test_updateProduct_onlyNonNullFields_success() throws IOException {
        final String productName = "Product 01";

        final UUID productId = createProduct(productName, null)
                .jsonPath().getUUID("data.createProduct.product.id");

        final String updatedProductName = productName + " - Updated";

        updateProduct(productId, updatedProductName, null).then().statusCode(200)
                .body("data.updateProduct.product.id", Matchers.notNullValue())
                .body("data.updateProduct.product.name", Matchers.equalTo(updatedProductName))
                .body("data.updateProduct.product.category", Matchers.nullValue());
    }

    @Test
    public void test_updateProduct_onlyNullableFields_success() throws IOException {
        final String productName = "Product Name";
        final String categoryName = "Category 001";

        final UUID categoryId = CategoryResourceTest.createCategory(categoryName)
                .jsonPath().getUUID("data.createCategory.category.id");

        Response response = createProduct(productName, categoryId);
        final UUID productId = response
                .jsonPath().getUUID("data.createProduct.product.id");

        response.then().body("data.createProduct.product.category.id", Matchers.equalTo(categoryId.toString()));

        final String categoryName_2 = "Category 002";
        final UUID categoryId_2 = CategoryResourceTest.createCategory(categoryName_2)
                .jsonPath().getUUID("data.createCategory.category.id");

        updateProduct(productId, null, categoryId_2).then().statusCode(200)
                .body("data.updateProduct.product.id", Matchers.notNullValue())
                .body("data.updateProduct.product.name", Matchers.equalTo(productName))
                .body("data.updateProduct.product.category", Matchers.notNullValue())
                .body("data.updateProduct.product.category.id", Matchers.equalTo(categoryId_2.toString()))
                .body("data.updateProduct.product.category.name", Matchers.equalTo(categoryName_2));

    }

    public static Response createProduct(final String name, final UUID categoryId) throws IOException {
        Objects.requireNonNull(name);

        final ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("name", name);

        if (categoryId != null)
            variables.put("categoryId", categoryId.toString());

        return GraphQLTestHelper.sendQuery("createProduct.graphql", new ObjectMapper().createObjectNode().set("input", variables));
    }

    public static Response updateProduct(final UUID id, final String name, final UUID categoryId) throws IOException {
        Objects.requireNonNull(id);

        final ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("id", id.toString());

        if (name != null)
            variables.put("name", name);

        if (categoryId != null)
            variables.put("categoryId", categoryId.toString());

        return GraphQLTestHelper.sendQuery("updateProduct.graphql", new ObjectMapper().createObjectNode().set("input", variables));
    }
}
