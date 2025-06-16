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

    public String variant = "Silver"; // ✅ Thêm phân loại mặc định

    // Constructor đầy đủ cho phần ProductDetail
    public ProductItem(String title, String price, int imageRes, String rating, String sold,
                       String description, int imageGoldRes, int imageSilverRes) {
        this.title = title;
        this.price = price;
        this.imageRes = imageRes;
        this.rating = rating;
        this.sold = sold;
        this.description = description;
        this.imageGoldRes = imageGoldRes;
        this.imageSilverRes = imageSilverRes;
        this.variant = "Silver"; // ✅ Mặc định Silver, có thể thay đổi sau
    }

    // Constructor cũ (nếu vẫn dùng chỗ khác, giữ lại để không bị lỗi)
    public ProductItem(String title, String price, int imageRes, String rating) {
        this.title = title;
        this.price = price;
        this.imageRes = imageRes;
        this.rating = rating;
    }

    public String getName() {
        return title;
    }

    // ✅ Optional: Getter/Setter nếu muốn xài tiện hơn
    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductItem that = (ProductItem) o;
        return title.equals(that.title) &&
                variant.equals(that.variant); // Phân biệt Gold / Silver
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + variant.hashCode();
        return result;
    }
}
