package com.rebound.models.Main;

public class LastCollectionItem {
    private String title;
    private String price;
    private int imageResId;

    public LastCollectionItem(String title, String price, int imageResId) {
        this.title = title;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}

