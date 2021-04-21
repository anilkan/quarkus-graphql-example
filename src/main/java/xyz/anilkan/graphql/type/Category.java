package xyz.anilkan.graphql.type;

import org.eclipse.microprofile.graphql.Type;

import java.util.UUID;

@Type
public class Category {
    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
