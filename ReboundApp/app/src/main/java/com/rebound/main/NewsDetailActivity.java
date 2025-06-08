package com.rebound.main;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.rebound.R;
import com.rebound.data.NewsData;
import com.rebound.models.Main.NewsDetailItem;

import java.util.List;
public class NewsDetailActivity extends AppCompatActivity {

    private TextView txtDetailNewsTitle, txtDescriptionDetailNews, txtDateDetailNews;
    private ImageView imgDetailNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        txtDetailNewsTitle = findViewById(R.id.txtDetailNewsTitle);
        txtDescriptionDetailNews = findViewById(R.id.txtDescriptionDetailNews);
        txtDateDetailNews = findViewById(R.id.txtDateDetailNews);
        imgDetailNews = findViewById(R.id.imgDetailNews);

        List<NewsDetailItem> newsList = NewsData.getSampleNews();
        if (!newsList.isEmpty()) {
            NewsDetailItem firstNews = newsList.get(0);

            txtDetailNewsTitle.setText(firstNews.getTitle());
            txtDescriptionDetailNews.setText(firstNews.getDescription());
            txtDateDetailNews.setText(firstNews.getDate());

            Glide.with(this)
                    .load(firstNews.getImage())
                    .into(imgDetailNews);
        }
    }
}