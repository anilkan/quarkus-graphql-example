package xyz.anilkan.entity;

import org.eclipse.microprofile.graphql.Ignore;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public abstract class Entity {
    @NotNull
    private UUID id;

    public UUID getId() {
        return id;
    }

    @Ignore
    public void setId(UUID id) {
        this.id = id;
    }
}
