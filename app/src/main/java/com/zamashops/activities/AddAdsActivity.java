package com.zamashops.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.zamashops.MainActivity;
import com.zamashops.MainLoginActivity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;


public class AddAdsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

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

    Context context = AddAdsActivity.this;

    String[] city_ids;
    String[] city_names;

    String category_id = "";
    String city_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ads);

        initNavigation();
        initViews();
        fetchCategory();
        fetchCity();

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        upload_image_lis.setLayoutManager(linearLayoutManager);
        upload_image_lis.setHasFixedSize(true);
        upload_image_lis.setItemViewCacheSize(20);
        upload_image_lis.setDrawingCacheEnabled(true);
        upload_image_lis.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        /************** CATEGORY RECYCLEVIEW *****************/
        adapter = new GalleryAdapter(context, uploadImageModels, "");
        upload_image_lis.setAdapter(adapter);
        /************** CATEGORY RECYCLEVIEW *****************/

        btn_add_post.setOnClickListener(this);
        txt_category.setOnClickListener(this);
        txt_city.setOnClickListener(this);

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
                    Bitmap resized = Bitmap.createScaledBitmap(photo, 200, 250, true);
                    uploadImageModels.add(new UploadImageModel("", "jpg", resized));
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
                            //Bitmap cmpBitmap = UtilityFunctions.compressUriQuality(context, imageUri);
                            String filePath = UtilityFunctions.getRealPathFromURI(context, imageUri);
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                                uploadImageModels.add(new UploadImageModel("", file_extn, bitmap));
                            } catch (IOException e) {
                                Toast.makeText(context,"Image not selected try again!",Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }


                        }

                        //do something with the image (save it to some directory or whatever you need to do with it here)
                    } else if (imageReturnedIntent.getData() != null) {
                        Uri selectedImage = imageReturnedIntent.getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            String filePath = UtilityFunctions.getRealPathFromURI(context, selectedImage);
                            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                            uploadImageModels.add(new UploadImageModel("", file_extn, bitmap));
                        } catch (IOException e) {
                           Toast.makeText(context,"Image not selected try again!",Toast.LENGTH_SHORT).show();

                        }

//                        Uri imgUri = Uri.fromFile(new File(imageReturnedIntent.getData().getPath()));
//                        Bitmap cmpBitmap = UtilityFunctions.compressUriQuality(context, imageReturnedIntent.getData());



//                    String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);
//                        final Bitmap bm = BitmapFactory.decodeFile(filePath);


//                        Bitmap resized = Bitmap.createScaledBitmap(cmpBitmap, 200, 250, true);


                    }




                }
                break;
        }

        adapter.notifyDataSetChanged();
    }

    public void selectImage(View view) {


        if (uploadImageModels.size() <= 9) {

            String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the option:");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
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
        } else {
            Toast.makeText(context, "You can select more then 10 images!", Toast.LENGTH_LONG).show();
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


        for (int i = 0; i < uploadImageModels.size(); i++) {
            params.put("image" + i, UtilityFunctions.convertToBase64(uploadImageModels.get(i).getImage()));
            params.put("tag" + i, uploadImageModels.get(i).getTag());
        }




        CustomVolley.getInsertPOSTDataDialog(context, params, "add_post.php", new VolleyCallback() {
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

                        new MyDialog(context, "success", "Your Post Added Successfully. Check It In Your Profile!")
                                .onPositiveClick("OK", new myOnClickListener() {
                                    @Override
                                    public void onButtonClick(MyDialog dialog) {
                                        finish();
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
                        city_ids = new String[city.length()];
                        city_names = new String[city.length()];

                        for (int i = 0; i < city.length(); i++) {
                            JSONObject c = city.getJSONObject(i);
                            city_ids[i] = c.getString("city_id");
                            city_names[i] = c.getString("city_name");

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

        final ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, city_names);
        final ListView list = dialog.findViewById(R.id.list_city);
        final TextView txt_back = dialog.findViewById(R.id.txt_back);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txt_city.setText(city_names[i]);
                city_id = city_ids[i];
                dialog.dismiss();
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




        View headerView = navigationView.getHeaderView(0);
        TextView txt_navigation_name = headerView.findViewById(R.id.txt_navigation_name);
        TextView txt_navigation_email = headerView.findViewById(R.id.txt_navigation_email);
        CircleImageView img_navigation = headerView.findViewById(R.id.img_navigation);

        Menu menu = navigationView.getMenu();
        if (!logPre.getBoolean("login_stauts", false)) {
            menu.findItem(R.id.nav_myshop).setVisible(false);
            menu.findItem(R.id.nav_profile).setVisible(false);
            menu.findItem(R.id.nav_myshop).setVisible(false);
            menu.findItem(R.id.nav_chat).setVisible(false);
            menu.findItem(R.id.nav_follower_post).setVisible(false);
            menu.findItem(R.id.nav_favourite).setVisible(false);
            menu.findItem(R.id.nav_logout).setTitle("Login").setIcon(getResources().getDrawable(R.drawable.ic_vpn_key_black_24dp));
        }else{
            menu.findItem(R.id.nav_myshop).setVisible(true);
            menu.findItem(R.id.nav_profile).setVisible(true);
            menu.findItem(R.id.nav_myshop).setVisible(true);
            menu.findItem(R.id.nav_chat).setVisible(true);
            menu.findItem(R.id.nav_follower_post).setVisible(true);
            menu.findItem(R.id.nav_favourite).setVisible(true);
            menu.findItem(R.id.nav_logout).setTitle("Logout").setIcon(getResources().getDrawable(R.drawable.ic_baseline_exit_to_app_24));
        }


        menu.findItem(R.id.nav_add_post).setChecked(true);


        txt_navigation_name.setText(logPre.getString("user_name", "Zama Shops"));
        txt_navigation_email.setText(logPre.getString("user_email", "info@zamashops.com"));
        if (!logPre.getString("user_image", "").equals("")) {
            Glide
                    .with(context)
                    .load(context.getResources().getString(R.string.url_image) + logPre.getString("user_image", ""))
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(img_navigation);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        initNavigation();
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
            Intent intent = new Intent(context, SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favourite) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, FavouriteActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_add_post) {



        } else if (id == R.id.nav_profile) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, ProfileActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
            }

        } else if (id == R.id.nav_shop) {
            Intent intent = new Intent(context, AllUserActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_follower_post) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, FollowerPostActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }
        } else if (id == R.id.nav_chat) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, AllChatActivity.class);
                intent.putExtra("activity",true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }
        } else if (id == R.id.nav_myshop) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, MyPostAcitivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }
        } else if (id == R.id.nav_logout) {
            if (logPre.getBoolean("login_stauts", false)) {
                logout();
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                intent.putExtra("status", true);
                startActivity(intent);
            }

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(context, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.appurl) + context.getPackageName());
            startActivity(Intent.createChooser(shareIntent, "Share ZamaShops using"));

        } else if (id == R.id.nav_rateus) {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void logout() {

        /*  Google  */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        /*  Google  */

        LoginManager.getInstance().logOut();

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        preEditor.remove("user_id");
        preEditor.remove("user_email");
        preEditor.remove("user_name");
        preEditor.remove("user_pass");
        preEditor.remove("user_contact");
        preEditor.remove("user_address");
        preEditor.remove("login_stauts");
        preEditor.remove("user_image");

        preEditor.apply();
        preEditor.commit();

        Intent intent = new Intent(context, MainActivity.class);
        finish();
        startActivity(intent);

    }

}
