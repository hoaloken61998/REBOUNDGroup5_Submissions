package com.rebound.models.Cart;

public class CartItem {
    private String name;
    private String variant;
    private int price;
    private int quantity;
    private int imageResId;

    public CartItem(String name, String variant, int price, int quantity, int imageResId) {
        this.name = name;
        this.variant = variant;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getVariant() { return variant; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getImageResId() { return imageResId; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getTotalPrice() { return price * quantity; }
}