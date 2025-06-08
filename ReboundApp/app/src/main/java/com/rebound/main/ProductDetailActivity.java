package com.rebound.main;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.rebound.R;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private FrameLayout goldColor, silverColor;
    private ImageView checkGold, checkSilver;

    private TextView productTitle, soldText, ratingText, productDetailsContent;
    private ImageView imgProductDetailImage;
    private ImageView heartIcon;
    private boolean isHearted = false;

    private TextView quantityValue, totalPriceValue;
    private ImageView btnDecrease, btnPlus; // bạn dùng ImageView hay Button thì khai báo tương ứng
    private int quantity = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = findViewById(R.id.imgProductdetailImage);
        goldColor = findViewById(R.id.drawableProductdetailColorGold);
        silverColor = findViewById(R.id.drawableProductdetailColorSilver);
        checkGold = findViewById(R.id.drawableProductdetailcolorGold);
        checkSilver = findViewById(R.id.drawableProductdetailCheckSilver);

        quantityValue = findViewById(R.id.txtProductDetailQuantityValue);
        totalPriceValue = findViewById(R.id.txtProductdetailTotalpriceValue);
        btnDecrease = findViewById(R.id.imgProductdetailButtonDecrease);
        btnPlus = findViewById(R.id.imgProductdetailButtonPlus);

        // Khởi tạo số lượng và giá hiển thị lần đầu
        quantityValue.setText(String.valueOf(quantity));
        updateTotalPrice();

        goldColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productImage.setImageResource(R.mipmap.ic_earring_gold);
                checkGold.setVisibility(View.VISIBLE);
                checkSilver.setVisibility(View.GONE);
            }
        });

        silverColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productImage.setImageResource(R.mipmap.ic_earring_silver);
                checkGold.setVisibility(View.GONE);
                checkSilver.setVisibility(View.VISIBLE);
            }
        });

        heartIcon = findViewById(R.id.imgProductdetailButtonHeart);

        heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isHearted = !isHearted; // Đảo trạng thái
                if (isHearted) {
                    heartIcon.setImageResource(R.mipmap.ic_heart_black); // đổi icon sang đen
                } else {
                    heartIcon.setImageResource(R.mipmap.ic_heart); // trở lại icon ban đầu
                }
            }
        });

        // Ánh xạ view
        productTitle = findViewById(R.id.txtProductdetailProductTitle);
        soldText = findViewById(R.id.drawableProductImageSold);
        ratingText = findViewById(R.id.txtProductdetailRatingText);
        productDetailsContent = findViewById(R.id.txtProductDetailContent);
        imgProductDetailImage = findViewById(R.id.imgProductdetailImage);

        // Thiết lập text từ Java
        productTitle.setText("Tote Earrings");
        soldText.setText("423 SOLD");
        ratingText.setText("4.3 (53 reviews)");
        productDetailsContent.setText("Designed in Denmark\n"
                + "Skillfully crafted in recycled sterling silver\n"
                + "Soft geometry trapeze shape with flat sides\n"
                + "BLACK is engraved on one side only");
        imgProductDetailImage.setImageResource(R.mipmap.ic_earring_silver);
        imgProductDetailImage.setImageResource(R.mipmap.ic_earring_gold);

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    quantityValue.setText(String.valueOf(quantity));
                    updateTotalPrice();
                }
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityValue.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });
    }

    private void updateTotalPrice() {
        int pricePerItem = 150000;
        int totalPrice = pricePerItem * quantity;

        // Định dạng giá như "150.000vnd"
        String priceText = String.format("%,d", totalPrice).replace(',', '.') + "vnd";
        totalPriceValue.setText(priceText);
    }
}