package com.zamashops.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;
import com.zamashops.R;
import com.zamashops.activities.MainCategoryActivity;
import com.zamashops.activities.SubCategoryActivity;
import com.zamashops.models.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryListViewHolder> {

    Context context;
    ArrayList<CategoryModel> items;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_main_category, viewGroup, false);



        return new CategoryAdapter.CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryListViewHolder viewHolder, final int i) {
        final CategoryModel item = items.get(i);

        viewHolder.txt_category.setText(item.getCat_name());

        if(i == items.size()-1){
            viewHolder.img_category.setImageResource(R.drawable.ic_format_list_bulleted_white_24dp);
        }else{


            Picasso.get().load(context.getResources().getString(R.string.category_images) + item.getCat_image())
                    .into(viewHolder.img_category);


        }



        viewHolder.lin_main_category_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i == items.size()-1){
                    Intent intent = new Intent(context, MainCategoryActivity.class);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, SubCategoryActivity.class);
                    intent.putExtra("cat_id",items.get(i).getCat_id());
                    intent.putExtra("cat_name",items.get(i).getCat_name());
                    context.startActivity(intent);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CategoryListViewHolder extends RecyclerView.ViewHolder {

        ImageView img_category;
        TextView txt_category;
        LinearLayout lin_main_category_item;

        public CategoryListViewHolder(@NonNull View itemView) {
            super(itemView);

            img_category =  itemView.findViewById(R.id.img_category);
            txt_category =  itemView.findViewById(R.id.txt_category);
            lin_main_category_item =  itemView.findViewById(R.id.lin_main_category_item);


        }
    }




}