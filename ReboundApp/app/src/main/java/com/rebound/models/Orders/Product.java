package com.rebound.models.Orders;

public class Product {
    private String name;
    private String variant;      // ✅ thêm variant
    private String price;
    private int imageResId;

    public Product(String name, String variant, String price, int imageResId) {
        this.name = name;
        this.variant = variant;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getVariant() {
        return variant != null ? variant : "Default"; // ✅ fallback nếu null
    }

    public String getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }
}
