package com.zamashops.adapters;


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
import com.zamashops.activities.LoginActivity;
import com.zamashops.activities.ProductDetailActivity;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.ProductModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zamashops.utility.App.logPre;

public class ProductNormalAdapter extends RecyclerView.Adapter<ProductNormalAdapter.CategoryListViewHolder> {

    Context context;
    ArrayList<ProductModel> items;

    public ProductNormalAdapter(Context context, ArrayList<ProductModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_card_normal_post, viewGroup, false);

        return new ProductNormalAdapter.CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryListViewHolder viewHolder, int i) {
        final ProductModel item = items.get(i);

        viewHolder.txt_list_product_name.setText(item.getName());
        viewHolder.txt_list_product_des.setText(item.getDescripition());
        viewHolder.txt_list_product_price.setText(item.getPrice() + " PKR");

        if(item.isFavorite()){
            viewHolder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }else{
            viewHolder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_gray_24dp));
        }

        if (item.getProduct_images().length > 0) {

            Glide
                    .with(context)
                    .load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
                    .placeholder(R.drawable.infinity)
                    .into(viewHolder.img_list_product);
//
//            Picasso.get().load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
//                    .placeholder(R.drawable.infinity)
//                    .into(viewHolder.img_list_product);

        }else{
            viewHolder.img_list_product.setImageResource(R.drawable.empty_logo);
        }

        viewHolder.product_normal_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", item.getProduct_id());
                context.startActivity(intent);
            }
        });

        viewHolder.img_favourit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (logPre.getBoolean("login_stauts", false)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", logPre.getString("user_id",""));
                    params.put("product_id", item.getProduct_id());

                    CustomVolley.getInsertPOSTData2(context, params, "add_favourite.php", new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {


                            try {

                                boolean error = result.getBoolean("error");
                                if (error) {
                                    Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_SHORT).show();
                                } else {

                                    boolean fav_insert = result.getBoolean("fav_insert");

                                    if (fav_insert) {
                                        Toast.makeText(context, result.getString("fav_msg"), Toast.LENGTH_SHORT).show();
                                        viewHolder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
                                        item.setFavorite(true);

                                    } else {
                                        Toast.makeText(context, result.getString("fav_msg"), Toast.LENGTH_SHORT).show();
                                        viewHolder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_gray_24dp));
                                        item.setFavorite(false);
                                    }


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
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

        ImageView img_list_product;
        ImageView img_favourit;
        TextView txt_list_product_name, txt_list_product_price,txt_list_product_des;
        CardView product_normal_card;

        public CategoryListViewHolder(@NonNull View itemView) {
            super(itemView);

            img_list_product = itemView.findViewById(R.id.img_list_product);
            img_favourit = itemView.findViewById(R.id.img_favourit);
            txt_list_product_des = itemView.findViewById(R.id.txt_list_product_des);
            txt_list_product_name = itemView.findViewById(R.id.txt_list_product_name);
            txt_list_product_price = itemView.findViewById(R.id.txt_list_product_price);
            product_normal_card = itemView.findViewById(R.id.product_normal_card);


        }
    }


    void favourite(String user_id, String product_id) {


    }


}