package com.zamashops.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.zamashops.MainActivity;
import com.zamashops.R;
import com.zamashops.adapters.GalleryAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.models.CategoryModel;
import com.zamashops.models.UploadImageModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;
import com.zamashops.utility.Permission;
import com.zamashops.utility.UtilityFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.zamashops.utility.App.logPre;


public class UpdateAdsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public ArrayList<UploadImageModel> uploadImageModels = new ArrayList<>();
    RecyclerView upload_image_lis;


    EditText edt_description, edt_title, edt_price;
    Button btn_add_post;
    RadioGroup radio_post_type_group;
    RadioButton radio_post;
    TextView txt_category, txt_city;

    ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    ArrayList<String> main_categories = new ArrayList<>();


    GalleryAdapter adapter;

    Context context = UpdateAdsActivity.this;
    ///////////////////////////// City Dialog///////////////////////////////////////////////////////////
    ArrayList<String> city_ids = new ArrayList<>();
    ArrayList<String> city_names  = new ArrayList<>();

    ArrayList<String> temp_city_ids  = new ArrayList<>();
    ArrayList<String> temp_city_names  = new ArrayList<>();

    String category_id = "";
    String city_id = "";
/////////////////////////////////////////////////////////////////////////////////////////

    String product_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ads);


        if(getIntent().hasExtra("product_id")){
            product_id = getIntent().getStringExtra("product_id");
        }else {
            Toast.makeText(context,"Some thing went wrong!",Toast.LENGTH_SHORT).show();
            finish();
        }
        fetchCity();
        initNavigation();
        initViews();
        fetchCategory();


    }



    void initViews() {

        upload_image_lis = findViewById(R.id.upload_image_lis);
        edt_description = findViewById(R.id.edt_description);
        edt_title = findViewById(R.id.edt_title);
        edt_price = findViewById(R.id.edt_price);
        radio_post_type_group = findViewById(R.id.radio_post_type_group);
        txt_category = findViewById(R.id.txt_category);
        txt_city = findViewById(R.id.txt_city);
        btn_add_post = findViewById(R.id.btn_add_post);

        btn_add_post.setText("Update Post");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        upload_image_lis.setLayoutManager(linearLayoutManager);
        upload_image_lis.setHasFixedSize(true);
        upload_image_lis.setItemViewCacheSize(20);
        upload_image_lis.setDrawingCacheEnabled(true);
        upload_image_lis.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        /************** CATEGORY RECYCLEVIEW *****************/
        adapter = new GalleryAdapter(context, uploadImageModels,product_id);
        upload_image_lis.setAdapter(adapter);
        /************** CATEGORY RECYCLEVIEW *****************/

        btn_add_post.setOnClickListener(this);
        txt_category.setOnClickListener(this);
        txt_city.setOnClickListener(this);

        fetchProductDetail();

    }

    void fetchProductDetail() {

        HashMap<String, String> params = new HashMap<>();
        params.put("product_id", product_id);


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
                            JSONObject p = user_data.getJSONObject(i);
                            JSONArray images = p.getJSONArray("images");
                            for(int count = 0; count<images.length(); count++){
                                uploadImageModels.add(new UploadImageModel(images.getJSONObject(count).getString("product_image")));

                            }

                            adapter.notifyDataSetChanged();

                            edt_title.setText(p.getString("product_name"));
                            edt_price.setText(p.getString("product_price"));
                            edt_description.setText(p.getString("product_description"));

                            for(int count=0; count<city_ids.size(); count++){
                                if(p.getString("city").equals(city_ids.get(count))){
                                    txt_city.setText(city_names.get(count));
                                    city_id = city_ids.get(count);
                                    break;
                                }
                            }

                           txt_category.setText(p.getString("category"));
                            category_id = p.getString("sub_category_id");

                            if(p.getString("product_type").equals("1")){
                                radio_post = findViewById(R.id.radio_normal);
                                radio_post.setChecked(true);
                            }else {
                                radio_post = findViewById(R.id.radio_highlight);
                                radio_post.setChecked(true);
                            }

                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    boolean validateForm() {
        boolean validate = true;
        if (edt_title.getText().toString().length() < 5) {
            edt_title.setError("Title it least 5 characters!");
            edt_title.requestFocus();
            validate = false;
        } else if (txt_category.getText().toString().isEmpty()) {
            txt_category.setError("Category May not be Empty!");
            txt_category.setHintTextColor(Color.rgb(255, 0, 0));
            validate = false;
        } else if (txt_city.getText().toString().isEmpty()) {
            txt_city.setError("City May not be Empty!");
            txt_city.setHintTextColor(Color.rgb(255, 0, 0));
            validate = false;
        } else if (edt_price.getText().toString().isEmpty()) {
            edt_price.setError("Price May not be Empty!");
            edt_price.requestFocus();
            validate = false;
        } else if (edt_description.getText().toString().length() < 10) {
            edt_description.setError("Descrption it least 10 characters!");
            edt_description.requestFocus();
            validate = false;
        }

        return validate;
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
//                    Uri selectedImage = imageReturnedIntent.getData();
                    uploadImageModels.add(new UploadImageModel("", "jpg", photo));
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {


                    if (imageReturnedIntent.getClipData() != null) {
                        int count = imageReturnedIntent.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                        if(count >9){
                            count = 9;
                        }
                        for (int i = 0; i < count; i++) {
                            Uri imageUri = imageReturnedIntent.getClipData().getItemAt(i).getUri();
                            Bitmap cmpBitmap = UtilityFunctions.compressUriQuality(context, imageUri);




                            String filePath = UtilityFunctions.getRealPathFromURI(context, imageUri);
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
//                    String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);
//                            final Bitmap bm = BitmapFactory.decodeFile(filePath);

                            uploadImageModels.add(new UploadImageModel("", file_extn, cmpBitmap));

                            Log.d("Image: ",UtilityFunctions.convertToBase64(cmpBitmap));
                        }
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    } else if (imageReturnedIntent.getData() != null) {
                        // Uri selectedImage = imageReturnedIntent.getData();
                        Uri selectedImage = imageReturnedIntent.getData();

                        Uri imgUri = Uri.fromFile(new File(imageReturnedIntent.getData().getPath()));
                        Bitmap cmpBitmap = UtilityFunctions.compressUriQuality(context, imageReturnedIntent.getData());


                        String filePath = UtilityFunctions.getRealPathFromURI(context, selectedImage);
                        String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
//                    String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);
//                        final Bitmap bm = BitmapFactory.decodeFile(filePath);

                        uploadImageModels.add(new UploadImageModel("", file_extn, cmpBitmap));
                    }



                }
                break;
        }

        adapter.notifyDataSetChanged();
    }

    public void selectImage(View view) {


        if(uploadImageModels.size() >= 0 ) {
            String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the option:");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        if (Permission.isStoragePermissionGranted(context)) {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }else{
                            Toast.makeText(context, "First Grant The Storage Permission!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (which == 1) {
                        if (Permission.isStoragePermissionGranted(context)) {
                            Intent i = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                            startActivityForResult(i, 1);

                        } else {
                            Toast.makeText(context, "First Grant The Storage Permission!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }else {
            Toast.makeText(context,"You Can Only Upload 10 Images",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_post:
                if (validateForm()) {
                    String title = edt_title.getText().toString();
                    String price = edt_price.getText().toString();
                    String des = edt_description.getText().toString();
                    String type = "1";

                    if (radio_post_type_group.getCheckedRadioButtonId() == R.id.radio_normal) {
                        type = "1";
                    } else if (radio_post_type_group.getCheckedRadioButtonId() == R.id.radio_highlight) {
                        type = "2";
                    }

                    insertData(title, price, des, type);

                }


                break;
            case R.id.txt_category:
                selectCategory();
                break;

            case R.id.txt_city:
                selectCity();
                break;

        }
    }

    void insertData(final String title, final String price, final String des, final String type) {
        HashMap<String, String> params = new HashMap<>();
        params.put("product", String.valueOf(true));
        params.put("title", title);
        params.put("price", price);
        params.put("description", des);
        params.put("category", category_id);
        params.put("user_id", logPre.getString("user_id", ""));
        params.put("city", city_id);
        params.put("product_type", type);
        params.put("no_of_images", String.valueOf(uploadImageModels.size()));
        params.put("product_id", product_id);


        for (int i = 0; i < uploadImageModels.size(); i++) {
            if(uploadImageModels.get(i).isCheck()) {
                params.put("image" + i, UtilityFunctions.convertToBase64(uploadImageModels.get(i).getImage()));
                params.put("tag" + i, "jpg");
            }else{
                params.put("image" + i,"");
                params.put("tag" + i, "jpg");
            }
        }

        CustomVolley.getInsertPOSTData(context, params, "update_post.php", new VolleyCallback() {
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

                        new MyDialog(context, "success", "Your Post Updated Successfully. Check It In Your Profile!")
                                .onPositiveClick("OK", new myOnClickListener() {
                                    @Override
                                    public void onButtonClick(MyDialog dialog) {
                                        Intent intent = new Intent(context, MyPostAcitivity.class);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
//


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void fetchCategory() {
        categoryModels.clear();
        main_categories.clear();
        CustomVolley.getInsertPOSTData(context, new HashMap<String, String>(), "fetch_main_category.php", new VolleyCallback() {
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
                            JSONObject category = main_category.getJSONObject(i);
                            main_categories.add(category.getString("main_category_name"));
                            categoryModels.add(new CategoryModel(category.getString("main_category_id"), category.getString("main_category_name"),
                                    category.getString("image")));
                        }

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
                            temp_city_ids.add(c.getString("city_id"));
                            city_ids.add(c.getString("city_id"));
                            city_names.add(c.getString("city_name"));
                            temp_city_names.add(c.getString("city_name"));

                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    void selectCategory() {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_main_category);

        final ArrayAdapter adapter2 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, main_categories);
        final ListView list = dialog.findViewById(R.id.list_main_category);
        list.setAdapter(adapter2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ArrayList<String> sub_category = new ArrayList<>();
                final ArrayList<CategoryModel> sub_categorylist = new ArrayList<>();
                final ArrayAdapter adapter3 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, sub_category);


                ////////////FETCH SUB CATEGORY///////////////////////
                HashMap<String, String> params = new HashMap<>();
                params.put("cat_id", categoryModels.get(i).getCat_id());

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
                                    sub_category.add(category.getString("sub_category_name"));
                                    sub_categorylist.add(new CategoryModel(category.getString("sub_category_id"), category.getString("sub_category_name"),
                                            category.getString("image")));
                                }

                                adapter3.notifyDataSetChanged();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

                final Dialog dialog2 = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_sub_category);
                dialog2.setCancelable(false);
                TextView txt_back = dialog2.findViewById(R.id.txt_back);
                txt_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog2.dismiss();
                    }
                });

                final ListView list = dialog2.findViewById(R.id.list_main_category);
                list.setAdapter(adapter3);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        category_id = sub_categorylist.get(i).getCat_id();
                        txt_category.setText(sub_categorylist.get(i).getCat_name());
                        dialog.dismiss();
                        dialog2.dismiss();
                    }
                });


                dialog2.show();
            }
        });


        dialog.show();
    }


    void selectCity() {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_city);

        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, temp_city_names);
        final ListView list = dialog.findViewById(R.id.list_city);
        final TextView txt_back = dialog.findViewById(R.id.txt_back);
        final EditText edt_search = dialog.findViewById(R.id.edt_search);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txt_city.setText(temp_city_names.get(i));
                city_id = temp_city_ids.get(i);
                dialog.dismiss();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String serach_text = edt_search.getText().toString().toLowerCase();
                temp_city_names.clear();
                temp_city_ids.clear();

                for (int j=0; j<city_names.size(); j++) {
                    if (city_names.get(j).toLowerCase().contains(serach_text)) {
                        temp_city_names.add(city_names.get(j));
                        temp_city_ids.add(city_ids.get(j));
                    }
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_favourite) {


        } else if (id == R.id.nav_add_post) {

        } else if (id == R.id.nav_profile) {
            if (logPre.getBoolean("login_stauts", false)) {

            } else {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
