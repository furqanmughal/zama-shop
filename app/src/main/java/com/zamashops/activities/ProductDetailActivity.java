package com.zamashops.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;
import com.zamashops.MainLoginActivity;
import com.zamashops.R;
import com.zamashops.adapters.ImageSlideAdapter;
import com.zamashops.adapters.ProductAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.models.ProductModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;

public class ProductDetailActivity extends AppCompatActivity {

    ViewPager image_slider;
    TextView txt_name, txt_price, txt_date, txt_view, txt_description, txt_user_name, txt_id, txt_city;
    TextView txt_address;
    RatingBar ratingBar;
    Context context = ProductDetailActivity.this;
    CircleImageView profile_image;
    LinearLayout lin_user;

    String slider_images[];
    String product_id = "";
    boolean favrouite = false;
    MenuItem item;
    Button btn_chat;

    TextView btn_remove;

    String token = "";

    ////////////////////////Related Post///////////////////////////////////////////////////
    RecyclerView list_product_horizental;
    ArrayList<ProductModel> productModelList = new ArrayList<>();
    ProductAdapter productAdapter;
    /* Scroll */
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loadHightlightmore = true;
    boolean loadNormalmore = true;
    int page_no = 0;
    String sub_category_id = "";
    String filter_city_id = "";
    /* Scroll */
    ////////////////////////Related Post///////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Token///

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();


                        //  Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });

        //Token//


        list_product_horizental = findViewById(R.id.list_product_horizental2);


        txt_address = findViewById(R.id.txt_address);
        txt_city = findViewById(R.id.txt_city);
        btn_remove = findViewById(R.id.btn_remove);
        image_slider = findViewById(R.id.img_slider);
        txt_name = findViewById(R.id.txt_name);
        txt_price = findViewById(R.id.txt_price);
        txt_date = findViewById(R.id.txt_date);
        txt_view = findViewById(R.id.txt_view);
        txt_description = findViewById(R.id.txt_description);
        profile_image = findViewById(R.id.profile_image);
        txt_user_name = findViewById(R.id.txt_user_name);
        ratingBar = findViewById(R.id.ratingBar);
        lin_user = findViewById(R.id.lin_user);
        btn_chat = findViewById(R.id.btn_chat);
        txt_id = findViewById(R.id.txt_id);


        // ATTENTION: This was auto-generated to handle app links.
        handlerUri();

        if (getIntent().hasExtra("product_id")) {
            product_id = getIntent().getStringExtra("product_id");

        } else {
            if (product_id.equals("")) {
                Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        selectData();


        /************** GALLERY PRODUCT RECYCLEVIEW *****************/

        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context);
        linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        list_product_horizental.setLayoutManager(linearLayoutManager2);
        list_product_horizental.setHasFixedSize(true);
        list_product_horizental.setItemViewCacheSize(20);
        list_product_horizental.setDrawingCacheEnabled(true);
        list_product_horizental.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        productAdapter = new ProductAdapter(context, productModelList);
        list_product_horizental.setAdapter(productAdapter);

        list_product_horizental.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dx > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager2.getChildCount();
                    totalItemCount = linearLayoutManager2.getItemCount();
                    pastVisiblesItems = linearLayoutManager2.findFirstVisibleItemPosition();


                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (loadHightlightmore) {
                            selectRelatedPost(10);
                        }

                    }

                }
            }
        });


        /************** GALLERY PRODUCT RECYCLEVIEW *****************/


        //  selectRelatedPost(10);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handlerUri();
    }

    private void handlerUri() {
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null) {
            product_id = appLinkData.getLastPathSegment();
        }
    }


    void addview() {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);
        if (!token.isEmpty()) {
            params.put("user_token", token);
        }

        CustomVolley.getInsertPOSTDataNotShowError(context, params, "add_view.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
            }
        });
    }


    void selectData() {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
        }

        if (!token.isEmpty()) {
            params.put("token", token);
        }


        CustomVolley.getInsertPOSTData(context, params, "fetch_detail_product.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage(result.getString("msg"))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  finish();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    } else {


                        JSONArray user_data = result.getJSONArray("products");

                        for (int i = 0; i < user_data.length(); i++) {
                            final JSONObject p = user_data.getJSONObject(i);
                            JSONArray images = p.getJSONArray("images");
                            slider_images = new String[images.length()];
                            for (int count = 0; count < images.length(); count++) {
                                slider_images[count] = images.getJSONObject(count).getString("product_image");
                            }

                            sub_category_id = p.getString("sub_category_id");
                            filter_city_id = p.getString("city");

                            txt_id.setText("ID: " + p.getString("product_id"));
                            txt_name.setText(p.getString("product_name"));
                            txt_price.setText(p.getString("product_price") + " PKR");
                            txt_description.setText(p.getString("product_description"));
                            txt_view.setText(p.getString("view"));
                            txt_date.setText(p.getString("upload_date"));
                            txt_city.setText("City: " + p.getString("city_name"));

                            txt_address.setText(p.getString("address"));


                            //////////////////////////////APP BAR///////////////////////////////////////////////
                            final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
                            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                                boolean isShow = true;
                                int scrollRange = -1;

                                @Override
                                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                    if (scrollRange == -1) {
                                        scrollRange = appBarLayout.getTotalScrollRange();
                                    }
                                    if (scrollRange + verticalOffset == 0) {
                                        try {
                                            collapsingToolbarLayout.setTitle(p.getString("product_name"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        isShow = true;
                                    } else if (isShow) {
                                        collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                                        isShow = false;
                                    }
                                }
                            });
                            //////////////////////////////APP BAR///////////////////////////////////////////////

                            favrouite = p.getBoolean("favourite");
                            if (favrouite) {
                                if (item != null) {
                                    item.setIcon(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
                                }
                            }

                            final JSONObject user = p.getJSONObject("user");
                            txt_user_name.setText(user.getString("user_name"));


                            String image_name = "";
                            if(!user.getString("user_image").isEmpty()) {
                                if(user.getString("user_image").contains("/")){
                                    image_name =user.getString("user_image");
                                }else{
                                    image_name = context.getResources().getString(R.string.url_image) + user.getString("user_image");
                                }
                                Picasso.get().load(image_name)
                                        .placeholder(R.drawable.loding_blue)
                                        .error(R.drawable.image_not_found)
                                        .into(profile_image);
                            }




                            ratingBar.setRating(Float.parseFloat(user.getString("reviews")));
                            final String user_id = p.getString("user_id");

                            lin_user.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (logPre.getString("user_id", "").equals(user_id)) {

                                        Intent intent = new Intent(context, ProfileActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(context, SellerProfileActivity.class);
                                        intent.putExtra("user_id", user_id);
                                        startActivity(intent);
                                    }
                                }
                            });

                            if (p.getString("delete_status").equals("0") || p.getString("product_status").equals("0")) {
                                btn_remove.setVisibility(View.GONE);
                                btn_chat.setVisibility(View.GONE);
                            } else {

                                if (logPre.getString("user_id", "").equals(user_id)) {
                                    btn_chat.setText("Sold Out");
                                    btn_chat.setBackgroundColor(Color.RED);
                                    btn_remove.setVisibility(View.VISIBLE);
                                }

                                btn_remove.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MyDialog dialog = new MyDialog(context, "Danger!", "Are you sure to Remove this post!");
                                        dialog.onPositiveClick("Yes", new myOnClickListener() {
                                            @Override
                                            public void onButtonClick(MyDialog dialog) {
                                                deletePost(product_id, "removed");
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.showCancel();
                                        dialog.setDialog(MyDialog.DANGER);
                                    }
                                });

                                btn_chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if (logPre.getString("user_id", "").equals(user_id)) {

                                            MyDialog dialog = new MyDialog(context, "Sold Out", "Sold Out On ZamaShops?");
                                            dialog.onPositiveClick("Yes", new myOnClickListener() {
                                                @Override
                                                public void onButtonClick(MyDialog dialog) {
                                                    deletePost(product_id, "Yes");
                                                    dialog.dismiss();
                                                }
                                            });
                                            dialog.showCancel();
                                            dialog.setDialog(MyDialog.DANGER);


                                        } else {

                                            if (logPre.getBoolean("login_stauts", false)) {
                                                Intent intent = new Intent(context, ChatRoomActivity.class);
                                                intent.putExtra("user_id_1", user_id);
                                                intent.putExtra("user_id_2", logPre.getString("user_id", ""));
                                                intent.putExtra("product_id", product_id);
                                                intent.putExtra("activity",true);
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(context, MainLoginActivity.class);
                                                startActivity(intent);
                                            }


                                        }
                                    }
                                });
                            }


                        }

                        ImageSlideAdapter adapter = new ImageSlideAdapter(context, slider_images);

                        image_slider.setAdapter(adapter);


                        ////////////////////////////////////////////////////////////////////////////////////////
                        if (!token.isEmpty()) {
                            addview();
                        }
                        ////////////////////////////////////////////////////////////////////////////////////////

                        selectRelatedPost(10);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    void selectRelatedPost(final int limit) {

        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));
        params.put("limit_count", String.valueOf(page_no++));


        if (!sub_category_id.isEmpty()) {
            params.put("filterCategories", sub_category_id);
        }


        CustomVolley.getInsertPOSTData2(context, params, "fetch_normal_product.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {
                    boolean error = result.getBoolean("error");
                    if (error) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage(result.getString("msg"))
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //  finish();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                    } else {

                        JSONArray user_data = result.getJSONArray("products");


                        if (user_data.length() == 0) {
                            loadHightlightmore = false;
                        }


                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject p = user_data.getJSONObject(i);
                            JSONArray images = p.getJSONArray("images");
                            String[] extraImages = new String[images.length()];
                            for (int count = 0; count < images.length(); count++) {
                                extraImages[count] = images.getJSONObject(count).getString("product_image");
                            }

                            ProductModel model = new ProductModel(
                                    p.getString("product_id"), p.getString("product_name"), p.getString("product_price"),
                                    p.getString("product_description"), extraImages,
                                    p.getString("upload_date"), p.getString("expiry_date"), p.getString("product_status"),
                                    p.getString("user_id"), p.getString("category"), p.getString("city"), p.getString("view")
                            );


                            model.setFavorite(p.getBoolean("favourite"));


                            productModelList.add(model);


                        }
                        productAdapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        item = menu.findItem(R.id.item_fav);
        if (favrouite) {
            item.setIcon(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_fav:

                if (logPre.getBoolean("login_stauts", false)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", logPre.getString("user_id", ""));
                    params.put("product_id", product_id);

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
                                        item.setIcon(context.getResources().getDrawable(R.drawable.ic_star_yellow_24dp));

                                    } else {
                                        Toast.makeText(context, result.getString("fav_msg"), Toast.LENGTH_SHORT).show();
                                        item.setIcon(context.getResources().getDrawable(R.drawable.ic_star_border_white_24dp));
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

                break;

            case R.id.item_share:


                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shareurl) + product_id);
                startActivity(Intent.createChooser(shareIntent, "Share link using"));


                break;

            case android.R.id.home:
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    void deletePost(String product_id, final String status) {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);
        params.put("status", status);


        CustomVolley.getInsertPOSTData(context, params, "delete_post.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        MyDialog dialog = new MyDialog(context, "success!", result.getString("msg"));
                        dialog.onPositiveClick("Ok", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                        dialog.setDialog(MyDialog.DANGER);
                    } else {


                        if (status.equals("removed")) {

                            new MyDialog(context, "Congragtulations!", "Your Post Removed Successfull!").onPositiveClick("Ok", new myOnClickListener() {
                                @Override
                                public void onButtonClick(MyDialog dialog) {
                                    finish();
                                    dialog.dismiss();
                                }
                            });

                        } else {

                            new MyDialog(context, "Congragtulations!", "Your Post Deleted Successfull!").onPositiveClick("Ok", new myOnClickListener() {
                                @Override
                                public void onButtonClick(MyDialog dialog) {
                                    finish();
                                    dialog.dismiss();
                                }
                            });
                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //selectData();
    }
}
