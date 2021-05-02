package com.zamashops.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zamashops.R;
import com.zamashops.adapters.AllProductPostAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.ProductModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zamashops.utility.App.logPre;

public class FavouriteActivity extends AppCompatActivity {


    RecyclerView list_favourite;
    LinearLayout lin_empty_list;

    ArrayList<ProductModel> productModels = new ArrayList<>();

    Context context = FavouriteActivity.this;
    AllProductPostAdapter favouritePostAdapter;

    int page_no = 0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    ProgressBar progress_circular;
    boolean loadmore = true;

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
        setContentView(R.layout.activity_favourite);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        list_favourite = findViewById(R.id.list_favourite);
        lin_empty_list = findViewById(R.id.lin_empty_list);
        progress_circular = findViewById(R.id.progress_circular);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_favourite.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list_favourite.getContext(),
                linearLayoutManager.getOrientation());
        list_favourite.addItemDecoration(dividerItemDecoration);

        list_favourite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();


                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (loadmore) {
                            selectData();
                        }
                    }

                }
            }
        });


        favouritePostAdapter = new AllProductPostAdapter(context, productModels);
        list_favourite.setAdapter(favouritePostAdapter);

        selectData();
    }



    void selectData() {

        progress_circular.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", "20");
        params.put("limit_count", String.valueOf(page_no++));
        if(logPre.getBoolean("login_stauts",false)) {
            params.put("user_id", logPre.getString("user_id", ""));
        }


        CustomVolley.getInsertPOSTData(context, params, "fetch_favourite_product.php", new VolleyCallback() {
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
                            loadmore = false;
                        }

                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject p = user_data.getJSONObject(i);
                            JSONArray images = p.getJSONArray("images");
                            String[] extraImages = new String[images.length()];
                            for (int count = 0; count < images.length(); count++) {
                                extraImages[count] = images.getJSONObject(count).getString("product_image");
                            }

                            ProductModel  model = new ProductModel(
                                    p.getString("product_id"), p.getString("product_name"), p.getString("product_price"),
                                    p.getString("product_description"), extraImages,
                                    p.getString("upload_date"), p.getString("expiry_date"), p.getString("product_status"),
                                    p.getString("user_id"), p.getString("category"), p.getString("city"),
                                    p.getString("view")
                            );
                            model.setFavorite(p.getBoolean("favourite"));
                            productModels.add(model);
                        }
                        favouritePostAdapter.notifyDataSetChanged();



                        if (productModels.size() == 0) {
                            lin_empty_list.setVisibility(View.VISIBLE);
                            list_favourite.setVisibility(View.GONE);
                        } else {
                            lin_empty_list.setVisibility(View.GONE);
                            list_favourite.setVisibility(View.VISIBLE);
                        }



                    }
                    progress_circular.setVisibility(View.GONE);


                } catch (JSONException e) {
                    progress_circular.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        });
    }



}
