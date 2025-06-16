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
    private ImageView imgDetailNews, imgBackNewsDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        txtDetailNewsTitle = findViewById(R.id.txtDetailNewsTitle);
        txtDescriptionDetailNews = findViewById(R.id.txtDescriptionDetailNews);
        txtDateDetailNews = findViewById(R.id.txtDateDetailNews);
        imgDetailNews = findViewById(R.id.imgDetailNews);
        imgBackNewsDetail = findViewById(R.id.imgBackNewsDetail);

        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("desc");
        String date = getIntent().getStringExtra("date");
        int imageResId = getIntent().getIntExtra("imageResId", R.mipmap.ic_launcher); // fallback nếu null

        txtDetailNewsTitle.setText(title);
        txtDescriptionDetailNews.setText(desc);
        txtDateDetailNews.setText(date);
        imgBackNewsDetail.setOnClickListener(v -> finish());

        Glide.with(this).load(imageResId).into(imgDetailNews);
    }
}