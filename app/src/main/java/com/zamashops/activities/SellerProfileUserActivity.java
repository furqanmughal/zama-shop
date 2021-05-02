package com.zamashops.activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zamashops.R;
import com.zamashops.activities.ui.main.SectionsPagerAdapter;

public class SellerProfileUserActivity extends AppCompatActivity {

    String post_user_id = "";
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile_user);


        if (getIntent().hasExtra("post_user_id")) {
            post_user_id = getIntent().getStringExtra("post_user_id");
            position = getIntent().getIntExtra("position",0);
        }else{
            finish();
        }




        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(),post_user_id);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(position);

    }
}