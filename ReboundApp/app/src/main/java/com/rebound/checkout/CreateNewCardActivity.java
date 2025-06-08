package com.rebound.checkout;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.rebound.R;
import com.rebound.adapters.ViewPaperAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;


public class CreateNewCardActivity extends AppCompatActivity {


    private ViewPager2 viewPagerCards;
    private DotsIndicator dotsIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_card);


        viewPagerCards = findViewById(R.id.viewPagerCards);
        dotsIndicator = findViewById(R.id.dotsIndicator);


        int[] imageResIds = {
                R.mipmap.visa_logo,
                R.mipmap.mastercard_logo,
                R.mipmap.momo_logo
        };


        ViewPaperAdapter adapter = new ViewPaperAdapter(this, imageResIds);
        viewPagerCards.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPagerCards);
    }
}

