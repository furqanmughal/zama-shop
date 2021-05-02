package com.zamashops.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class AllProductPostAdapter extends RecyclerView.Adapter<AllProductPostAdapter.ViewHolder> {

    ArrayList<ProductModel> items;
    Context context;

    public AllProductPostAdapter(Context context, ArrayList<ProductModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_all_product_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final ProductModel item = items.get(position);
        holder.txt_category.setText(item.getSub_category_id());
        holder.txt_product_name.setText(item.getName());
        holder.txt_price.setText(item.getPrice() + " RS");

        if (item.getProduct_images().length > 0) {
            Glide
                    .with(context)
                    .load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
                    .placeholder(R.drawable.infinity)
                    .error(R.drawable.empty_logo)
                    .into(holder.img_product);

        }else{
            holder.img_product.setImageResource(R.drawable.empty_logo);
        }


        if(item.isFavorite()){
            holder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_48dp));
        }else{
            holder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_gray_48dp));
        }



        holder.img_favourit.setOnClickListener(new View.OnClickListener() {
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
                                        item.setFavorite(true);
                                        notifyDataSetChanged();
                                       // holder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_48dp));

                                    } else {
                                        Toast.makeText(context, result.getString("fav_msg"), Toast.LENGTH_SHORT).show();
                                        item.setFavorite(false);
                                        notifyDataSetChanged();

                                    //    holder.img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_gray_48dp));
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

        holder.lin_profile_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", item.getProduct_id());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img_product;
        ImageView img_favourit;
        LinearLayout lin_profile_post;
        TextView txt_category, txt_product_name, txt_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_product = itemView.findViewById(R.id.img_product);
            img_favourit = itemView.findViewById(R.id.img_favourit);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);

            lin_profile_post = itemView.findViewById(R.id.lin_profile_post);


        }
    }


}
