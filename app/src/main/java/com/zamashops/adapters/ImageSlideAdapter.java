package com.zamashops.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.zamashops.R;
import com.zamashops.activities.FullScreenViewActivity;

public class ImageSlideAdapter extends PagerAdapter {

    Context context;
    String[] images;

    public ImageSlideAdapter(Context context, String images[]) {
        this.context = context;
        this.images = images;
    }



    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager)container;
        View view = (View)object;
        viewPager.removeView(view);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image_slider,null);

        final ImageView img_slide = view.findViewById(R.id.img_slide);
        final TextView txt_count = view.findViewById(R.id.txt_count);


        Glide
                .with(context)
                .load(context.getResources().getString(R.string.url_image)  + images[position])
                .placeholder(R.drawable.infinity)
                .error(R.drawable.image_not_found)
                .into(img_slide);



        txt_count.setText(String.valueOf(position+1)+"/"+images.length);

        ViewPager viewPager = (ViewPager)container;
        viewPager.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FullScreenViewActivity.class);
                i.putExtra("position", position);
                i.putExtra("images",images);
                context.startActivity(i);
            }
        });

        return view;
    }
}
