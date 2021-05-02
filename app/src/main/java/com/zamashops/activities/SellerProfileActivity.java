package com.zamashops.activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zamashops.MainLoginActivity;
import com.zamashops.R;
import com.zamashops.adapters.ProductNormalAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.ProductModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;

public class SellerProfileActivity extends AppCompatActivity {

    Button btn_follow_unfollow;
    TextView txt_followers, txt_following, txt_user_name, txt_user_location, txt_user_email, txt_profile_name, txt_profile_latter;
    CircleImageView img_profile;
    RatingBar user_rating;

    String user_firebase_token = "";

    static int normal_post_colums = 1;

    LinearLayout lin_follower,lin_following;

    Context context = SellerProfileActivity.this;
    String post_user_id = "";
    float rating = 0;

    RecyclerView list_product_grid;
    NestedScrollView home_nested_scroll_veiw;
    ProgressBar progress_circular;

    ArrayList<ProductModel> productModels = new ArrayList<>();
    ProductNormalAdapter profilePostAdapter;


    /* Scroll */
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loadHightlightmore = true;
    boolean loadNormalmore = true;
    /* Scroll */

    int page_no = 0;

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    ActionBar actionBar;

    TextView txt_contact,txt_address,txt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile2);

        if (getIntent().hasExtra("user_id")) {
            post_user_id = getIntent().getStringExtra("user_id");
        }

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra("notification_id")) {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(getIntent().getIntExtra("notification_id",0));
        }





        txt_email = findViewById(R.id.txt_email);
        txt_contact = findViewById(R.id.txt_contact);
        txt_address = findViewById(R.id.txt_address);
        btn_follow_unfollow = findViewById(R.id.btn_follow_unfollow);
        txt_followers = findViewById(R.id.txt_followers);
        txt_following = findViewById(R.id.txt_following);
        txt_user_name = findViewById(R.id.txt_user_name);
        img_profile = findViewById(R.id.img_profile);
        user_rating = findViewById(R.id.user_rating);
        txt_profile_latter = findViewById(R.id.txt_profile_latter);
        lin_following = findViewById(R.id.lin_following);
        lin_follower = findViewById(R.id.lin_follower);

        selectData();



        list_product_grid = findViewById(R.id.list_product_grid);
        home_nested_scroll_veiw = findViewById(R.id.home_nested_scroll_veiw);
        progress_circular = findViewById(R.id.progress_circular);

        initColums();


        /************** NORMAL PRODUCT RECYCLEVIEW *****************/
        final GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context, normal_post_colums);
        list_product_grid.setLayoutManager(gridLayoutManager2);
        list_product_grid.setHasFixedSize(true);
        list_product_grid.setItemViewCacheSize(20);
        list_product_grid.setDrawingCacheEnabled(true);
        list_product_grid.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        profilePostAdapter = new ProductNormalAdapter(context, productModels);
        list_product_grid.setAdapter(profilePostAdapter);

        selectNormalProductData(post_user_id);

        lin_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFollower(view);
            }
        });

        lin_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFollowing(view);
            }
        });


//        home_nested_scroll_veiw.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
//
//                    if (loadNormalmore) {
//                        selectNormalProductData();
//                    }
//
//                }
//            }
//        });



    }


    void selectNormalProductData(String id) {

        progress_circular.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", id);
        CustomVolley.getInsertPOSTData2(context, params, "fetch_user_post.php", new VolleyCallback() {
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

                        if (user_data.length() < 20) {
                            loadNormalmore = false;
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


                            productModels.add(model);


                        }
                        profilePostAdapter.notifyDataSetChanged();

                    }

                    progress_circular.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void selectData() {


        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", post_user_id);
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("follower_user_id", logPre.getString("user_id", ""));
        }


        CustomVolley.getInsertPOSTData(context, params, "fetch_user.php", new VolleyCallback() {
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


                        JSONArray user_data = result.getJSONArray("user");

                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject user = user_data.getJSONObject(i);
                            txt_user_name.setText(user.getString("user_name"));
                            txt_followers.setText(user.getString("followers"));
                            txt_following.setText(user.getString("following"));

                            if (actionBar != null) {
                                actionBar.setTitle(user.getString("user_name")+" Shop");
                            }


                            if(user.getString("email_privacy").equals("1")){
                                txt_email.setVisibility(View.VISIBLE);
                                if(user.getString("user_email").contains("@")) {
                                    txt_email.setText(user.getString("user_email"));
                                }else{
                                    txt_email.setVisibility(View.GONE);
                                }

                            }


                            if(user.getString("contact_privacy").equals("1")){
                                txt_contact.setVisibility(View.VISIBLE);
                                txt_contact.setText(user.getString("user_contact"));
                            }

                            txt_address.setText(user.getString("user_address"));
                            user_rating.setRating(Float.parseFloat(user.getString("reviews")));

                            user_firebase_token = user.getString("firebase_token");


                            if (user.getBoolean("follow")) {
                                btn_follow_unfollow.setText("UnFollow");
                            } else {
                                btn_follow_unfollow.setText("Follow");
                            }



                            //////////////Image////////////////////////////////

                            String image_name = "";
                            if (!user.getString("user_image").equals("")) {
                                if (user.getString("user_image").contains("/")) {
                                    image_name = user.getString("user_image");
                                } else {
                                    image_name = context.getResources().getString(R.string.url_image) + user.getString("user_image");
                                }

                                Picasso.get().load(image_name)
                                        .placeholder(R.drawable.image_not_found)
                                        .error(R.drawable.image_not_found)
                                        .into(img_profile);
                            }


                            //////////////Image////////////////////////////////


                            final String id = user.getString("user_id");


                            btn_follow_unfollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (logPre.getBoolean("login_stauts", false)) {
                                        follow(logPre.getString("user_id", ""));
                                    } else {
                                        Intent intent = new Intent(context, MainLoginActivity.class);
                                        intent.putExtra("status", true);
                                        startActivity(intent);
                                    }
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


    void follow(String id) {


        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", id);
        params.put("token", user_firebase_token);
        params.put("follow_user_id", post_user_id);

        CustomVolley.getInsertPOSTData2(context, params, "add_follow.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_SHORT).show();
                    } else {

                        boolean fav_insert = result.getBoolean("follow_insert");

                        if (fav_insert) {
                            Toast.makeText(context, result.getString("follow_msg"), Toast.LENGTH_SHORT).show();
                            btn_follow_unfollow.setText("unfollow");
                        } else {
                            Toast.makeText(context, result.getString("follow_msg"), Toast.LENGTH_SHORT).show();
                            btn_follow_unfollow.setText("follow");

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    void rateUser(String id, float rating) {


        HashMap<String, String> params = new HashMap<>();
        params.put("rating", String.valueOf(rating));
        params.put("user_id", post_user_id);
        params.put("rate_user_id", id);

        CustomVolley.getInsertPOSTData2(context, params, "add_rating.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                        Toast.makeText(context, "Some thing went wrong!", Toast.LENGTH_SHORT).show();
                    } else {

                        boolean fav_insert = result.getBoolean("rate_insert");

                        if (fav_insert) {
                            Toast.makeText(context, result.getString("rate_msg"), Toast.LENGTH_SHORT).show();
                            selectData();
                        } else {
                            Toast.makeText(context, result.getString("rate_msg"), Toast.LENGTH_SHORT).show();
                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void userPost(View view) {
        Intent intent = new Intent(context, UserPostActivity.class);
        intent.putExtra("user_id", post_user_id);
        startActivity(intent);
    }

    public void reviewUser(View view) {

        final Dialog rankDialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        rankDialog.setContentView(R.layout.dialog_rank);
        rankDialog.setCancelable(true);
        RatingBar ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = v;
            }
        });

        Button updateButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logPre.getBoolean("login_stauts", false)) {
                    rateUser(logPre.getString("user_id", ""), rating);
                } else {
                    Intent intent = new Intent(context, MainLoginActivity.class);
                    intent.putExtra("status", true);
                    startActivity(intent);
                }
                rankDialog.dismiss();
            }
        });
        rankDialog.show();
    }

    void initColums() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        boolean orientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;


        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                if (orientation) {
                    normal_post_colums = 6;
                } else {
                    normal_post_colums = 4;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                if (orientation) {
                    normal_post_colums = 3;
                } else {
                    normal_post_colums = 2;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                if (orientation) {
                    normal_post_colums = 2;
                } else {
                    normal_post_colums = 2;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                if (orientation) {
                    normal_post_colums = 5;
                } else {
                    normal_post_colums = 7;
                }
                break;
            default:

        }

    }

    public void gotoFollower(View view) {

        Intent intent = new Intent(context,SellerProfileUserActivity.class);
        intent.putExtra("post_user_id",post_user_id);
        intent.putExtra("position",0);
        startActivity(intent);


    }

    public void gotoFollowing(View view) {
        Intent intent = new Intent(context,SellerProfileUserActivity.class);
        intent.putExtra("post_user_id",post_user_id);
        intent.putExtra("position",1);
        startActivity(intent);
    }
}
