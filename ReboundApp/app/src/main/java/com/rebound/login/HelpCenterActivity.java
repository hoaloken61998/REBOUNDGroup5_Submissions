package com.rebound.login;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rebound.R;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HelpCenterActivity extends AppCompatActivity {

    private LinearLayout faqContainer;
    private Typeface montserratRegular;
    private Typeface montserratBold;
    private Typeface montserratItalic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_center);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout searchBar = findViewById(R.id.searchbarHelpcenterSearch);

        searchBar.setOnClickListener(v -> {
            // Ví dụ mở activity tìm kiếm
            // startActivity(new Intent(HelpCenterActivity.this, SearchActivity.class));

            // Hoặc hiển thị log hoặc Toast tạm thời
            Toast.makeText(HelpCenterActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
        });

        // ✅ Khởi tạo biến và layout sau khi setContentView
        TextView tabFaqs = findViewById(R.id.txtHelpcenterFaqsText);
        TextView tabContact = findViewById(R.id.txtHelpcenterContactUs);
        FrameLayout tabContent = findViewById(R.id.layoutHelpcenterInfo);
        LayoutInflater inflater = LayoutInflater.from(this);

        ScrollView contactUsView = new ScrollView(this);
        LinearLayout contactContainer = new LinearLayout(this);
        contactContainer.setOrientation(LinearLayout.VERTICAL);
        contactContainer.setPadding(32, 32, 32, 32);
        contactUsView.addView(contactContainer);
        // Thêm các liên hệ
        addContactsToLayout(contactContainer);
        View faqsView = inflater.inflate(R.layout.layout_faqs, null);

        // Thêm mặc định FAQs
        tabContent.addView(faqsView);

        // Handle switching tabs
        tabFaqs.setOnClickListener(v -> {
            tabFaqs.setBackgroundResource(R.drawable.tab_selected);
            tabFaqs.setTextColor(Color.WHITE);
            tabContact.setBackgroundColor(Color.TRANSPARENT);
            tabContact.setTextColor(Color.parseColor("#847c4a"));

            tabContent.removeAllViews();
            tabContent.addView(faqsView);
        });

        tabContact.setOnClickListener(v -> {
            tabContact.setBackgroundResource(R.drawable.tab_selected);
            tabContact.setTextColor(Color.WHITE);
            tabFaqs.setBackgroundColor(Color.TRANSPARENT);
            tabFaqs.setTextColor(Color.parseColor("#847c4a"));

            tabContent.removeAllViews();
            tabContent.addView(contactUsView);
        });

        // Tiếp tục xử lý FAQs
        faqContainer = faqsView.findViewById(R.id.layoutFaqsContainer); // từ layout_faqs.xml
        montserratRegular = ResourcesCompat.getFont(this, R.font.montserrat_regular);
        montserratBold = ResourcesCompat.getFont(this, R.font.montserrat_bold);
        montserratItalic  = ResourcesCompat.getFont(this, R.font.montserrat_italic);
        Map<String, List<FAQItem>> faqData = getFaqData();
        addFaqsToLayout(faqData);
    }

    private Map<String, List<FAQItem>> getFaqData() {
        Map<String, List<FAQItem>> data = new LinkedHashMap<>();

        data.put("DELIVERY", List.of(
                new FAQItem("What happens if I miss my delivery?", "We will reschedule the delivery on the next available date."),
                new FAQItem("What is the estimated delivery time?", "Delivery takes 2–3 business days depending on your location."),
                new FAQItem("Can I change my delivery address after placing an order?", "Yes, before the order is shipped."),
                new FAQItem("Is there an express delivery service?", "Yes, within city: 2-4 hrs, nearby: 1-2 days, others: 2-3 days.")
        ));

        data.put("ORDER", List.of(
                new FAQItem("How can I track my order?", "You will receive a tracking link via email."),
                new FAQItem("Can I cancel my order after confirmation?", "Only before shipment."),
                new FAQItem("What should I do if I receive a wrong item?", "Please contact support immediately."),
                new FAQItem("Is it possible to modify my order after checkout?", "You can edit your order before it ships.")
        ));

        data.put("PRICING", List.of(
                new FAQItem("How is the delivery cost calculated?", "It depends on weight and distance."),
                new FAQItem("Are there any hidden charges?", "No, all charges are shown at checkout."),
                new FAQItem("Do you offer discounts for bulk orders?", "Yes, for orders over 10 items."),
                new FAQItem("What payment methods are accepted?", "Visa, MasterCard, PayPal, COD.")
        ));

        data.put("SERVICE", List.of(
                new FAQItem("Do you deliver on weekends?", "Yes, but only in selected areas."),
                new FAQItem("What is your return policy?", "Returns are accepted within 14 days."),
                new FAQItem("Do you provide installation services?", "Yes, available for selected products."),
                new FAQItem("Can I schedule a specific delivery time?", "Yes, contact support to arrange this.")
        ));

        return data;
    }

    private void addFaqsToLayout(Map<String, List<FAQItem>> faqData) {
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map.Entry<String, List<FAQItem>> entry : faqData.entrySet()) {
            String groupTitle = entry.getKey();

            TextView groupHeader = new TextView(this);
            groupHeader.setText(groupTitle);
            groupHeader.setTextSize(20);
            groupHeader.setTextColor(Color.parseColor("#7C6F34"));
            int marginStart = (int) (getResources().getDisplayMetrics().density * 12); // 16dp
            groupHeader.setPadding(marginStart, 24, marginStart, 12);
            groupHeader.setTypeface(montserratBold);
            faqContainer.addView(groupHeader);

            for (FAQItem item : entry.getValue()) {
                View faqItemView = inflater.inflate(R.layout.faq_item, faqContainer, false);
                TextView question = faqItemView.findViewById(R.id.txtFaqsQuestion);
                TextView answer = faqItemView.findViewById(R.id.txtFaqsAnswer);
                ImageView toggleIcon = faqItemView.findViewById(R.id.imgFaqsButtonAdd);

                question.setText(item.question);
                question.setTypeface(montserratRegular);
                answer.setText(item.answer);
                answer.setTypeface(montserratItalic);

                faqItemView.findViewById(R.id.layoutFaqsQuestion).setOnClickListener(v -> {
                    if (answer.getVisibility() == View.GONE) {
                        answer.setVisibility(View.VISIBLE);
                        toggleIcon.setImageResource(R.mipmap.ic_minus_detail);
                    } else {
                        answer.setVisibility(View.GONE);
                        toggleIcon.setImageResource(R.mipmap.ic_plus_detail);
                    }
                });

                faqContainer.addView(faqItemView);
            }
        }
    }

    private static class FAQItem {
        String question, answer;

        FAQItem(String q, String a) {
            this.question = q;
            this.answer = a;
        }
    }
    private static class ContactItem {
        String title, detail;
        int iconResId;

        ContactItem(String title, String detail, int iconResId) {
            this.title = title;
            this.detail = detail;
            this.iconResId = iconResId;
        }
    }
    private void addContactsToLayout(LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(this);
        List<ContactItem> contacts = List.of(
                new ContactItem("Customer Service", "090-767-767", R.mipmap.ic_customer_service_help),
                new ContactItem("Facebook", "facebook.com/reboundpiercing", R.mipmap.ic_facebook_help),
                new ContactItem("Website", "www.reboundpiericng.vn", R.mipmap.ic_web_help),
                new ContactItem("WhatsApp", "012-345-6789", R.mipmap.ic_whatsapp_help),
                new ContactItem("Twitter", "twitter.com/reboundpiercing", R.mipmap.ic_twitter_help),
                new ContactItem("Instagram", "instagram.com/reboundpiercing", R.mipmap.ic_instagram_help)
        );

        for (ContactItem item : contacts) {
            View view = inflater.inflate(R.layout.contact_item, container, false);
            ImageView icon = view.findViewById(R.id.imgContactIcon);
            TextView title = view.findViewById(R.id.txtContactusContactname);
            TextView detail = view.findViewById(R.id.txtContactusContactDetail);
            ImageView toggleIcon = view.findViewById(R.id.imgContactusButtonMore);

            icon.setImageResource(item.iconResId);
            title.setText(item.title);
            detail.setText(item.detail);

            if (item.detail == null || item.detail.isEmpty()) {
                detail.setVisibility(View.GONE);
                toggleIcon.setVisibility(View.GONE);
            } else {
                detail.setVisibility(View.GONE);
                toggleIcon.setImageResource(R.mipmap.ic_minus_detail);
            }

            view.findViewById(R.id.contact_item_layout).setOnClickListener(v -> {
                if (detail.getVisibility() == View.GONE) {
                    detail.setVisibility(View.VISIBLE);
                    toggleIcon.setImageResource(R.mipmap.ic_minus_detail);
                } else {
                    detail.setVisibility(View.GONE);
                    toggleIcon.setImageResource(R.mipmap.ic_minus_detail);
                }
            });

            container.addView(view);
        }
    }
}

