package com.zamashops.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.zamashops.R;
import com.zamashops.adapters.FullScreenImageAdapter;

import java.util.ArrayList;

public class FullScreenViewActivity extends AppCompatActivity {

    String[] images;

    ViewPager pager;
    FullScreenImageAdapter fullScreenImageAdapter;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        pager = findViewById(R.id.pager);

        if(getIntent().hasExtra("images")){
            images = getIntent().getStringArrayExtra("images");
            position = getIntent().getIntExtra("position",0);
        }else {
            finish();
        }

        ArrayList<String> _images = new ArrayList<>();
        for(int i=0; i<images.length; i++) {
            _images.add(images[i]);
        }

        fullScreenImageAdapter = new FullScreenImageAdapter(FullScreenViewActivity.this,_images);
        pager.setAdapter(fullScreenImageAdapter);
        pager.setCurrentItem(position);

    }
}