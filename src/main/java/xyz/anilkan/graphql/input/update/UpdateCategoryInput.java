package xyz.anilkan.graphql.input.update;

import org.eclipse.microprofile.graphql.Input;

@Input("UpdateCategoryInput")
public class UpdateCategoryInput {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
