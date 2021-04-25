package xyz.anilkan.graphql;

import graphql.relay.ConnectionCursor;
import graphql.relay.DefaultConnectionCursor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class CursorHelper {
    public static ConnectionCursor encode(UUID id) {
        return new DefaultConnectionCursor(
                Base64.getEncoder().encodeToString(id.toString().getBytes(StandardCharsets.UTF_8)));
    }

    public static UUID decode(String cursor) {
        return UUID.fromString(new String(Base64.getDecoder().decode(cursor)));
    }
}
