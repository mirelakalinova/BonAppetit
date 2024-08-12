package com.bonappetit.model.dto;

import com.bonappetit.model.entity.Category;
import com.bonappetit.model.entity.CategoryName;

import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddRecipeDto {

    @NotNull
    @Size(min=3, max=40)
    private String name;
    @NotNull
    @Size(min=2, max=150)
    private String ingredients;

    @NotNull
    private CategoryName category;

    public AddRecipeDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public CategoryName getCategory() {
        return category;
    }

    public void setCategory(CategoryName category) {
        this.category = category;
    }
}
