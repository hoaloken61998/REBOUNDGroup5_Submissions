package com.rebound.ar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.rebound.R;
import com.rebound.adapters.ProductARAdapter;
import com.rebound.data.ProductData;
import com.rebound.models.Cart.ProductItem;

import java.util.List;

public class ARCameraProductActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProductAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arcamera_product);

        recyclerViewProductAR = findViewById(R.id.recyclerViewProductAR);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewProductAR.setLayoutManager(layoutManager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewProductAR);

        // Lấy danh sách sản phẩm từ ProductData
        List<ProductItem> productList = ProductData.getEarringList();

        // Truyền vào adapter
        ProductARAdapter adapter = new ProductARAdapter(productList, this);
        recyclerViewProductAR.setAdapter(adapter);
    }
}