package com.zamashops.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.zamashops.R;
import com.zamashops.adapters.CategorySubListAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.CategoryModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SubCategoryActivity extends AppCompatActivity {


    RecyclerView list_category;

    CategorySubListAdapter categoryAdapter;
    LinearLayout lin_category_item;


    ArrayList<CategoryModel> categoryModelList = new ArrayList<>();

    Context context = SubCategoryActivity.this;


    String  cat_string = "";
    String cat_id = "";
    String cat_name = "";

    TextView txt_category_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        if(getIntent().hasExtra("cat_id")){
            cat_id = getIntent().getStringExtra("cat_id");
            cat_name = getIntent().getStringExtra("cat_name");

            getSupportActionBar().setTitle(cat_name);
        }else{
            finish();
            Toast.makeText(context,"Some thing went wrong!", Toast.LENGTH_SHORT).show();
        }


        lin_category_item = findViewById(R.id.lin_category_item);
        txt_category_name = findViewById(R.id.txt_category_name);
        txt_category_name.setText(cat_name);

        lin_category_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProduct(view);
            }
        });

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



        categoryAdapter = new CategorySubListAdapter(this,categoryModelList);
        list_category.setAdapter(categoryAdapter);


        /************** CATEGORY RECYCLEVIEW *****************/
    }

    void selectMainCategoryData() {

        categoryModelList.clear();
        HashMap<String, String> params = new HashMap<>();
        params.put("cat_id",cat_id);
        CustomVolley.getInsertPOSTData(context, params, "fetch_sub_category.php", new VolleyCallback() {
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


                        cat_string = "";
                        JSONArray main_category = result.getJSONArray("sub_category");

                        for (int i = 0; i < main_category.length(); i++) {
                            JSONObject m = main_category.getJSONObject(i);
                            cat_string += m.getString("sub_category_id")+",";
                            categoryModelList.add(new CategoryModel(
                                    m.getString("sub_category_id"),m.getString("sub_category_name"),m.getString("image")
                            ));
                        }
                        categoryAdapter.notifyDataSetChanged();

                        if (cat_string != null && cat_string.length() > 0 && cat_string.charAt(cat_string.length() - 1) == ',') {
                            cat_string = cat_string.substring(0, cat_string.length() - 1);
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void gotoProduct(View view) {
        Intent intent = new Intent(context,AllProductActivity.class);
        intent.putExtra("cat_id",cat_string);
        intent.putExtra("cat_name",cat_name);
        startActivity(intent);
    }
}
