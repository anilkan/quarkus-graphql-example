import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class GraphQLTestHelper {

    private static String readFile(final String filePath) throws IOException {
        final File file = Paths.get("src", "test", "resources", "graphql", filePath).toFile();
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    public static String getQueryString(final String filePath, final ObjectNode variables) throws IOException {
        final String query = readFile(filePath);

        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectNode objectNode = objectMapper.createObjectNode();

        objectNode.put("query", query);
        objectNode.put("variables", variables);

        return objectMapper.writeValueAsString(objectNode);
    }

    public static Response sendQuery(final String filePath, final ObjectNode variables) throws IOException {
        final String query =
                GraphQLTestHelper.getQueryString(filePath, variables);

        return RestAssured.given()
                .when()
                .contentType("application/json")
                .body(query)
                .post("/graphql");
    }
}
