package com.zamashops.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.zamashops.adapters.AllUserAdapter;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.UserModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.view.BadgeNavigationDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;

public class AllUserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean shouldExecuteOnResume;
    LinearLayout lin_empty_list;
    RecyclerView lis_user;
    ArrayList<UserModel> userModels = new ArrayList<>();

    Context context = AllUserActivity.this;
    AllUserAdapter userAdapter;
    int limit_count = 0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ProgressBar progress_circular;
    boolean loadmore = true;

    String search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);


        if (getIntent().hasExtra("search")) {
            search = getIntent().getStringExtra("search");
        }

        shouldExecuteOnResume = false;

        lin_empty_list = findViewById(R.id.lin_empty_list);
        lis_user = findViewById(R.id.lis_user);
        progress_circular = findViewById(R.id.progress_circular);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        lis_user.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lis_user.getContext(),
                linearLayoutManager.getOrientation());
        lis_user.addItemDecoration(dividerItemDecoration);


        lis_user.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();


                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (loadmore) {
                            selectData(20, false);
                        }
                    }

                }
            }
        });


        userAdapter = new AllUserAdapter(context, userModels);
        lis_user.setAdapter(userAdapter);

        selectData(20, false);

        initNavigation();


    }


    void selectData(final int limit, final boolean update) {

        if(!update) {
            progress_circular.setVisibility(View.VISIBLE);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("limit", String.valueOf(limit));
        params.put("limit_count", String.valueOf(limit_count++));

        if (!search.isEmpty()) {
            params.put("search", search);
        }

        if (logPre.getBoolean("login_stauts", false)) {
            params.put("user_id", logPre.getString("user_id", ""));
        }

        CustomVolley.getInsertPOSTData2(context, params, "fetch_all_user.php", new VolleyCallback() {
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

                        if (user_data.length() == 0) {
                            loadmore = false;
                            if (limit_count == 1) {
                                lin_empty_list.setVisibility(View.VISIBLE);
                                lis_user.setVisibility(View.GONE);
                            }
                        } else {


                            for (int i = 0; i < user_data.length(); i++) {
                                JSONObject user = user_data.getJSONObject(i);

                                UserModel model = new UserModel(
                                        user.getString("user_id"), user.getString("user_name"), user.getString("user_email"),
                                        user.getString("user_contact"), user.getString("user_address"), user.getString("user_password"),
                                        user.getString("account_type"),
                                        user.getString("user_image"), user.getBoolean("follow"), user.getString("reviews")
                                );

                                model.setToken(user.getString("firebase_token"));

                                if (update) {
                                    userModels.get(i).setUser_id(user.getString("user_id"));
                                    userModels.get(i).setUser_name(user.getString("user_name"));
                                    userModels.get(i).setUser_email(user.getString("user_email"));
                                    userModels.get(i).setUser_contact(user.getString("user_contact"));
                                    userModels.get(i).setUser_address(user.getString("user_address"));
                                    userModels.get(i).setUser_password(user.getString("user_password"));
                                    userModels.get(i).setUser_image(user.getString("user_image"));
                                    userModels.get(i).setFollow(user.getBoolean("follow"));
                                    userModels.get(i).setReviews(user.getString("reviews"));

                                } else {
                                    userModels.add(model);
                                }


                            }
                            userAdapter.notifyDataSetChanged();
//                            userAdapter.notifyItemMoved(limit_count,userModels.size()-1);
                            progress_circular.setVisibility(View.GONE);

                        }
                    }

                } catch (JSONException e) {
                    progress_circular.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNavigation();

        if (shouldExecuteOnResume) {
//            userModels.clear();
            int temp = limit_count;
            limit_count = 0;
            selectData(userModels.size(), true);
            limit_count = temp;

        } else {
            shouldExecuteOnResume = true;
        }
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


        menu.findItem(R.id.nav_shop).setChecked(true);

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


    public void gotoSearch(View view) {
        Intent intent = new Intent(context, SearchUserActivity.class);
        startActivity(intent);
    }
}
