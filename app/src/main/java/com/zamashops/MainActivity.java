package com.zamashops;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.zamashops.activities.AddAdsActivity2;
import com.zamashops.activities.AllChatActivity;
import com.zamashops.activities.AllUserActivity;
import com.zamashops.activities.FavouriteActivity;
import com.zamashops.activities.FollowerPostActivity;
import com.zamashops.activities.HelpActivity;
import com.zamashops.activities.LoginActivity;
import com.zamashops.activities.MyPostAcitivity;
import com.zamashops.activities.ProfileActivity;
import com.zamashops.activities.SearchActivity;
import com.zamashops.activities.TermActivity;
import com.zamashops.adapters.CategoryAdapter;
import com.zamashops.adapters.NewHighLightAdapter;
import com.zamashops.adapters.NormalProductAdapter;
import com.zamashops.adapters.ProductNormalAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.CategoryModel;
import com.zamashops.models.ProductModel;
import com.zamashops.pagination.HighLighPagination;
import com.zamashops.pagination.PaginationListener;
import com.zamashops.utility.CustomVolley;
import com.zamashops.view.BadgeNavigationDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.pagination.PaginationListener.PAGE_START;
import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NestedScrollView home_nested_scroll_veiw;
    SwipeRefreshLayout refresh_product;
    SQLiteDB db;

    StaggeredGridLayoutManager staggeredGridLayoutManager;

    Button txt_goto_top;
//    TextView txt_highlighted;
//    TextView txt_normal;

    //    RecyclerView list_category;
//    RecyclerView list_product_horizental;
    RecyclerView list_product_grid;
    ProgressBar progress_circular;
    ProgressBar sponsored_progress_circular;

    public static CategoryAdapter categoryAdapter;
    public static NewHighLightAdapter productAdapter;
    ProductNormalAdapter productNormalAdapter;


    public static ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    public static ArrayList<ProductModel> productModelList = new ArrayList<>();
    ArrayList<ProductModel> productModelListGrid = new ArrayList<>();


    Context context = MainActivity.this;

    public static int category_colums = 1;
    public static int normal_post_colums = 1;
    int limit_count = 0;


    int page_no = 0;

    /*   Filter data */
    String filterCities = "";
    String filterCategories = "";
    /*   Filter data */

    boolean shouldExecuteOnResume;

    /////////////////////////////////////////////////////////////

    int highlight_post_per_page = 10;
    int normal_post_per_page = 20;


    ////////////////////////////////////////////////////////////////


    /////////////////////////////////////////////////////////////////

    boolean check_refresh_highligh = false;
    boolean check_refresh_normal = false;

    boolean normal_loading = true;

    int minheight = 500;
    int height = 0;

    /////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    NormalProductAdapter normalProductAdapter;

    ////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////
    public static int hcurrentPage = PAGE_START;
    public static boolean hisLastPage = false;
    public static boolean hisLoading = false;
    ////////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shouldExecuteOnResume = false;
//        db = new SQLiteDB(context);
//
//        filterCities = db.allCityInString();
//        filterCategories = db.allCategoryInString();

        productAdapter = new NewHighLightAdapter(context, productModelList);
        categoryAdapter = new CategoryAdapter(context, categoryModelList);

        initColums();
        selectMainCategoryData(category_colums);
        selectHighlightProductData(highlight_post_per_page);


//        Bitmap bmp = Bitmap.createBitmap(449, 47, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bmp);


//        txt_highlighted = findViewById(R.id.txt_highlighted);
//        txt_normal = findViewById(R.id.txt_normal);


        txt_goto_top = findViewById(R.id.txt_goto_top);
        refresh_product = findViewById(R.id.refresh_product);
//        list_category = findViewById(R.id.list_category);
//        list_product_horizental = findViewById(R.id.list_product_horizental);
        list_product_grid = findViewById(R.id.list_product_grid);
//        home_nested_scroll_veiw = findViewById(R.id.home_nested_scroll_veiw);
//        progress_circular = findViewById(R.id.progress_circular);
//        sponsored_progress_circular = findViewById(R.id.sponsored_progress_circular);


//        initCategory();


        /************** NORMAL PRODUCT RECYCLEVIEW *****************/
         staggeredGridLayoutManager = new StaggeredGridLayoutManager(normal_post_colums, StaggeredGridLayoutManager.VERTICAL);
//        final GridLayoutManager gridLayoutManager2 = new GridLayoutManager(context, normal_post_colums);
        list_product_grid.setLayoutManager(staggeredGridLayoutManager);
        list_product_grid.setHasFixedSize(true);
        list_product_grid.setItemViewCacheSize(20);
        list_product_grid.setDrawingCacheEnabled(true);
        list_product_grid.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        normalProductAdapter = new NormalProductAdapter(context, productModelListGrid);
        list_product_grid.setAdapter(normalProductAdapter);


        selectNormalProductData(normal_post_per_page);

//        ViewCompat.setNestedScrollingEnabled(list_product_grid, false);

//        list_product_grid.setNestedScrollingEnabled(false);
//        home_nested_scroll_veiw.setSmoothScrollingEnabled(true);

//        home_nested_scroll_veiw.stopNestedScroll();





        list_product_grid.addOnScrollListener(new PaginationListener(staggeredGridLayoutManager, txt_goto_top) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                selectNormalProductData(normal_post_per_page);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


        txt_goto_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                staggeredGridLayoutManager.scrollToPosition(0);
                txt_goto_top.setVisibility(View.GONE);
//                list_product_grid.scrollTo(0, 0);
            }
        });


//        /************** Refresh PRODUCT RECYCLEVIEW *****************/
        refresh_product.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isLastPage) {
                    currentPage = PAGE_START;
                } else {
                    currentPage++;
                }



                productAdapter.clear();
                if (hisLastPage) {
                    hcurrentPage = HighLighPagination.PAGE_START;
                } else {
                    productAdapter.addLoading();
                    hcurrentPage++;
                }

                isLastPage = false;
                hisLastPage = false;

                normalProductAdapter.clear();
                normalProductAdapter.addLoading();




                selectNormalProductData(normal_post_per_page);
                selectHighlightProductData(highlight_post_per_page);

//                refresh_product.setRefreshing(false);
            }
        });
//        /************** Refresh PRODUCT RECYCLEVIEW *****************/


    }

    void initColums() {
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        boolean orientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;


        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                if (orientation) {
                    category_colums = 9;
                    normal_post_colums = 6;
                } else {
                    category_colums = 6;
                    normal_post_colums = 4;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                if (orientation) {
                    category_colums = 6;
                    normal_post_colums = 3;
                } else {
                    category_colums = 4;
                    normal_post_colums = 2;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                if (orientation) {
                    category_colums = 4;
                    normal_post_colums = 2;
                } else {
                    category_colums = 3;
                    normal_post_colums = 2;
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                if (orientation) {
                    category_colums = 12;
                    normal_post_colums = 5;
                } else {
                    category_colums = 9;
                    normal_post_colums = 7;
                }
                break;
            default:

        }

    }


    void selectMainCategoryData(int limit) {

        categoryModelList.clear();
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));

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
                                    m.getString("main_category_id"), m.getString("main_category_name"), m.getString("image")
                            ));
                        }
                        categoryModelList.add(new CategoryModel("", "All Category", ""));
                        categoryAdapter.notifyDataSetChanged();
                        staggeredGridLayoutManager.scrollToPosition(0);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void selectNormalProductData(final int limit) {

        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));
        params.put("limit_count", String.valueOf(currentPage));
        if (!filterCities.isEmpty()) {
            params.put("filterCities", filterCities);
        }

        if (!filterCategories.isEmpty()) {
            params.put("filterCategories", filterCategories);
        }
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
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


                        ArrayList<ProductModel> temoItems = new ArrayList<>();
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


                            temoItems.add(model);


                        }

                        if (temoItems.size() > 1) {
                            Collections.shuffle(temoItems);

                        }

                        if (currentPage != PAGE_START) normalProductAdapter.removeLoading();
                        normalProductAdapter.addItems(temoItems);
                        refresh_product.setRefreshing(false);
                        if (user_data.length() < normal_post_per_page) {
                            isLastPage = true;
                        } else {
                            normalProductAdapter.addLoading();
                        }
                        isLoading = false;

//                        productNormalAdapter.notifyDataSetChanged();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                normal_loading = true;

            }
        });
    }


    public void selectHighlightProductData(final int limit) {

        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));
        params.put("limit_count", String.valueOf(hcurrentPage));
        if (!filterCities.isEmpty()) {
            params.put("filterCities", filterCities);
        }

        if (!filterCategories.isEmpty()) {
            params.put("filterCategories", filterCategories);
        }
        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
        }


        CustomVolley.getInsertPOSTData2(context, params, "fetch_highlight_product.php", new VolleyCallback() {
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


                        ArrayList<ProductModel> temp = new ArrayList<>();
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


                            temp.add(model);


                        }

                        if (temp.size() > 1) {
                            Collections.shuffle(temp);

                        }


                        if (user_data.length() < highlight_post_per_page) {
                            hisLastPage = true;

                        }

                        if (hcurrentPage != HighLighPagination.PAGE_START)
                            productAdapter.removeLoading();
                        productAdapter.addItems(temp);
                        refresh_product.setRefreshing(false);
                        // check weather is last page or not


                        if (user_data.length() < highlight_post_per_page) {
                            hisLastPage = true;

                        } else {
                            productAdapter.addLoading();
                        }

                        hisLoading = false;


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
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

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.navigation_icon2);


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
        if (logPre.getString("user_email", "info@zamashops.com").contains("@")) {
            txt_navigation_email.setText(logPre.getString("user_email", "info@zamashops.com"));
        } else {
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

        if (shouldExecuteOnResume) {
//            productModelList.clear();
//            int temp = page_no;
//            page_no = 0;
//            selectHighlightProductData(10 * temp);
//            page_no = temp;
//
//            productModelListGrid.clear();
//
//            int temp2 = limit_count;
//            limit_count = 0;
//            selectNormalProductData(20 * temp2);
//            limit_count = temp2;
//


        } else {
            shouldExecuteOnResume = true;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Close App:");
            alert.setMessage("Are you sure to close the app");


            alert.setPositiveButton("Close App", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alert.setNeutralButton("Rate Us", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                    startActivity(intent);
                }
            });
            alert.create();
            alert.show();
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

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
                intent.putExtra("activity", true);
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
        } else if (id == R.id.nav_policy) {
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

    public void gotoSearch(View view) {
        Intent intent = new Intent(context, SearchActivity.class);
        startActivity(intent);
    }


}
