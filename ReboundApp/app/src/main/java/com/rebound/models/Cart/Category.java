package com.rebound.models.Cart;

public class Category {
    // --- Firebase fields ---
    public String CategoryName;
    public Long CategoryID;

    public Category() {
        // Default constructor required for Firebase
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
    public Long getCategoryID() {
        return CategoryID;
    }
}
