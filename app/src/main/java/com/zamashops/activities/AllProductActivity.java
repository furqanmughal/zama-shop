package com.zamashops.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.zamashops.adapters.AllProductPostAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.CategoryModel;
import com.zamashops.models.ProductModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.view.BadgeNavigationDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;

public class AllProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView list_all_product;
    ArrayList<ProductModel> productModels = new ArrayList<>();
    LinearLayout lin_empty_list;

    Context context = AllProductActivity.this;
    AllProductPostAdapter PostAdapter;

    /////////////////////////////Filter By/////////////////////////////////////////////
    String cat_id = "";
    String search = "";
    String filter_city_id = "";
    /////////////////////////////Filter By/////////////////////////////////////////////
    int page_no = 0;

    /* Scroll */
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loadmore = true;
    /* Scroll */

    ///////////////////////////// City Dialog///////////////////////////////////////////////////////////
    ArrayList<String> city_ids = new ArrayList<>();
    ArrayList<String> city_names = new ArrayList<>();

    ArrayList<String> temp_city_ids = new ArrayList<>();
    ArrayList<String> temp_city_names = new ArrayList<>();

    /////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////Category dialog ///////////////////////////////////////////////////////
    ArrayList<CategoryModel> categoryModels = new ArrayList<>();
    ArrayList<String> main_categories = new ArrayList<>();
    String cat_string = "";
    ///////////////////////Category dialog ///////////////////////////////////////////////////////


    Button btn_city;
    Button btn_cat;

    String cat_name = "All Categories";


    ProgressBar progress_circular;

    SwipeRefreshLayout refresh_product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);


        if (getIntent().hasExtra("cat_id")) {
            cat_id = getIntent().getStringExtra("cat_id");
            cat_name = getIntent().getStringExtra("cat_name");
        }

        if (getIntent().hasExtra("search")) {
            search = getIntent().getStringExtra("search");
        }

        refresh_product = findViewById(R.id.refresh_product);
        list_all_product = findViewById(R.id.list_all_product);
        lin_empty_list = findViewById(R.id.lin_empty_list);
        progress_circular = findViewById(R.id.progress_circular);
        btn_city = findViewById(R.id.btn_city);
        btn_cat = findViewById(R.id.btn_cat);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        list_all_product.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list_all_product.getContext(),
                linearLayoutManager.getOrientation());
        list_all_product.addItemDecoration(dividerItemDecoration);


        PostAdapter = new AllProductPostAdapter(context, productModels);
        list_all_product.setAdapter(PostAdapter);
        list_all_product.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();


                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (loadmore) {
                            selectData(30);
                        }
                    }

                }
            }
        });

        btn_cat.setText(cat_name);

        initNavigation();

        fetchCity();
        fetchCategory();

        selectData(30);

        btn_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCity();
            }
        });
        btn_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCategory();
            }
        });


        /************** Refresh PRODUCT RECYCLEVIEW *****************/
        refresh_product.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (loadmore) {
                    productModels.clear();
                    selectData(30);
                } else {

//                    int temp = page_no;
                    page_no = 0;
                    productModels.clear();
                    loadmore = true;
                    selectData( 30);
//                    page_no = temp;


                }
                refresh_product.setRefreshing(false);
            }
        });
        /************** Refresh PRODUCT RECYCLEVIEW *****************/


    }

    void selectData(int limit) {
        progress_circular.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();

        params.put("limit", String.valueOf(limit));
        params.put("limit_count", String.valueOf(page_no++));

        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
        }
        if (!cat_id.isEmpty()) {
            params.put("cat_id", cat_id);
        }

        if (!search.isEmpty()) {
            params.put("search", search);
        }

        if (!filter_city_id.isEmpty()) {
            params.put("city_id", filter_city_id);
        }


        CustomVolley.getInsertPOSTData3(context, params, "fetch_all_product.php", new VolleyCallback() {
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

                        if (user_data.length() < 30) {
                            loadmore = false;
                        }

                        ArrayList<ProductModel> tempProduct = new ArrayList<>();
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
                                    p.getString("user_id"), p.getString("category"), p.getString("city"),
                                    p.getString("view")
                            );
                            model.setFavorite(p.getBoolean("favourite"));
                            tempProduct.add(model);
                        }
                        if(tempProduct.size() > 1) {
                            Collections.shuffle(tempProduct);
                        }
                        productModels.addAll(tempProduct);
                        PostAdapter.notifyDataSetChanged();

                        if (productModels.size() == 0) {
                            lin_empty_list.setVisibility(View.VISIBLE);
                            list_all_product.setVisibility(View.GONE);
                        } else {
                            lin_empty_list.setVisibility(View.GONE);
                            list_all_product.setVisibility(View.VISIBLE);
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

    void fetchCity() {
        CustomVolley.getInsertPOSTData(context, new HashMap<String, String>(), "fetch_city.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    if (error) {
                    } else {
                        JSONArray city = result.getJSONArray("city");


                        temp_city_ids.add("-1");
                        city_ids.add("-1");
                        city_names.add("All Cities");
                        temp_city_names.add("All Cities");

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
                if (i == 0) {
                    btn_city.setText(temp_city_names.get(i));
                    filter_city_id = "";
                    page_no = 0;
                    productModels.clear();
                    selectData(30);
                } else {
                    btn_city.setText(temp_city_names.get(i));
                    filter_city_id = temp_city_ids.get(i);
                    page_no = 0;
                    productModels.clear();
                    selectData(30);
                }

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

                for (int j = 0; j < city_names.size(); j++) {
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
                        main_categories.add("All Categories");
                        categoryModels.add(new CategoryModel("-1", "All Categories",
                                ""));
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

    void selectCategory() {



        final Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_main_category);

        final ArrayAdapter adapter2 = new ArrayAdapter(context, android.R.layout.simple_list_item_1, main_categories);
        final ListView list = dialog.findViewById(R.id.list_main_category);
        list.setAdapter(adapter2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


                if (i == 0) {
                    btn_cat.setText(main_categories.get(i));
                    cat_id = "";
                    page_no = 0;
                    productModels.clear();
                    selectData(30);
                    dialog.dismiss();
                } else {




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
                                    cat_string = "";
                                    JSONArray main_category = result.getJSONArray("sub_category");
                                    sub_category.add("All Categories");
                                    sub_categorylist.add(new CategoryModel("-1", categoryModels.get(i).getCat_name(),
                                            ""));
                                    for (int i = 0; i < main_category.length(); i++) {
                                        JSONObject category = main_category.getJSONObject(i);
                                        sub_category.add(category.getString("sub_category_name"));
                                        cat_string += category.getString("sub_category_id")+",";
                                        sub_categorylist.add(new CategoryModel(category.getString("sub_category_id"), category.getString("sub_category_name"),
                                                category.getString("image")));
                                    }

                                    adapter3.notifyDataSetChanged();

                                    if (cat_string != null && cat_string.length() > 0 && cat_string.charAt(cat_string.length() - 1) == ',') {
                                        cat_string = cat_string.substring(0, cat_string.length() - 1);
                                    }
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

                            if (i == 0) {
                                btn_cat.setText(sub_categorylist.get(i).getCat_name());
                                cat_id = cat_string;
                                page_no = 0;
                                productModels.clear();
                                selectData(30);
                            } else {
                                btn_cat.setText(sub_categorylist.get(i).getCat_name());
                                page_no = 0;
                                cat_id = sub_categorylist.get(i).getCat_id();
                                productModels.clear();
                                selectData(30);
                            }
                            dialog.dismiss();
                            dialog2.dismiss();
                        }
                    });


                    dialog2.show();
                }
            }
        });


        dialog.show();
    }


    public void gotoSearch(View view) {
        Intent intent = new Intent(context, SearchActivity.class);
        startActivity(intent);
    }

    void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final BadgeNavigationDrawable drawerIcon = new BadgeNavigationDrawable(context);


        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
//        toggle.setDrawerArrowDrawable(drawerIcon);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        TextView txt_navigation_name = headerView.findViewById(R.id.txt_navigation_name);
        TextView txt_navigation_email = headerView.findViewById(R.id.txt_navigation_email);
        CircleImageView img_navigation = headerView.findViewById(R.id.img_navigation);

        final Menu menu = navigationView.getMenu();
        if (!logPre.getBoolean("login_stauts", false)) {
            menu.findItem(R.id.nav_myshop).setVisible(false);
            menu.findItem(R.id.nav_profile).setVisible(false);
            menu.findItem(R.id.nav_myshop).setVisible(false);
            menu.findItem(R.id.nav_chat).setVisible(false);
            menu.findItem(R.id.nav_follower_post).setVisible(false);
            menu.findItem(R.id.nav_favourite).setVisible(false);
            menu.findItem(R.id.nav_logout).setTitle("Login").setIcon(getResources().getDrawable(R.drawable.ic_vpn_key_black_24dp));
        } else {
            menu.findItem(R.id.nav_myshop).setVisible(true);
            menu.findItem(R.id.nav_profile).setVisible(true);
            menu.findItem(R.id.nav_myshop).setVisible(true);
            menu.findItem(R.id.nav_chat).setVisible(true);
            menu.findItem(R.id.nav_follower_post).setVisible(true);
            menu.findItem(R.id.nav_favourite).setVisible(true);
            menu.findItem(R.id.nav_logout).setTitle("Logout").setIcon(getResources().getDrawable(R.drawable.ic_baseline_exit_to_app_24));
        }


        menu.findItem(R.id.nav_home).setChecked(true);

        final MenuItem inbox = menu.findItem(R.id.nav_chat);

        HashMap<String, String> params = new HashMap<>();
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
            CustomVolley.getInsertPOSTData2(context, params, "chat/fetch_chat_count.php", new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {


                    try {

                        boolean error = result.getBoolean("error");
                        if (error) {
                        } else {
                            int count = result.getInt("count");
                            if (count <= 0) {
                                inbox.setActionView(null);
                            } else {
                                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.bage, null, false);
                                inbox.setActionView(view);
                                View inbox_view = inbox.getActionView();
                                final TextView txt_bage = inbox_view.findViewById(R.id.txt_bage);
                                txt_bage.setText(String.valueOf(count));
                                drawerIcon.setCount(String.valueOf(count));
                                toggle.setDrawerArrowDrawable(drawerIcon);
                                toggle.syncState();

                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } else {
            inbox.setActionView(null);
        }


        txt_navigation_name.setText(logPre.getString("user_name", "Zama Shops"));
        if(logPre.getString("user_email", "info@zamashops.com").contains("@")) {
            txt_navigation_email.setText(logPre.getString("user_email", "info@zamashops.com"));
        }else{
            txt_navigation_email.setText("");
        }

        String image_name = "";
        if (!logPre.getString("user_image", "").isEmpty()) {
            if (logPre.getString("user_image", "").contains("/")) {
                image_name = logPre.getString("user_image", "");
            } else {
                image_name = context.getResources().getString(R.string.url_image) + logPre.getString("user_image", "");
            }
            Glide
                    .with(context)
                    .load(image_name)
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

            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, AddAdsActivity2.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                startActivity(intent);
            }


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
        }else if (id == R.id.nav_policy) {
            Intent intent = new Intent(context, TermActivity.class);
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
        preEditor.remove("user_image");
        preEditor.remove("user_contact");
        preEditor.remove("user_address");
        preEditor.remove("user_account_type");
        preEditor.remove("login_stauts");
        preEditor.remove("token");
        preEditor.apply();
        preEditor.commit();

        Intent intent = new Intent(context, MainActivity.class);
        finish();
        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_add) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, AddAdsActivity2.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                startActivity(intent);
            }
        }
        return true;
    }

}
