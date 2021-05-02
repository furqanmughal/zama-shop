package com.zamashops.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import com.zamashops.MainActivity;
import com.zamashops.MainLoginActivity;
import com.zamashops.R;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;
import com.zamashops.utility.Permission;
import com.zamashops.utility.UtilityFunctions;
import com.zamashops.utility.VolleyMultipartRequest;
import com.zamashops.view.BadgeNavigationDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;
import static com.zamashops.utility.UtilityFunctions.getFileDataFromDrawable;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button btn_review, btn_follow_unfollow;
    TextView txt_followers, txt_following, txt_user_name, txt_user_location, txt_user_contact, txt_user_email, txt_profile_name;
    TextView txt_profile_latter;
    CircleImageView img_profile;
    RatingBar user_rating;
    Button btn_post;
    TextView divider, txt_change_password;


    LinearLayout lin_follower,lin_following;
    LinearLayout lin_follower_post,lin_favrouit_post;

    Button btn_edit_profile;
    LinearLayout lin_my_post;

    String image_name = "";

    public static final int PICK_IMAGE = 1;


    Context context = ProfileActivity.this;
    String user_id = "";
    float rating = 0;

    PopupWindow popUp;
    boolean click = true;

    GoogleSignInClient mGoogleSignInClient;

    AlertDialog.Builder profileImageDialogBuilder;
    AlertDialog profile_image_dialog;
    ImageView img_profile_dialog_image;

    boolean email_privacy = false;
    boolean contact_privacy = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /* Facebook */
        FacebookSdk.sdkInitialize(getApplicationContext());
        /* Facebook */


        /*  Google  */
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        /*  Google  */

        initNavigation();
        initView();


    }


    void initView() {
        lin_following = findViewById(R.id.lin_following);
        lin_follower_post = findViewById(R.id.lin_follower_post);
        lin_favrouit_post = findViewById(R.id.lin_favrouit_post);
        lin_follower = findViewById(R.id.lin_follower);
        lin_my_post = findViewById(R.id.lin_my_post);
        txt_followers = findViewById(R.id.txt_followers);
        txt_following = findViewById(R.id.txt_following);
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_user_location = findViewById(R.id.txt_user_location);
        txt_user_contact = findViewById(R.id.txt_user_contact);
        txt_user_email = findViewById(R.id.txt_user_email);
        img_profile = findViewById(R.id.img_profile);
        txt_profile_name = findViewById(R.id.txt_profile_name);
        user_rating = findViewById(R.id.user_rating);
        txt_profile_latter = findViewById(R.id.txt_profile_latter);
        btn_edit_profile = findViewById(R.id.btn_edit);
        divider = findViewById(R.id.divider);
        divider = findViewById(R.id.divider);
        txt_change_password = findViewById(R.id.txt_change_password);

        lin_my_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MyPostAcitivity.class);
                startActivity(intent);
            }
        });

        profileImageDialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_fullimage, null);
        profileImageDialogBuilder.setView(dialogView);

        img_profile_dialog_image = dialogView.findViewById(R.id.dialog_img);
        Button btn_edit = dialogView.findViewById(R.id.btn_edit);



        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Permission.isStoragePermissionGranted(context)) {

                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, PICK_IMAGE);

                } else {
                    Toast.makeText(context, "First Grant The Storage Permission!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        profile_image_dialog = profileImageDialogBuilder.create();

        if (!logPre.getString("user_account_type", "").equals("1")) {
//            btn_edit_profile.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
            txt_change_password.setVisibility(View.GONE);
        }

        txt_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(view);
            }
        });

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        lin_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFollower(view);
            }
        });

        lin_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFollowing(view);
            }
        });

        lin_follower_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFollowerPost(view);
            }
        });
        lin_favrouit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoFavourite(view);
            }
        });


    }


    void selectData() {


        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", logPre.getString("user_id", ""));


        CustomVolley.getInsertPOSTData(context, params, "user/fetch_login_user_details.php", new VolleyCallback() {
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

                        for (int i = 0; i < user_data.length(); i++) {
                            JSONObject user = user_data.getJSONObject(i);
                            txt_user_name.setText(user.getString("user_name"));
                            if(user.getString("user_email").contains("@")) {
                                txt_user_email.setText(user.getString("user_email"));
                            }else{
                                txt_user_email.setText("null");
                            }
                            txt_user_location.setText(user.getString("user_address"));
                            txt_user_contact.setText(user.getString("user_contact"));
                            txt_profile_name.setText(user.getString("user_name"));
                            txt_followers.setText(user.getString("followers"));
                            txt_following.setText(user.getString("following"));

                            user_rating.setRating(Float.parseFloat(user.getString("reviews")));
                            final String id = user.getString("user_id");
                            user_id = id;

                            image_name = user.getString("user_image");

                            if (user.getString("email_privacy").equals("0")) {
                                email_privacy = false;
                            } else {
                                email_privacy = true;
                            }


                            if (user.getString("contact_privacy").equals("0")) {
                                contact_privacy = false;
                            } else {
                                contact_privacy = true;
                            }




                            String image_name = "";
                            if (!user.getString("user_image").equals("")) {
                                if (user.getString("user_image").contains("/")) {
                                    image_name = logPre.getString("user_image", "");
                                } else {
                                    image_name = context.getResources().getString(R.string.url_image) + user.getString("user_image");
                                }

                                preEditor.putString("user_image",image_name);
                                preEditor.apply();
                                preEditor.commit();

                                Picasso.get().load(image_name)
                                        .placeholder(R.drawable.loding_blue)
                                        .error(R.drawable.image_not_found)
                                        .into(img_profile_dialog_image);

                                Picasso.get().load(image_name)
                                        .placeholder(R.drawable.loding_blue)
                                        .error(R.drawable.image_not_found)
                                        .into(img_profile);
                            }


                        }


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


        menu.findItem(R.id.nav_profile).setChecked(true);

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
        if (!logPre.getString("user_image", "").equals("")) {
            if (logPre.getString("user_image", "").contains("/")) {
                image_name = logPre.getString("user_image", "");
            } else {
                image_name = context.getResources().getString(R.string.url_image) + logPre.getString("user_image", "");
            }
            Picasso.get().load(image_name)
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
        getMenuInflater().inflate(R.menu.menu_profile_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_privacy) {
            if (logPre.getBoolean("login_stauts", false)) {
                Intent intent = new Intent(context, PrivacyActivity.class);
                intent.putExtra("email", email_privacy);
                intent.putExtra("contact", contact_privacy);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, MainLoginActivity.class);
                startActivity(intent);
            }
        }
        return true;
    }

    public void gotoFavourite(View view) {
        Intent intent = new Intent(ProfileActivity.this, FavouriteActivity.class);
        startActivity(intent);
    }

    public void gotoFollowerPost(View view) {
        Intent intent = new Intent(ProfileActivity.this, FollowerPostActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        selectData();
    }

    public void logOut(View view) {


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

    public void changePassword(View view) {


        final EditText oldPass = new EditText(context);
        final EditText newPass = new EditText(context);
        final EditText confirmPass = new EditText(context);


        oldPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

        oldPass.setHint("Old Password");
        newPass.setHint("New Password");
        confirmPass.setHint("Confirm Password");
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);


        ll.setPadding(20, 20, 20, 20);

        ll.addView(oldPass);

        ll.addView(newPass);
        ll.addView(confirmPass);


        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Change Passsword")
                .setIcon(R.drawable.ic_vpn_key_black_24dp)
                .setView(ll)
                .setPositiveButton("Change Password", null)
                .setNegativeButton("Cancel", null)
                .show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpass = oldPass.getText().toString();
                String newpass = newPass.getText().toString();
                String confirmpass = confirmPass.getText().toString();


                String oldpassworddatabase = logPre.getString("user_pass", "");
                if (oldpass.isEmpty()) {
                    oldPass.setError("Old Password May not be empty!");
                    oldPass.requestFocus();
                } else if (newpass.isEmpty() || newpass.length() < 5) {
                    newPass.setError("New Password must be at least 5 characters!");
                    newPass.requestFocus();
                } else if (!newpass.equals(confirmpass)) {
                    confirmPass.setError("Confirm Password Does not match!");
                    confirmPass.requestFocus();
                } else if (!oldpass.equals(oldpassworddatabase)) {
                    oldPass.setError("Old Password is incorrect!");
                    oldPass.requestFocus();
                } else {
                    updatePassword(newpass);
                    dialog.cancel();
                }
            }
        });
    }

    private void updatePassword(final String newpass) {


        HashMap<String, String> params = new HashMap<>();
        params.put("update_pass", String.valueOf(true));
        params.put("user_id", logPre.getString("user_id", ""));
        params.put("password", newpass);


        CustomVolley.getInsertPOSTData(context, params, "user/update_password.php", new VolleyCallback() {
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

                        preEditor.putString("user_pass", newpass);

                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setIcon(android.R.drawable.ic_dialog_info)
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
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void gotoFollower(View view) {
//        Intent intent = new Intent(context, SellerProfileUserActivity.class);
//        intent.putExtra("post_user_id", logPre.getString("user_id", ""));
//        intent.putExtra("position", 0);
//        startActivity(intent);

        Intent intent = new Intent(context, FollowersActivity.class);
        startActivity(intent);

    }

    public void gotoFollowing(View view) {
        Intent intent = new Intent(context, FollowingActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(context, SellerProfileUserActivity.class);
//        intent.putExtra("post_user_id", logPre.getString("user_id", ""));
//        intent.putExtra("position", 1);
//        startActivity(intent);

    }

    public void fullImage(View view) {


        profile_image_dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != imageReturnedIntent) {
            // Uri selectedImage = imageReturnedIntent.getData();
            Uri selectedImage = imageReturnedIntent.getData();

            Uri imgUri = Uri.fromFile(new File(imageReturnedIntent.getData().getPath()));
            Bitmap cmpBitmap = UtilityFunctions.compressUriQuality(context, imageReturnedIntent.getData());

            cmpBitmap = UtilityFunctions.getScaledDownBitmap(cmpBitmap, 800, false);


            String filePath = UtilityFunctions.getRealPathFromURI(context, selectedImage);
            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
            String file_name = filePath.substring(filePath.lastIndexOf("/") + 1);
            final Bitmap bm = BitmapFactory.decodeFile(file_name);


            sendImage(cmpBitmap, file_extn);


        }


    }


    void sendImage(final Bitmap bm, final String extension) {


        /////////////////////   VOLLEY MULTIPART ///////////////////////////////////////////////
        final ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("Loading!");
        progressDialog.setMessage("Please wait a while...");


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, getResources().getString(R.string.url) + "user/upload_image.php",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject result = new JSONObject(new String(response.data));


//                               Toast.makeText(context,response.toString(),Toast.LENGTH_LONG).show();

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


                                MyDialog dialog = new MyDialog(context, "Success!", result.getString("msg"));
                                dialog.onPositiveClick("OK", new myOnClickListener() {
                                    @Override
                                    public void onButtonClick(MyDialog dialog) {
                                        profile_image_dialog.dismiss();
                                        dialog.dismiss();
                                        recreate();
                                    }
                                });
//

                            }
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("upload_image", String.valueOf(true));
                params.put("user_id", user_id);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(extension, getFileDataFromDrawable(bm)));


                return params;
            }
        };

        //adding the request to volley
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
        progressDialog.show();
    }


}
