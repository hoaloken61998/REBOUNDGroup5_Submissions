package com.rebound.main;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rebound.R;
import com.rebound.utils.ContentLoader;

import java.util.Locale;

public class PrivacyPolicyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_privacy_policy_fragment, container, false);

        TextView txtContent = view.findViewById(R.id.txtTerms); // Chỉ dùng 1 TextView tổng
        ImageView imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // 🗂 Chọn file dựa trên ngôn ngữ hiện tại
        String lang = Locale.getDefault().getLanguage(); // ví dụ: "vi", "en"
        String fileName = lang.equals("vi") ? "privacy_policy_vi.html" : "privacy_policy_en.html";

        // 📥 Nạp nội dung từ file HTML
        String htmlContent = ContentLoader.loadAssetText(requireContext(), fileName);
        txtContent.setText(Html.fromHtml(htmlContent));
        txtContent.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}
