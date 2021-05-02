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
import com.zamashops.R;
import com.zamashops.activities.ProductDetailActivity;
import com.zamashops.models.ProductModel;
import com.zamashops.pagination.BaseViewHolder;
import com.zamashops.pagination.BaseViewHolder2;

import java.util.ArrayList;
import java.util.List;

public class NewHighLightAdapter  extends RecyclerView.Adapter<BaseViewHolder2> {

    Context context;
    ArrayList<ProductModel> items;


    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;


    public NewHighLightAdapter(Context context,ArrayList<ProductModel> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_gallery_post, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.highlight_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder2 holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == items.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void addItems(ArrayList<ProductModel> models) {
        items.addAll(models);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        items.add(new ProductModel());
        notifyItemInserted(items.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = items.size() - 1;
        ProductModel item = getItem(position);
        if (item != null) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    ProductModel getItem(int position) {
        return items.get(position);
    }




    public class ViewHolder extends BaseViewHolder2 {
        ImageView img_list_product;
        TextView txt_list_product_name,txt_list_product_price,txt_list_product_des;
        CardView product_hightlight_card;


        ViewHolder(View itemView) {
            super(itemView);

            product_hightlight_card =  itemView.findViewById(R.id.product_hightlight_card);
            txt_list_product_des =  itemView.findViewById(R.id.txt_list_product_des);
            img_list_product =  itemView.findViewById(R.id.img_list_product);
            txt_list_product_name =  itemView.findViewById(R.id.txt_list_product_name);
            txt_list_product_price =  itemView.findViewById(R.id.txt_list_product_price);

        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            final ProductModel item = items.get(position);



            txt_list_product_name.setText(item.getName());
            txt_list_product_des.setText(item.getDescripition());
            txt_list_product_price.setText(item.getPrice() + " PKR");


            product_hightlight_card.setOnClickListener(new View.OnClickListener() {
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
                        .into(img_list_product);

//            Picasso.get().load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
//                    .placeholder(R.drawable.loding_blue)
//                    .error(R.drawable.image_not_found)
//                    .into(viewHolder.img_list_product);

            }else{
                img_list_product.setImageResource(R.drawable.empty_logo);
            }


        }
    }
    public class ProgressHolder extends BaseViewHolder2 {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }
}
