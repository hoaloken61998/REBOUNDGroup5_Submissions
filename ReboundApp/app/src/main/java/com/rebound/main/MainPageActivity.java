package com.rebound.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.rebound.adapters.ProductAdapter;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.LastCollectionAdapter;
import com.rebound.data.ProductData;
import com.rebound.utils.CartManager;

public class MainPageActivity extends AppCompatActivity {
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CartManager.init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//
//        // Gán sự kiện click cho các nút phân loại
//        findViewById(R.id.btnMainPageNecklaces).setOnClickListener(v -> openCategory("Necklaces"));
//        findViewById(R.id.btnMainPageEarrings).setOnClickListener(v -> openCategory("Earrings"));
//        findViewById(R.id.btnMainPageRings).setOnClickListener(v -> openCategory("Rings"));
//        findViewById(R.id.btnMainPageBodyPiercing).setOnClickListener(v -> openCategory("Body Piercing"));
//
//        // Khởi tạo RecyclerView cho "Latest Collection"
//        RecyclerView recyclerView = findViewById(R.id.recyclerViewLastCollection);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        LastCollectionAdapter adapter = new LastCollectionAdapter(ProductData.getLastCollectionList());
//        recyclerView.setAdapter(adapter);
//
//        //Link shopping cart
//        ImageView imgCart = findViewById(R.id.imgMainPageShoppingCart);
//        imgCart.setOnClickListener(v -> {
//            Intent intent = new Intent(MainPageActivity.this, com.rebound.checkout.ShoppingCartActivity.class);
//            startActivity(intent);
//        });
//    }
//
//    private void openCategory(String category) {
//        Intent intent = new Intent(this, CategoryProductActivity.class);
//        intent.putExtra("category", category);
//        startActivity(intent);
//    }
    }}
