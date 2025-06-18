package com.rebound.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rebound.models.Cart.ProductItem;
import com.rebound.models.Customer.Customer;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {
    private static final String PREFS_NAME = "WishlistPrefs";

    private static WishlistManager instance;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    private final Context context;

    private WishlistManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static WishlistManager getInstance(Context context) {
        if (instance == null) {
            instance = new WishlistManager(context);
        }
        return instance;
    }

    // 🔑 Key riêng cho từng user
    private String getUserWishlistKey() {
        Customer currentUser = SharedPrefManager.getCurrentCustomer(context);
        if (currentUser != null && currentUser.getEmail() != null) {
            return "wishlist_" + currentUser.getEmail();  // hoặc dùng getUsername()
        }
        return "wishlist_guest";  // fallback nếu chưa login
    }

    public void addToWishlist(ProductItem product) {
        List<ProductItem> wishlist = getWishlist();
        if (!wishlist.contains(product)) {
            wishlist.add(product);
            saveWishlist(wishlist);
        }
    }

    public List<ProductItem> getWishlist() {
        String key = getUserWishlistKey();
        String json = sharedPreferences.getString(key, "");
        if (json.isEmpty()) return new ArrayList<>();
        return gson.fromJson(json, new TypeToken<List<ProductItem>>() {}.getType());
    }

    public void saveWishlist(List<ProductItem> list) {
        String key = getUserWishlistKey();
        sharedPreferences.edit().putString(key, gson.toJson(list)).apply();
    }

    public void removeFromWishlist(ProductItem product) {
        List<ProductItem> wishlist = getWishlist();
        if (wishlist.remove(product)) {
            saveWishlist(wishlist);
        }
    }

    public boolean isInWishlist(ProductItem product) {
        return getWishlist().contains(product);
    }

    public void clearWishlist() {
        String key = getUserWishlistKey();
        sharedPreferences.edit().remove(key).apply();
    }
}