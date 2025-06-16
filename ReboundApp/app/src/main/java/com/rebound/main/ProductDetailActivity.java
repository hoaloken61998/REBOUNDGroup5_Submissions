package com.rebound.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.rebound.R;
import com.rebound.utils.CartManager;
import com.rebound.checkout.ShoppingCartActivity;
import com.rebound.models.Cart.ProductItem;
import com.rebound.utils.WishlistManager;
import com.rebound.models.Customer.Customer;
import com.rebound.utils.SharedPrefManager;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage, heartIcon;
    private FrameLayout goldColor, silverColor;
    private ImageView checkGold, checkSilver;
    private TextView productTitle, soldText, ratingText, productDetailsContent;
    private TextView quantityValue, totalPriceValue;
    private ImageView btnDecrease, btnPlus;
    private String selectedColor = "Silver";

    private int quantity = 1;
    private boolean isHearted = false;
    private int pricePerItem = 0;
    private ProductItem currentItem;
    private boolean isGoldSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Ánh xạ view
        productImage = findViewById(R.id.imgProductdetailImage);
        goldColor = findViewById(R.id.drawableProductdetailColorGold);
        silverColor = findViewById(R.id.drawableProductdetailColorSilver);
        checkGold = findViewById(R.id.drawableProductdetailcolorGold);
        checkSilver = findViewById(R.id.drawableProductdetailCheckSilver);
        heartIcon = findViewById(R.id.imgProductdetailButtonHeart);

        productTitle = findViewById(R.id.txtProductdetailProductTitle);
        soldText = findViewById(R.id.drawableProductImageSold);
        ratingText = findViewById(R.id.txtProductdetailRatingText);
        productDetailsContent = findViewById(R.id.txtProductDetailContent);
        quantityValue = findViewById(R.id.txtProductDetailQuantityValue);
        totalPriceValue = findViewById(R.id.txtProductdetailTotalpriceValue);
        btnDecrease = findViewById(R.id.imgProductdetailButtonDecrease);
        btnPlus = findViewById(R.id.imgProductdetailButtonPlus);
        MaterialButton btnAddToCart = findViewById(R.id.btnProductdetailAddtocart);

        // Nhận dữ liệu ProductItem từ Intent
        Intent intent = getIntent();
        currentItem = (ProductItem) intent.getSerializableExtra("product");

        if (currentItem != null) {
            productTitle.setText(currentItem.title);
            ratingText.setText(currentItem.rating + " (53 reviews)");
            soldText.setText(currentItem.sold);
            productDetailsContent.setText(currentItem.description);
            productImage.setImageResource(currentItem.imageSilverRes); // mặc định màu bạc
            isGoldSelected = false;

            // Tính giá tiền
            try {
                String cleaned = currentItem.price.replace(".", "")
                        .replace(" VND", "")
                        .replace("vnd", "")
                        .trim();
                pricePerItem = Integer.parseInt(cleaned);
            } catch (Exception e) {
                pricePerItem = 0;
            }
        }

        // Quantity & total price
        quantityValue.setText(String.valueOf(quantity));
        updateTotalPrice();

        // Nút back
        ImageView backButton = findViewById(R.id.imgProductdetailButtonBack);
        backButton.setOnClickListener(v -> finish());

        // Switch màu
        goldColor.setOnClickListener(v -> {
            if (currentItem != null) {
                productImage.setImageResource(currentItem.imageGoldRes);
                isGoldSelected = true;
                checkGold.setVisibility(View.VISIBLE);
                checkSilver.setVisibility(View.GONE);
            }
        });

        silverColor.setOnClickListener(v -> {
            if (currentItem != null) {
                productImage.setImageResource(currentItem.imageSilverRes);
                isGoldSelected = false;
                checkGold.setVisibility(View.GONE);
                checkSilver.setVisibility(View.VISIBLE);
            }
        });

        // Yêu thích
        heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Customer currentCustomer = SharedPrefManager.getCurrentCustomer(ProductDetailActivity.this);
                if (currentCustomer == null) {
                    Toast.makeText(ProductDetailActivity.this, "Please log in to add to your wishlist.", Toast.LENGTH_SHORT).show();
                    return;
                }

                isHearted = !isHearted;

                if (isHearted) {
                    heartIcon.setImageResource(R.drawable.ic_heart_filled);

                    // Gán màu hiện tại cho biến variant
                    String selectedVariant = isGoldSelected ? "Gold" : "Silver";
                    ProductItem wishItem = new ProductItem(
                            currentItem.title,
                            currentItem.price,
                            isGoldSelected ? currentItem.imageGoldRes : currentItem.imageSilverRes,
                            currentItem.rating,
                            currentItem.sold,
                            currentItem.description,
                            currentItem.imageGoldRes,
                            currentItem.imageSilverRes
                    );
                    wishItem.setVariant(selectedVariant);

                    // Thêm vào danh sách yêu thích
                    WishlistManager.getInstance(ProductDetailActivity.this).addToWishlist(wishItem);
                    Toast.makeText(ProductDetailActivity.this, "Added to wishlist", Toast.LENGTH_SHORT).show();
                } else {
                    heartIcon.setImageResource(R.mipmap.ic_heart);
                    // (Tuỳ chọn: có thể thêm xóa khỏi wishlist ở đây)
                }
            }
        });

        // Tăng giảm số lượng
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityValue.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            quantityValue.setText(String.valueOf(quantity));
            updateTotalPrice();
        });

        // Thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            // Lấy username hiện tại từ SharedPreferences
            Customer currentCustomer = SharedPrefManager.getCurrentCustomer(this);
            if (currentCustomer == null) {
                Toast.makeText(this, "Please log in to add items to your cart", Toast.LENGTH_SHORT).show();
                return;
            }
            CartManager.getInstance().setUserEmail(currentCustomer.getEmail());


            // Nếu không phải guest => thêm vào giỏ hàng
            if (currentItem != null) {
                for (int i = 0; i < quantity; i++) {
                    ProductItem itemCopy = new ProductItem(
                            currentItem.title,
                            currentItem.price,
                            isGoldSelected ? currentItem.imageGoldRes : currentItem.imageSilverRes,
                            currentItem.rating,
                            currentItem.sold,
                            currentItem.description,
                            currentItem.imageGoldRes,
                            currentItem.imageSilverRes
                    );

// 🔹 Gán màu đã chọn cho variant
                    itemCopy.setVariant(isGoldSelected ? "Gold" : "Silver");
                    CartManager.getInstance().addToCart(itemCopy);
                }

                Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show();

                // Tùy chọn chuyển sang giỏ hàng
                Intent cartIntent = new Intent(this, ShoppingCartActivity.class);
                startActivity(cartIntent);
            }
        });
    }

    private void updateTotalPrice() {
        int totalPrice = pricePerItem * quantity;
        String priceText = String.format("%,d", totalPrice).replace(',', '.') + " VND";
        totalPriceValue.setText(priceText);
    }
}
