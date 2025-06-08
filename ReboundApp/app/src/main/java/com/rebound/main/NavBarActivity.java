package com.rebound.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rebound.R;

public class NavBarActivity extends AppCompatActivity {
    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        bottomNavigationView = findViewById(R.id.layoutMainBottomNavigationView);
        drawerLayout = findViewById(R.id.layoutMainDrawer);
        Toolbar toolbar = findViewById(R.id.layoutMainToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        if (savedInstanceState == null){
            replaceFragment(new ShopNavActivity());
        }

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.txtNavigationShop) {
                replaceFragment(new ShopNavActivity());
            } else if (id == R.id.txtNavigationSchedule) {
                replaceFragment(new ScheduleNavActivity());
            } else if (id == R.id.txtNavigationNews) {
                replaceFragment(new NewsNavActivity());
            } else if (id == R.id.txtNavigationProfile) {
                replaceFragment(new ProfileNavActivity());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layoutMainFrame, fragment)
                .commit();
    }
}