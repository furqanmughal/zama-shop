package com.zamashops.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zamashops.R;
import com.zamashops.SQLiteDB;
import com.zamashops.adapters.AllFilterCategoryAdapter;
import com.zamashops.adapters.AllFilterCityAdapter;
import com.zamashops.adapters.AllFilteredCategoryAdapter;
import com.zamashops.adapters.AllFilteredCityAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.CategoryModel;
import com.zamashops.models.CityModel;
import com.zamashops.utility.CustomVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FilterActivity extends AppCompatActivity {

    Button btn_add_city,btn_add_category;
    Context context = FilterActivity.this;

    SQLiteDB db;

    ArrayList<CityModel> cityModels = new ArrayList<>();
    ArrayList<CityModel> localcityModels = new ArrayList<>();
    RecyclerView list_selected_cities;
    AllFilteredCityAdapter city_adapter;


    ArrayList<CategoryModel>  categoryModels = new ArrayList<>();
    ArrayList<CategoryModel> localCategoryModel = new ArrayList<>();
    RecyclerView list_selected_categories;
    AllFilteredCategoryAdapter category_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);

        btn_add_city = findViewById(R.id.btn_add_city);
        list_selected_cities = findViewById(R.id.list_selected_cities);

        btn_add_category = findViewById(R.id.btn_add_category);
        list_selected_categories = findViewById(R.id.list_selected_categories);

        db = new SQLiteDB(context);
        localcityModels = db.allCity();
        localCategoryModel = db.allCategory();

        fetchCategory();
        fetchCity();

        ////////////  CITY //////////////////////
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_selected_cities.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list_selected_cities.getContext(),
                linearLayoutManager.getOrientation());
        list_selected_cities.addItemDecoration(dividerItemDecoration);

        city_adapter = new AllFilteredCityAdapter(context,localcityModels);
        list_selected_cities.setAdapter(city_adapter);
        //////////////////// City ////////////////////////

        ////////////  Category //////////////////////
        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_selected_categories.setLayoutManager(linearLayoutManager2);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(list_selected_categories.getContext(),
                linearLayoutManager.getOrientation());
        list_selected_categories.addItemDecoration(dividerItemDecoration2);

        category_adapter = new AllFilteredCategoryAdapter(context,localCategoryModel);
        list_selected_categories.setAdapter(category_adapter);
        //////////////////// Category ////////////////////////





        btn_add_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCity();
            }
        });

        btn_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategory();
            }
        });
    }


    void selectCity() {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter_city);
        dialog.setCancelable(false);



        final RecyclerView list = dialog.findViewById(R.id.list_filter_city);
        final EditText edt_search = dialog.findViewById(R.id.edt_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        AllFilterCityAdapter adapter = new AllFilterCityAdapter(context,cityModels);
        final ImageView back = dialog.findViewById(R.id.img_filter_back);

        list.setAdapter(adapter);

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                    ArrayList<CityModel> tempList = new ArrayList<>();
                    for(CityModel temp : cityModels){
                        if(temp.getName().toLowerCase().contains(s.toString().toLowerCase())){
                            tempList.add(temp);
                        }
                    }
                    if(tempList.size() > 0) {
                        AllFilterCityAdapter adapter = new AllFilterCityAdapter(context,tempList);
                        list.setAdapter(adapter);
                    }else{

                    }

            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                localcityModels = db.allCity();
                city_adapter = new AllFilteredCityAdapter(context,localcityModels);
                list_selected_cities.setAdapter(city_adapter);
            }
        });
        dialog.show();
    }


    void selectCategory() {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter_category);
        dialog.setCancelable(false);



        final RecyclerView list = dialog.findViewById(R.id.list_filter_city);
        final EditText edt_search = dialog.findViewById(R.id.edt_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        AllFilterCategoryAdapter adapter = new AllFilterCategoryAdapter(context,categoryModels);
        final ImageView back = dialog.findViewById(R.id.img_filter_back);

        list.setAdapter(adapter);

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                ArrayList<CategoryModel> tempList = new ArrayList<>();
                for(CategoryModel temp : categoryModels){
                    if(temp.getCat_name().toLowerCase().contains(s.toString().toLowerCase())){
                        tempList.add(temp);
                    }
                }
                if(tempList.size() > 0) {
                    AllFilterCategoryAdapter adapter = new AllFilterCategoryAdapter(context,tempList);
                    list.setAdapter(adapter);
                }else{

                }

            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                localCategoryModel = db.allCategory();
                category_adapter = new AllFilteredCategoryAdapter(context,localCategoryModel);
                list_selected_categories.setAdapter(category_adapter);
            }
        });
        dialog.show();
    }


    void fetchCategory(){
        ////////////FETCH SUB CATEGORY///////////////////////
        HashMap<String, String> params = new HashMap<>();


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
                        JSONArray main_category = result.getJSONArray("sub_category");
                        for (int i = 0; i < main_category.length(); i++) {
                            JSONObject category = main_category.getJSONObject(i);
                            categoryModels.add(new CategoryModel(category.getString("sub_category_id"), category.getString("sub_category_name"),
                                    category.getString("image")));
                        }

                        category_adapter.notifyDataSetChanged();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void fetchCity() {
        CustomVolley.getInsertPOSTData(context, new HashMap<String, String>(), "fetch_city.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                    } else {
                        JSONArray city = result.getJSONArray("city");


                        for (int i = 0; i < city.length(); i++) {
                            JSONObject c = city.getJSONObject(i);
                            cityModels.add(new CityModel( c.getString("city_id"),c.getString("city_name")));
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }



}
