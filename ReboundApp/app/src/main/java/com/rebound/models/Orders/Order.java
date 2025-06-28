package com.rebound.models.Orders;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class Order {
    @PropertyName("OrderID")
    public Object OrderID;
    @PropertyName("UserID")
    public Object UserID;
    @PropertyName("PaymentMethodID")
    public Object PaymentMethodID;
    @PropertyName("DeliveryFee")
    public Object DeliveryFee;
    @PropertyName("DiscountValue")
    public Object DiscountValue;
    @PropertyName("Subtotal")
    public Object Subtotal;
    @PropertyName("TotalAmount")
    public Object TotalAmount;
    @PropertyName("UserPromotion")
    public Object UserPromotion;
    @PropertyName("OrderDate")
    public Object OrderDate;
    @PropertyName("Status")
    public Object Status;
    @PropertyName("productList")
    public List<com.rebound.models.Cart.ProductItem> productList;
    @PropertyName("total")
    public Object total;

    public Order() {}

    public String getOrderID() {
        return objectToString(OrderID);
    }
    public String getUserID() {
        return objectToString(UserID);
    }
    public String getPaymentMethodID() {
        return objectToString(PaymentMethodID);
    }
    public String getDeliveryFee() {
        return objectToString(DeliveryFee);
    }
    public String getDiscountValue() {
        return objectToString(DiscountValue);
    }
    public String getSubtotal() {
        return objectToString(Subtotal);
    }
    public String getTotalAmount() {
        return objectToString(TotalAmount);
    }
    public String getUserPromotion() {
        return objectToString(UserPromotion);
    }
    public String getOrderDate() {
        return objectToString(OrderDate);
    }
    public String getStatus() {
        return objectToString(Status);
    }
    public void setStatus(String status) {
        this.Status = status;
    }
    public List<com.rebound.models.Cart.ProductItem> getProductList() {
        return productList;
    }
    public void setProductList(List<com.rebound.models.Cart.ProductItem> productList) {
        this.productList = productList;
    }

    private String objectToString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        if (obj instanceof Number) return String.valueOf(obj);
        return obj.toString();
    }
}
