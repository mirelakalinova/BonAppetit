package com.bonappetit.model.entity;

public enum CategoryName {

    MAIN_DISH("Heart of the meal, substantial and satisfying; main dish delights taste buds.", "/img/main-dish.png"),
    DESSERT("Sweet finale, indulgent and delightful; dessert crowns the dining experience with joy.", "/img/dessert.png"),
    COCKTAIL("Sip of sophistication, cocktails blend flavors, creating a spirited symphony in every glass.", "/img/cocktail.png");

    private final String description;
    private final String image;

    CategoryName(String description, String image) {
        this.description = description;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}
