package com.rebound.models.Orders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String id;
    private final List<Product> productList;
    private final String total;
    private String status;

    private String formatTotal(int rawTotal) {
        return String.format("%,d VND", rawTotal).replace(',', '.');
    }


    public Order(List<Product> productList, int rawTotal, String status) {
        this.id = UUID.randomUUID().toString();
        this.productList = productList;
        this.total = formatTotal(rawTotal); // format từ int
        this.status = status;
    }
    public Order(List<Product> productList, String total, String status) {
        this.id = UUID.randomUUID().toString();
        this.productList = productList;
        this.total = total;
        this.status = status;
    }


    public Order(String id, List<Product> productList, String total, String status) {
        this.id = id;
        this.productList = productList;
        this.total = total;
        this.status = status;
    }

    public String getId() {
        return id;
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

    // Tính tổng tiền
    private String calculateFormattedTotal(List<Product> products) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Product product : products) {
            try {
                String raw = product.getPrice().replaceAll("[^\\d]", ""); // Chỉ lấy số
                BigDecimal price = new BigDecimal(raw);
                sum = sum.add(price);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return String.format("%,.0f VND", sum.doubleValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Order order = (Order) obj;
        return id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
