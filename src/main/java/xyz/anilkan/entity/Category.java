package xyz.anilkan.entity;

import org.eclipse.microprofile.graphql.Type;

@Type
public class Category extends Entity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
