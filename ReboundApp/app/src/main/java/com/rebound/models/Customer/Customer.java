package com.rebound.models.Customer;

import java.io.Serializable;

public class Customer implements Serializable {
    private String username;
    private String email;
    private String password;

    private String phone;
    private String fullName;
    private String avatarUrl;
    private String gender; // male/female/other


    public Customer() {
    }

    public Customer(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Customer(String username, String email, String password,
                    String fullName, String avatarUrl, String gender, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    // Getter và Setter cho tất cả trường
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
