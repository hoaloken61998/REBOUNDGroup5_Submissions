package com.rebound.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rebound.R;
import com.rebound.models.Main.NewsItem;
import com.rebound.main.NewsDetailActivity;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final Context context;
    private final List<NewsItem> items;

    public NewsAdapter(Context context, List<NewsItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = items.get(position);

        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());
        holder.date.setText(item.getDate());
        holder.image.setImageResource(item.getImageResId());

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("desc", item.getSubtitle());
            intent.putExtra("date", item.getDate());
            intent.putExtra("imageResId", item.getImageResId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle, date;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgThumbnailNews);
            title = itemView.findViewById(R.id.titleNews);
            subtitle = itemView.findViewById(R.id.txtNewsSubtitle);
            date = itemView.findViewById(R.id.txtNewsDate);
        }
    }
}
