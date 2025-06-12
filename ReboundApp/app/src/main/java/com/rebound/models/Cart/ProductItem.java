package com.rebound.models.Cart;

import java.io.Serializable;

public class ProductItem implements Serializable {
    public String title;
    public String price;
    public int imageRes;
    public String rating;
    public String sold;
    public String description;
    public int imageGoldRes;
    public int imageSilverRes;
    public int quantity = 1;

    // Constructor đầy đủ cho phần ProductDetail
    public ProductItem(String title, String price, int imageRes, String rating, String sold, String description, int imageGoldRes, int imageSilverRes) {
        this.title = title;
        this.price = price;
        this.imageRes = imageRes;
        this.rating = rating;
        this.sold = sold;
        this.description = description;
        this.imageGoldRes = imageGoldRes;
        this.imageSilverRes = imageSilverRes;
    }

    // Constructor cũ (nếu vẫn dùng chỗ khác, giữ lại để không bị lỗi)
    public ProductItem(String title, String price, int imageRes, String rating) {
        this.title = title;
        this.price = price;
        this.imageRes = imageRes;
        this.rating = rating;
    }
}
