package com.rebound.models.Orders;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class Order {
    @PropertyName("OrderID")
    public Long OrderID;
    @PropertyName("UserID")
    public Long UserID;
    @PropertyName("PaymentMethodID")
    public Long PaymentMethodID;
    @PropertyName("DeliveryFee")
    public Long DeliveryFee;
    @PropertyName("DiscountValue")
    public Double DiscountValue;
    @PropertyName("Subtotal")
    public Long Subtotal;
    @PropertyName("TotalAmount")
    public Long TotalAmount;
    @PropertyName("UserPromotion")
    public Long UserPromotion;
    @PropertyName("OrderDate")
    public String OrderDate;
    @PropertyName("Status")
    public String Status;

    public Order() {
    }

    public Order(Long orderID, Long userID, Long paymentMethodID, Long deliveryFee, Double discountValue, Long subtotal, Long totalAmount, Long userPromotion, String orderDate, String status) {
        OrderID = orderID;
        UserID = userID;
        PaymentMethodID = paymentMethodID;
        DeliveryFee = deliveryFee;
        DiscountValue = discountValue;
        Subtotal = subtotal;
        TotalAmount = totalAmount;
        UserPromotion = userPromotion;
        OrderDate = orderDate;
        Status = status;
    }


}
