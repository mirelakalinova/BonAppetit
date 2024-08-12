package com.bonappetit.model.dto;

import com.bonappetit.model.entity.Recipe;

public class RecipeInfoDto {

    private String name;
    private long id;
    private String ingredients;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public RecipeInfoDto(Recipe recipe) {
        this.name = recipe.getName();
        this.ingredients= recipe.getIngredients();
        this.id = recipe.getId();
    }

    public RecipeInfoDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
