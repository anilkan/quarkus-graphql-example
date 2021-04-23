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
public class CategoryResourceTest {

    @Test
    public void test_createCategory_success() throws IOException {
        final String categoryName = "Category 01";

        createCategory(categoryName).then().statusCode(200)
                .body("data.createCategory.category.id", Matchers.notNullValue())
                .body("data.createCategory.category.name", Matchers.equalTo(categoryName));
    }

    @Test
    public void test_updateCategory_success() throws IOException {
        final String categoryName = "Category Test";

        final UUID categoryId = createCategory(categoryName)
                .jsonPath().getUUID("data.createCategory.category.id");

        final String categoryNameUpdated = categoryName + " - Updated";

        updateCategory(categoryId, categoryNameUpdated).then().statusCode(200)
                .body("data.updateCategory.category.id", Matchers.equalTo(categoryId.toString()))
                .body("data.updateCategory.category.name", Matchers.equalTo(categoryNameUpdated));
    }

    @Test
    public void test_deleteCategory_success() throws IOException {
        final String categoryName = "Category Test";

        final UUID categoryId = createCategory(categoryName)
                .jsonPath().getUUID("data.createCategory.category.id");

        deleteCategory(categoryId).then().statusCode(200)
                .body("data.deleteCategory.deleted", Matchers.equalTo(Boolean.TRUE));
    }

    public static Response createCategory(final String name) throws IOException {
        Objects.requireNonNull(name);

        final ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("name", name);

        return GraphQLTestHelper.sendQuery("createCategory.graphql", new ObjectMapper().createObjectNode().set("input", variables));
    }

    public static Response updateCategory(final UUID id, final String name) throws IOException {
        Objects.requireNonNull(id);

        final ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("id", id.toString());

        if (name != null)
            variables.put("name", name);

        return GraphQLTestHelper.sendQuery("updateCategory.graphql", new ObjectMapper().createObjectNode().set("input", variables));

    }

    public static Response deleteCategory(final UUID id) throws IOException {
        Objects.requireNonNull(id);

        final ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("id", id.toString());

        return GraphQLTestHelper.sendQuery("deleteCategory.graphql", variables);
    }
}
