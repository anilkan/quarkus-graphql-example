package xyz.anilkan.graphql.input.create;

import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.NonNull;

@Input("CreateCategoryInput")
public class CreateCategoryInput {
    @NonNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
