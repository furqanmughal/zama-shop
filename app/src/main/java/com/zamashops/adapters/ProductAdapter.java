package com.zamashops.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.zamashops.R;
import com.zamashops.activities.ProductDetailActivity;
import com.zamashops.models.ProductModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.CategoryListViewHolder> {

    Context context;
    ArrayList<ProductModel> items;

    public ProductAdapter(Context context, ArrayList<ProductModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_card_gallery_post, viewGroup, false);

        return new ProductAdapter.CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryListViewHolder viewHolder, int i) {
        final ProductModel item = items.get(i);



        viewHolder.txt_list_product_name.setText(item.getName());
        viewHolder.txt_list_product_des.setText(item.getDescripition());
        viewHolder.txt_list_product_price.setText(item.getPrice() + " PKR");


        viewHolder.product_hightlight_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String file_name = context.getClass().toString().substring(context.getClass().toString().lastIndexOf(".") + 1);
                if(file_name.equals("ProductDetailActivity")){
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("product_id", item.getProduct_id());
                    ((Activity) context).finish();
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("product_id", item.getProduct_id());
                    context.startActivity(intent);
                }


            }
        });




        if (item.getProduct_images().length > 0) {

            Glide
                    .with(context)
                    .load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
                    .centerCrop()
                    .into(viewHolder.img_list_product);

//            Picasso.get().load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
//                    .placeholder(R.drawable.loding_blue)
//                    .error(R.drawable.image_not_found)
//                    .into(viewHolder.img_list_product);

        }else{
            viewHolder.img_list_product.setImageResource(R.drawable.empty_logo);
        }




    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class CategoryListViewHolder extends RecyclerView.ViewHolder {

        ImageView img_list_product;
        TextView txt_list_product_name,txt_list_product_price,txt_list_product_des;
        CardView product_hightlight_card;

        public CategoryListViewHolder(@NonNull View itemView) {
            super(itemView);

            product_hightlight_card =  itemView.findViewById(R.id.product_hightlight_card);
            txt_list_product_des =  itemView.findViewById(R.id.txt_list_product_des);
            img_list_product =  itemView.findViewById(R.id.img_list_product);
            txt_list_product_name =  itemView.findViewById(R.id.txt_list_product_name);
            txt_list_product_price =  itemView.findViewById(R.id.txt_list_product_price);


        }
    }




}