package com.rebound.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.adapters.NewsAdapter;
import com.rebound.data.NewsData;
import com.rebound.models.Main.NewsItem;

import java.util.List;

public class NewsFragment extends Fragment {

    private RecyclerView recyclerViewNews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        recyclerViewNews = view.findViewById(R.id.recyclerViewNews);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy dữ liệu từ NewsData
        List<NewsItem> newsItems = NewsData.convertToNewsItems(NewsData.getSampleNews());

        // Thiết lập Adapter
        NewsAdapter adapter = new NewsAdapter(requireContext(), newsItems);
        recyclerViewNews.setAdapter(adapter);

        return view;
    }
}
