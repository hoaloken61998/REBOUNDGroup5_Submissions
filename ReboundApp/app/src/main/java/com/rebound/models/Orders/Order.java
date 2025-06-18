package com.rebound.models.Orders;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private final List<Product> productList;
    private final String total; // Ví dụ: "330.000 VND"
    private String status; // "Shipped" hoặc "To Receive"

    public Order(List<Product> productList, String status) {
        this.productList = productList;
        this.total = calculateFormattedTotal(productList);
        this.status = status;
    }

    public Order(List<Product> productList, String total, String status) {
        this.productList = productList;
        this.total = total;
        this.status = status;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 🧮 Tính tổng tiền từ danh sách sản phẩm
    private String calculateFormattedTotal(List<Product> products) {
        BigDecimal sum = BigDecimal.ZERO;

        for (Product product : products) {
            try {
                String raw = product.getPrice().replaceAll("[^\\d]", ""); // Chỉ lấy phần số
                BigDecimal price = new BigDecimal(raw);
                sum = sum.add(price);
            } catch (Exception e) {
                e.printStackTrace(); // fallback nếu lỗi parse
            }
        }

        return String.format("%,.0f VND", sum.doubleValue());
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Order order = (Order) obj;

        // So sánh theo total + status + productList
        return total.equals(order.total)
                && status.equals(order.status)
                && productList.equals(order.productList);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(total, status, productList);
    }

}
