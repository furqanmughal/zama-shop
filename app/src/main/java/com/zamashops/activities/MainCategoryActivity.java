package com.zamashops.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.zamashops.R;
import com.zamashops.adapters.CategoryMainListAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.CategoryModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainCategoryActivity extends AppCompatActivity {


    RecyclerView list_category;

    CategoryMainListAdapter categoryAdapter;


    ArrayList<CategoryModel> categoryModelList = new ArrayList<>();

    Context context = MainCategoryActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);



        initRecycleView();

        selectMainCategoryData();
    }


    void initRecycleView(){
        list_category = findViewById(R.id.list_all_main_categories);

        /************** CATEGORY RECYCLEVIEW *****************/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_category.setLayoutManager(linearLayoutManager);
        list_category.setHasFixedSize(true);
        list_category.setItemViewCacheSize(20);
        list_category.setDrawingCacheEnabled(true);
        list_category.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list_category.getContext(),
//                linearLayoutManager.getOrientation());
//        list_category.addItemDecoration(dividerItemDecoration);



        categoryAdapter = new CategoryMainListAdapter(this,categoryModelList);
        list_category.setAdapter(categoryAdapter);


        /************** CATEGORY RECYCLEVIEW *****************/
    }

    void selectMainCategoryData() {

        categoryModelList.clear();
        HashMap<String, String> params = new HashMap<>();


        CustomVolley.getInsertPOSTData(context, params, "fetch_main_category.php", new VolleyCallback() {
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


                        JSONArray main_category = result.getJSONArray("main_category");

                        for (int i = 0; i < main_category.length(); i++) {
                            JSONObject m = main_category.getJSONObject(i);


                            categoryModelList.add(new CategoryModel(
                                    m.getString("main_category_id"),m.getString("main_category_name"),m.getString("image")
                            ));
                        }
                        categoryAdapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void gotoProduct(View view) {
        Intent intent = new Intent(context,AllProductActivity.class);
        startActivity(intent);
    }
}
