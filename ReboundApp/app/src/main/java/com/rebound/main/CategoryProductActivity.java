package com.rebound.main;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.ProductAdapter;
import com.rebound.data.ProductData;
import com.rebound.models.Cart.ProductItem;

import java.util.List;

public class CategoryProductActivity extends AppCompatActivity {
    ImageView imgBackCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        String category = getIntent().getStringExtra("category");

        TextView txtTitle = findViewById(R.id.txtCategoryTitle);
        txtTitle.setText(category);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategoryProduct);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<ProductItem> productList;
        switch (category) {
            case "Necklaces":
                productList = ProductData.getNecklaceList();
                break;
            case "Earrings":
                productList = ProductData.getEarringList();
                break;
            case "Rings":
                productList = ProductData.getRingList();
                break;
            case "Body Piercing":
                productList = ProductData.getBodyPiercingList();
                break;
            default:
                productList = ProductData.getEarringList();
        }

        ProductAdapter adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        imgBackCategory = findViewById(R.id.imgBackCategory);
        imgBackCategory.setOnClickListener(v -> finish());
    }
}
