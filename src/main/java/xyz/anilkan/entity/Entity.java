package xyz.anilkan.entity;

import org.eclipse.microprofile.graphql.Ignore;

import java.util.UUID;

public abstract class Entity {
    private UUID id;

    public UUID getId() {
        return id;
    }

    @Ignore
    public void setId(UUID id) {
        this.id = id;
    }
}
