package com.zamashops;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.zamashops.activities.LoginActivity;
import com.zamashops.activities.SignUpActivity;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import static com.zamashops.utility.App.preEditor;

public class MainLoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    private static final String TAG = "MainLoginActivity";
    private static final String EMAIL = "email";
    private static final int RC_SIGN_IN = 1001;

    Context context = MainLoginActivity.this;

    GoogleSignInClient mGoogleSignInClient;

    SignInButton signInButton;

    String token = "";

    Class gotoClass = MainActivity.class;

    boolean login_status = false;

    String term_and_condition = "";

    LoginButton btn_facebook;

    private ProfileTracker mProfileTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);


        btn_facebook = findViewById(R.id.btn_facebook);

        ////////////////////////////////////////////////////////////////////////

//        HashMap<String, String> params = new HashMap<>();
//        CustomVolley.getInsertPOSTData(context, params, "term_and_condition.php", new VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//
//
//                try {
//
//                    boolean error = result.getBoolean("error");
//                    if (error) {
//
//                    } else {
//                        String user_data = result.getString("result");
//                        term_and_condition = user_data;
//
//                    }
//
//
//                } catch (JSONException e) {
////                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//
//            }
//        });

        ////////////////////////////////////////////////////////////////////////


        if (getIntent().hasExtra("status")) {
            login_status = getIntent().getBooleanExtra("status", false);
        }


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();


                        //  Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });

        /* Facebook */
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        /* Facebook */

        /* Google */
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        /* Google */

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }


        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                handleFacebookToken(loginResult.getAccessToken());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback(){

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {


                                try {
                                    Log.d("JSON: ", "done");
                                    Log.d("JSON: ", object.toString());
                                    String facebook_id = object.getString("id");
                                    String email = "";
                                    if (object.has("email")) {
                                        //get Value of video
                                        email = object.getString("email");
                                    }else{
                                        email  = facebook_id;
                                    }
                                    String name = object.getString("name");
                                    URL profilePicUrl = new URL(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    Log.d("JSON: ", email);
                                    Log.d("JSON: ", String.valueOf(object.getJSONObject("picture")));

                                    sendData(facebook_id, name, email, String.valueOf(profilePicUrl), token);


                                } catch (JSONException e) {

                                    e.printStackTrace();


                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email ,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, callback);

//        LoginManager.getInstance().retrieveLoginStatus(this, new LoginStatusCallback() {
//            @Override
//            public void onCompleted(AccessToken accessToken) {
//                handleFacebookToken(accessToken);
//                // User was previously logged in, can log them in directly here.
//                // If this callback is called, a popup notification appears that says
//                // "Logged in as <User Name>"
//            }
//            @Override
//            public void onFailure() {
//                // No access token could be retrieved for the user
//            }
//            @Override
//            public void onError(Exception exception) {
//                // An error occurred
//            }
//        });

    }

    GraphRequest graphRequest;

    private void handleFacebookToken(final AccessToken accessToken) {


        final Profile profile = Profile.getCurrentProfile();


        if (profile == null) {


            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    if (currentProfile != null) {
                        final String facebook_id = currentProfile.getId();
                        final String name = currentProfile.getName();
                        final String profile_image = currentProfile.getProfilePictureUri(300, 300).toString();


                        graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    Log.d("JSON: ", "done");
                                    Log.d("JSON: ", object.toString());
                                    String email = object.getString("email");
                                    URL profilePicUrl = new URL(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    Log.d("JSON: ", email);
                                    Log.d("JSON: ", String.valueOf(object.getJSONObject("picture")));

                                    sendData(facebook_id, name, email, String.valueOf(profilePicUrl), token);


                                } catch (JSONException e) {

                                    e.printStackTrace();

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture.type(large),location");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();

                    }

                    mProfileTracker.stopTracking();
                }
            };
            // no need to call startTracking() on mProfileTracker
            // because it is called by its constructor, internally.
        } else {

            if (profile != null) {
                final String facebook_id = profile.getId();
                final String name = profile.getName();
                final String profile_image = profile.getProfilePictureUri(300, 300).toString();

                graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            Log.d("JSON: ", "done");
                            Log.d("JSON: ", object.toString());
                            String email = "";
                            if (object.has("email")) {
                                //get Value of video
                                email = object.getString("email");
                            }else{
                                email  = facebook_id;
                            }


                            URL profilePicUrl = new URL(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                            Log.d("JSON: ", email);
                            Log.d("JSON: ", String.valueOf(object.getJSONObject("picture")));

                            sendData(facebook_id, name, email, String.valueOf(profilePicUrl), token);


                        } catch (JSONException e) {

                            e.printStackTrace();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large),location");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }


        }

    }


    void sendData(final String login_id, final String name, final String email, final String image, final String token, final boolean... create_account) {


        HashMap<String, String> params = new HashMap<>();
        params.put("facebook_login", String.valueOf(true));
        params.put("login_id", login_id);
        params.put("image", image);
        params.put("user_name", name);
        params.put("user_email", email);
        params.put("token", token);


        CustomVolley.getInsertPOSTData2(context, params, "login/facebook_login.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
//                    boolean check_account_login = result.getBoolean("login_status");
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

                        JSONObject user_data = result.getJSONObject("data");
                        preEditor.putString("user_id", user_data.getString("user_id"));
                        preEditor.putString("user_email", email);
                        preEditor.putString("user_name", user_data.getString("user_name"));
                        preEditor.putString("user_pass", login_id);
                        preEditor.putString("user_contact", user_data.getString("user_contact"));
                        preEditor.putString("user_image", user_data.getString("user_image"));
                        preEditor.putString("user_address", user_data.getString("user_address"));
                        preEditor.putString("user_account_type", user_data.getString("account_type"));
                        preEditor.putString("token", token);
                        preEditor.putBoolean("login_stauts", true);
                        preEditor.apply();
                        preEditor.commit();

                        if (login_status) {
                            finish();
                        } else {
                            Intent intent2 = new Intent(context, MainActivity.class);
                            startActivity(intent2);
                        }

                    }


                } catch (JSONException e) {
//                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            googleLogin(personId, personName, personEmail, String.valueOf(personPhoto), token);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }


    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount acct) {
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            googleLogin(personId, personName, personEmail, String.valueOf(personPhoto), token);

        } else
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
    }

    public void login(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    public void signUp(View view) {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtra("status", login_status);
        finish();
        startActivity(intent);
    }

    public void signIn(View view) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("status", login_status);
        finish();
        startActivity(intent);
    }


    void googleLogin(final String login_id, final String name, final String email, final String image, final String token, final boolean... create_account) {


        HashMap<String, String> params = new HashMap<>();
        params.put("gmail_login", String.valueOf(true));
        params.put("login_id", login_id);
        params.put("image", image);
        params.put("user_name", name);
        params.put("user_email", email);
        params.put("token", token);

        if (create_account.length > 0) {
            params.put("create_account", "true");
        }

        CustomVolley.getInsertPOSTData(context, params, "login/google_login.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {


                try {

                    boolean error = result.getBoolean("error");
                    boolean check_account_login = result.getBoolean("login_status");
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
                        if (check_account_login) {
                            if (create_account.length > 0) {
                                JSONObject user_data = result.getJSONObject("data");
                                preEditor.putString("user_id", user_data.getString("user_id"));
                                preEditor.putString("user_email", email);
                                preEditor.putString("user_name", user_data.getString("user_name"));
                                preEditor.putString("user_pass", login_id);
                                preEditor.putString("user_contact", user_data.getString("user_contact"));
                                preEditor.putString("user_image", user_data.getString("user_image"));
                                preEditor.putString("user_address", user_data.getString("user_address"));
                                preEditor.putString("user_account_type", user_data.getString("account_type"));
                                preEditor.putString("token", token);
                                preEditor.putBoolean("login_stauts", true);
                                preEditor.apply();
                                preEditor.commit();

                                if (login_status) {
                                    finish();
                                } else {
                                    Intent intent2 = new Intent(context, MainActivity.class);
                                    startActivity(intent2);
                                }
                            } else {
                                termAndCondition(login_id, name, email, image, token);
                            }
                        } else {
                            JSONObject user_data = result.getJSONObject("data");
                            preEditor.putString("user_id", user_data.getString("user_id"));
                            preEditor.putString("user_email", email);
                            preEditor.putString("user_name", user_data.getString("user_name"));
                            preEditor.putString("user_pass", login_id);
                            preEditor.putString("user_contact", user_data.getString("user_contact"));
                            preEditor.putString("user_image", user_data.getString("user_image"));
                            preEditor.putString("user_address", user_data.getString("user_address"));
                            preEditor.putString("user_account_type", user_data.getString("account_type"));
                            preEditor.putString("token", token);
                            preEditor.putBoolean("login_stauts", true);
                            preEditor.apply();
                            preEditor.commit();

                            if (login_status) {
                                finish();
                            } else {

                                Intent intent2 = new Intent(context, MainActivity.class);
                                startActivity(intent2);
                            }
                        }


                    }


                } catch (JSONException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }


    void termAndCondition(final String login_id, final String name, final String email, final String image, final String token, final boolean... create_account) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_term_condition, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        String webUrl = "http://zamashops.com/term_and_condition.php";
        final WebView mWebView = (WebView) dialogView.findViewById(R.id.webview);
        final ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
        progressBar.setMax(100);
//        final TextView txt_term_condition = dialogView.findViewById(R.id.txt_term_condition);
        final Button txt_cancel = dialogView.findViewById(R.id.txt_cancel);


        Button btn_decline = dialogView.findViewById(R.id.btn_decline);
        Button btn_accept = dialogView.findViewById(R.id.btn_accept);


//        txt_term_condition.setText(Html.fromHtml(term_and_condition));


        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                progressBar.setProgress(progress); //Make the bar disappear after URL is loaded
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebView.loadUrl(webUrl);


        final AlertDialog alertDialog = dialogBuilder.create();

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                googleLogin(login_id, name, email, image, token, true);
            }
        });

        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                mGoogleSignInClient.signOut();
                MyDialog dialog = new MyDialog(context, "To Sign up!", "You must accept Term & condition.");
                dialog.onPositiveClick("OK", new myOnClickListener() {
                    @Override
                    public void onButtonClick(MyDialog dialog) {
                        dialog.dismiss();
                    }
                });
                dialog.setDialog(MyDialog.DANGER);
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                mGoogleSignInClient.signOut();
                MyDialog dialog = new MyDialog(context, "To Sign up!", "You must accept Term & condition.");
                dialog.onPositiveClick("OK", new myOnClickListener() {
                    @Override
                    public void onButtonClick(MyDialog dialog) {
                        dialog.dismiss();
                    }
                });
                dialog.setDialog(MyDialog.DANGER);

            }
        });

        alertDialog.show();
    }




}
