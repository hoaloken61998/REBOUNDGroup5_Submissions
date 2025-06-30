package com.rebound.models.Cart;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Address implements Serializable {
    @SerializedName("AddressID")
    private Long AddressID;
    @SerializedName("UserID")
    private Long UserID;
    @SerializedName("ReceiverName")
    private String ReceiverName;
    @SerializedName("ReceiverPhone")
    private Long ReceiverPhone;
    @SerializedName("Province")
    private String Province;
    @SerializedName("District")
    private Object District;
    @SerializedName("Ward")
    private Object Ward;
    @SerializedName("Street")
    private String Street;
    @SerializedName("Details")
    private String Details;
    @SerializedName("IsDefault")
    private String IsDefault;

    public Address() {}

    public Address(Long addressID, Long userID, String receiverName, Long receiverPhone, String province, Object district, Long ward, String street, String details, String isDefault) {
        AddressID = addressID;
        UserID = userID;
        ReceiverName = receiverName;
        ReceiverPhone = receiverPhone;
        Province = province;
        District = district;
        Ward = ward;
        Street = street;
        Details = details;
        IsDefault = isDefault;
    }

    public Long getAddressID() {
        return AddressID;
    }

    public void setAddressID(Long addressID) {
        AddressID = addressID;
    }

    public Long getUserID() {
        return UserID;
    }

    public void setUserID(Long userID) {
        UserID = userID;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public Long getReceiverPhone() {
        return ReceiverPhone;
    }

    public void setReceiverPhone(Long receiverPhone) {
        ReceiverPhone = receiverPhone;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getDistrict() {
        if (District == null) return null;
        if (District instanceof String) return (String) District;
        return String.valueOf(District);
    }

    public void setDistrict(Object district) {
        this.District = district;
    }

    public Object getWard() {
        return Ward;
    }

    public void setWard(Long ward) {
        Ward = ward;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(String isDefault) {
        IsDefault = isDefault;
    }
}
