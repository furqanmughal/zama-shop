package com.zamashops.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zamashops.R;
import com.zamashops.adapters.ProfilePostAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.ProductModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zamashops.utility.App.logPre;

public class MyPostAcitivity extends AppCompatActivity {

    RecyclerView list_post;
    LinearLayout lin_empty_list;
    FrameLayout fram_pause_post,frameLayout_active;

    ArrayList<ProductModel> productModels = new ArrayList<>();



    Context context = MyPostAcitivity.this;
    ProfilePostAdapter profilePostAdapter;


    int active = 1;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_acitivity);

        frameLayout_active = findViewById(R.id.frameLayout_active);
        fram_pause_post = findViewById(R.id.fram_pause_post);
        list_post = findViewById(R.id.list_post);
        lin_empty_list = findViewById(R.id.lin_empty_list);
        frameLayout_active.setBackgroundColor(Color.WHITE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(context, AddAdsActivity2.class);
                startActivity(intent);
            }
        });

        frameLayout_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout_active.setBackgroundColor(Color.WHITE);
                fram_pause_post.setBackgroundColor(getResources().getColor(R.color.lightsilver));
                activePost();
            }
        });

        fram_pause_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fram_pause_post.setBackgroundColor(Color.WHITE);
                frameLayout_active.setBackgroundColor(getResources().getColor(R.color.lightsilver));
                pausePost();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_post.setLayoutManager(linearLayoutManager);
//        profilePostAdapter = new ProfilePostAdapter(context, productModels);
//        list_post.setAdapter(profilePostAdapter);

        selectData();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    void selectData() {

        productModels.clear();
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", logPre.getString("user_id", ""));
        params.put("active", String.valueOf(active));


        CustomVolley.getInsertPOSTData(context, params, "fetch_user_product.php", new VolleyCallback() {
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
                            JSONObject p = user_data.getJSONObject(i);
                            JSONArray images = p.getJSONArray("images");
                            String[] extraImages = new String[images.length()];
                            for (int count = 0; count < images.length(); count++) {
                                extraImages[count] = images.getJSONObject(count).getString("product_image");
                            }
                            productModels.add(new ProductModel(
                                    p.getString("product_id"), p.getString("product_name"), p.getString("product_price"),
                                    p.getString("product_description"), extraImages,
                                    p.getString("upload_date"), p.getString("expiry_date"), p.getString("product_status"),
                                    p.getString("user_id"), p.getString("category"), p.getString("city"),
                                    p.getString("view"),p.getString("product_type")
                            ));
                        }
                        profilePostAdapter = new ProfilePostAdapter(context, productModels,active);
                        list_post.setAdapter(profilePostAdapter);



                        if (productModels.size() == 0) {
                            lin_empty_list.setVisibility(View.VISIBLE);
                            list_post.setVisibility(View.GONE);
                        } else {
                            lin_empty_list.setVisibility(View.GONE);
                            list_post.setVisibility(View.VISIBLE);
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }




    public void activePost() {
        if(active ==0 ){
            active = 1;
            selectData();
        }
    }

    public void pausePost() {
        if(active ==1 ){
            active = 0;
            selectData();
        }
    }
}
