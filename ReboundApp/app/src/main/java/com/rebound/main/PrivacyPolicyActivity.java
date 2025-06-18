package com.rebound.main;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rebound.R;
import com.rebound.utils.ContentLoader;

import java.util.Locale;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy_fragment); // dùng chung layout

        TextView txtContent = findViewById(R.id.txtTerms); // TextView tổng
        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        // Xác định ngôn ngữ và chọn file HTML tương ứng
        String lang = Locale.getDefault().getLanguage();
        String fileName = lang.equals("vi") ? "privacy_policy_vi.html" : "privacy_policy_en.html";

        // Nạp nội dung
        String html = ContentLoader.loadAssetText(this, fileName);
        txtContent.setText(Html.fromHtml(html));
        txtContent.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
