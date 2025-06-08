package com.rebound.main;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.internal.FlowLayout;
import com.rebound.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SearchActivity extends AppCompatActivity {


    private SearchView searchView;
    private FlowLayout searchRecentContainer, searchPopularContainer;


    private final String PREF_NAME = "search_prefs";
    private final String KEY_RECENT = "recent_keywords";
    private final String KEY_POPULAR = "popular_keywords";


    private SharedPreferences prefs;
    private LinkedList<String> recentList = new LinkedList<>();
    private HashMap<String, Integer> popularMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        searchView = findViewById(R.id.searchView);
        searchRecentContainer = findViewById(R.id.searchRecentContainer);
        searchPopularContainer = findViewById(R.id.searchPopularContainer);


        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadData();
        updateUI();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query.trim());
                searchView.clearFocus();
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    private void handleSearch(String query) {
        if (query.isEmpty()) return;


        recentList.remove(query);
        recentList.addFirst(query);
        if (recentList.size() > 10) recentList.removeLast();


        int count = popularMap.getOrDefault(query, 0);
        popularMap.put(query, count + 1);


        saveData();
        updateUI();
    }


    private void updateUI() {
        searchRecentContainer.removeAllViews();
        for (String keyword : recentList) {
            TextView chip = createChip(keyword);
            searchRecentContainer.addView(chip);
        }


        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(popularMap.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());


        searchPopularContainer.removeAllViews();
        for (int i = 0; i < Math.min(10, sorted.size()); i++) {
            String keyword = sorted.get(i).getKey();
            TextView chip = createChip(keyword);
            searchPopularContainer.addView(chip);
        }
    }


    private TextView createChip(String text) {
        TextView chip = new TextView(this);
        chip.setText(text);
        chip.setTextSize(14);
        chip.setAllCaps(false);
        chip.setBackgroundResource(R.drawable.bg_chip); // Bo tròn, nền nhẹ
        chip.setTextColor(getResources().getColor(android.R.color.black));
        chip.setPadding(48, 24, 48, 24);
        chip.setGravity(Gravity.CENTER);


        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(16, 16, 16, 16);
        chip.setLayoutParams(lp);
        return chip;
    }


    private void saveData() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_RECENT, String.join(",", recentList));


        StringBuilder popularBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : popularMap.entrySet()) {
            popularBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }
        editor.putString(KEY_POPULAR, popularBuilder.toString());


        editor.apply();
    }


    private void loadData() {
        recentList.clear();
        popularMap.clear();


        String recentRaw = prefs.getString(KEY_RECENT, "");
        if (!recentRaw.isEmpty()) {
            recentList.addAll(Arrays.asList(recentRaw.split(",")));
        }


        String popularRaw = prefs.getString(KEY_POPULAR, "");
        String[] popularPairs = popularRaw.split(",");
        for (String pair : popularPairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                try {
                    popularMap.put(parts[0], Integer.parseInt(parts[1]));
                } catch (NumberFormatException ignored) {}
            }
        }
    }
}



