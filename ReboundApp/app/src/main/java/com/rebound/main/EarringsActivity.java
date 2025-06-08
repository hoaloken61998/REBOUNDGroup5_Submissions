package com.rebound.main;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.ProductAdapter;
import com.rebound.data.ProductData;
import com.rebound.models.Cart.ProductItem;

import java.util.List;

public class EarringsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_earrings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<ProductItem> earringList = ProductData.getEarringList();

        RecyclerView earringsRecyclerView = findViewById(R.id.recyclerViewEarrings);
        earringsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        earringsRecyclerView.setPadding(24, 0, 24, 0); // thêm padding ngang
        earringsRecyclerView.setClipToPadding(false);
        earringsRecyclerView.setClipChildren(false);

        ProductAdapter adapter = new ProductAdapter(earringList);
        earringsRecyclerView.setAdapter(adapter);
    }

}