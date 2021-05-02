package com.zamashops.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.zamashops.MainActivity;
import com.zamashops.R;
import com.zamashops.activities.LoginActivity;
import com.zamashops.activities.ProductDetailActivity;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.ProductModel;
import com.zamashops.pagination.BaseViewHolder;
import com.zamashops.pagination.HighLighPagination;
import com.zamashops.pagination.PaginationListener;
import com.zamashops.utility.CustomVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.zamashops.MainActivity.category_colums;
import static com.zamashops.MainActivity.productModelList;
import static com.zamashops.utility.App.logPre;

public class NormalProductAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TEXT_HIGH_LIGHT = 2;
    private static final int VIEW_TEXT_NORMAL = 3;
    private static final int VIEW_CATEGORY = 4;
    private static final int VIEW_HIGHLIGHT = 5;
    private boolean isLoaderVisible = false;

    Context context;
    ArrayList<ProductModel> items;

    public NormalProductAdapter(Context context, ArrayList<ProductModel> items) {
        this.context = context;
        this.items = items;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case VIEW_CATEGORY:
                return new ViewHolderCategory(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.post_recyle_view, parent, false));
            case VIEW_HIGHLIGHT:
                return new ViewHolderHightLight(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.post_recyle_view, parent, false));
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_normal_post, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            case VIEW_TEXT_HIGH_LIGHT:
                return new ViewHolderText(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.post_text, parent, false));
            case VIEW_TEXT_NORMAL:
                return new ViewHolderNormalText(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.post_text, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (position == 0 || position == 1 || position == 2 || position == 3 || (position == items.size() - 1 && isLoaderVisible)) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_CATEGORY;
        } else if (position == 1) {
            return VIEW_TEXT_HIGH_LIGHT;
        } else if (position == 2) {
            return VIEW_HIGHLIGHT;
        }else if(position == 3){
            return VIEW_TEXT_NORMAL;
        }
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

    public void addItems(List<ProductModel> postItems) {
        items.addAll(postItems);
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


    public class ViewHolder extends BaseViewHolder {
        ImageView img_list_product;
        ImageView img_favourit;
        TextView txt_list_product_name, txt_list_product_price, txt_list_product_des;
        CardView product_normal_card;

        ViewHolder(View itemView) {
            super(itemView);


            img_list_product = itemView.findViewById(R.id.img_list_product);
            img_favourit = itemView.findViewById(R.id.img_favourit);
            txt_list_product_des = itemView.findViewById(R.id.txt_list_product_des);
            txt_list_product_name = itemView.findViewById(R.id.txt_list_product_name);
            txt_list_product_price = itemView.findViewById(R.id.txt_list_product_price);
            product_normal_card = itemView.findViewById(R.id.product_normal_card);


        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            final ProductModel item = items.get(position);

            txt_list_product_name.setText(item.getName());
            txt_list_product_des.setText(item.getDescripition());
            txt_list_product_price.setText(item.getPrice() + " PKR");


            if (item.isFavorite()) {
                img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
            } else {
                img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_gray_24dp));
            }

            if (item.getProduct_images().length > 0) {

                Glide
                        .with(context)
                        .load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
                        .centerCrop()
                        .into(img_list_product);
//
//            Picasso.get().load(context.getResources().getString(R.string.url_image) + item.getProduct_images()[0])
//                    .into(img_list_product);

            } else {
                img_list_product.setImageResource(R.drawable.empty_logo);
            }


            product_normal_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("product_id", item.getProduct_id());
                    context.startActivity(intent);
                }
            });

            img_favourit.setOnClickListener(new View.OnClickListener() {
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
                                            img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
                                            item.setFavorite(true);

                                        } else {
                                            Toast.makeText(context, result.getString("fav_msg"), Toast.LENGTH_SHORT).show();
                                            img_favourit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_border_gray_24dp));
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
    }

    public class ViewHolderText extends BaseViewHolder {

        TextView txt_highlighted;

        ViewHolderText(View itemView) {
            super(itemView);


            txt_highlighted = itemView.findViewById(R.id.txt_highlighted);


        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
//            ProductModel item = items.get(position);

        }
    }

    public class ViewHolderNormalText extends BaseViewHolder {

        TextView txt_highlighted;

        ViewHolderNormalText(View itemView) {
            super(itemView);


            txt_highlighted = itemView.findViewById(R.id.txt_highlighted);
            txt_highlighted.setText(context.getResources().getString(R.string.normal));


        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
//            ProductModel item = items.get(position);

        }
    }

    public class ViewHolderCategory extends BaseViewHolder {

        RecyclerView list_category;
        CategoryAdapter categoryAdapter;
//        ArrayList<CategoryModel> categoryModelList = new ArrayList<>();

        ViewHolderCategory(View itemView) {
            super(itemView);
            list_category = itemView.findViewById(R.id.recycle_view_genral);


        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);

            /************** CATEGORY RECYCLEVIEW *****************/


            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, category_colums + 1);
            list_category.setLayoutManager(gridLayoutManager);
            list_category.setHasFixedSize(true);
            list_category.setItemViewCacheSize(20);
            list_category.setDrawingCacheEnabled(true);
            list_category.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            list_category.setBackgroundColor(Color.WHITE);


//            categoryAdapter = new CategoryAdapter(context, MainActivity.categoryModelList);
            list_category.setAdapter(MainActivity.categoryAdapter);
            /************** CATEGORY RECYCLEVIEW *****************/


        }

    }


    public class ViewHolderHightLight extends BaseViewHolder {

        RecyclerView list_product_horizental;
        ProductAdapter productAdapter;

        ViewHolderHightLight(View itemView) {
            super(itemView);
            list_product_horizental = itemView.findViewById(R.id.recycle_view_genral);


        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);


            /************** GALLERY PRODUCT RECYCLEVIEW *****************/

            final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
            linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
            list_product_horizental.setLayoutManager(linearLayoutManager2);
            list_product_horizental.setHasFixedSize(true);
            list_product_horizental.setItemViewCacheSize(20);
            list_product_horizental.setDrawingCacheEnabled(true);
            list_product_horizental.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

            list_product_horizental.setAdapter(MainActivity.productAdapter);

            list_product_horizental.addOnScrollListener(new HighLighPagination(linearLayoutManager2) {
                @Override
                protected void loadMoreItems() {
                    MainActivity.hisLoading = true;
                    MainActivity.hcurrentPage++;
                    if (context instanceof MainActivity) {
                        ((MainActivity)context).selectHighlightProductData(10);
                    }

                }
                @Override
                public boolean isLastPage() {
                    return MainActivity.hisLastPage;
                }
                @Override
                public boolean isLoading() {
                    return MainActivity.hisLoading;
                }
            });

            /************** GALLERY PRODUCT RECYCLEVIEW *****************/


        }

    }


    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void clear() {
        }
    }

}
