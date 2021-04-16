package xyz.anilkan.graphql.input.create;

import io.smallrye.common.constraint.NotNull;
import org.eclipse.microprofile.graphql.Input;

@Input("CreateCategoryInput")
public class CreateCategoryInput {
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
