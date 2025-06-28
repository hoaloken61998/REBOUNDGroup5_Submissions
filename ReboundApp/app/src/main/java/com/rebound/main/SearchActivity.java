package com.rebound.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.LastCollectionAdapter;
import com.rebound.models.Cart.ProductItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private LastCollectionAdapter adapter;
    private RecyclerView recyclerView;
    private List<ProductItem> allProducts;
    private FlowLayout recentContainer, popularContainer;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "search_prefs";
    private static final String KEYWORDS_KEY = "recent_keywords";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        searchView = findViewById(R.id.searchView);
        recyclerView = new RecyclerView(this);
        allProducts = new ArrayList<>();
        // Load all products from Firebase
        com.rebound.connectors.FirebaseConnector.getAllItems(
            "Product",
            com.rebound.models.Cart.ProductItem.class,
            new com.rebound.callback.FirebaseListCallback<com.rebound.models.Cart.ProductItem>() {
                @Override
                public void onSuccess(ArrayList<com.rebound.models.Cart.ProductItem> result) {
                    allProducts.clear();
                    allProducts.addAll(result);
                    adapter.updateList(result);
                }
                @Override
                public void onFailure(String errorMessage) {
                    adapter.updateList(new ArrayList<>());
                }
            }
        );

        // RecyclerView setup (nếu muốn hiển thị kết quả ngay bên dưới layout)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LastCollectionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Gắn vào layout (bạn có thể thay thế bằng FrameLayout hay ViewStub)
        ((LinearLayout) findViewById(android.R.id.content)).addView(recyclerView);

        // Gắn query từ intent (nếu có)
        String query = getIntent().getStringExtra("query");
        if (query != null && !query.trim().isEmpty()) {
            searchView.setQuery(query, false);
            performSearch(query);
            saveSearchKeyword(query);
        }

        // Sự kiện khi nhập
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                saveSearchKeyword(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

        // Back
        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        // Load từ khóa
        recentContainer = findViewById(R.id.searchRecentContainer);
        popularContainer = findViewById(R.id.searchPopularContainer);
        loadRecentKeywords();
        loadPopularKeywords();
    }

    private void performSearch(String keyword) {
        List<ProductItem> filtered = new ArrayList<>();
        for (ProductItem item : allProducts) {
            String nameStr = item.ProductName != null ? item.ProductName.toString() : "";
            if (!nameStr.isEmpty() && nameStr.toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(item);
            }
        }

        adapter.updateData(filtered);

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSearchKeyword(String keyword) {
        String existing = prefs.getString(KEYWORDS_KEY, "");
        List<String> keywords = new ArrayList<>(Arrays.asList(existing.split(",")));
        keywords.remove(keyword); // remove duplicates
        keywords.add(0, keyword); // add latest first
        if (keywords.size() > 10) keywords = keywords.subList(0, 10);

        String newRaw = String.join(",", keywords);
        prefs.edit().putString(KEYWORDS_KEY, newRaw).apply();
        loadRecentKeywords();
    }

    private void loadRecentKeywords() {
        String raw = prefs.getString(KEYWORDS_KEY, "");
        recentContainer.removeAllViews();
        if (!raw.isEmpty()) {
            for (String kw : raw.split(",")) {
                addKeywordChip(kw, recentContainer);
            }
        }
    }

    private void loadPopularKeywords() {
        List<String> popular = Arrays.asList("Necklace", "Gold", "Minimal", "Gem", "Earring");
        for (String kw : popular) {
            addKeywordChip(kw, popularContainer);
        }
    }

    private void addKeywordChip(String keyword, FlowLayout container) {
        TextView chip = new TextView(this);
        chip.setText(keyword);
        chip.setPadding(24, 12, 24, 12);
        chip.setBackgroundResource(R.drawable.bg_keyword_chip); // drawable rounded background
        chip.setTextSize(14);
        chip.setTextColor(getResources().getColor(R.color.black));
        chip.setOnClickListener(v -> {
            searchView.setQuery(keyword, true);
        });
        container.addView(chip);
    }
}
