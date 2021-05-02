package com.zamashops;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.interfaces.myOnClickListener;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyDialog;

import org.json.JSONObject;

import java.util.HashMap;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.preEditor;

public class SplashScreenActivity extends AppCompatActivity {

    Handler handler;
    Context context = SplashScreenActivity.this;

    String token = "";

    public static int REQUEST_CODE_UPDATE = 78;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //App Update Code//




        // Creates instance of the manager.
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);

        // Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();


        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE, (Activity) context,REQUEST_CODE_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                }

                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    // Request the update.

                    try {
                        appUpdateManager.startUpdateFlowForResult(result,AppUpdateType.FLEXIBLE, (Activity) context,REQUEST_CODE_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }

                }


            }
        });




        //App Update Code//


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


                        if (logPre.getBoolean("login_stauts", false)) {
                            // updateToken(token);
                            checklogin(token);
                        }else{
                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 2000);
                        }


                        //  Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_UPDATE){
            Toast.makeText(context,"Start Download",Toast.LENGTH_LONG).show();
            if(resultCode != RESULT_OK){
                Log.d("update_app: ",resultCode+"");
            }
        }

    }

    //    void updateToken(String token) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("update_token", String.valueOf(true));
//        params.put("user_id", logPre.getString("user_id", ""));
//        params.put("token", token);
//
//        CustomVolly.getInsertPOSTData3(context, params, "user/update_token.php", new VolleyCallback() {
//            @Override
//            public void onSuccess(JSONObject result) {
//
//            }
//        });
//    }


    void checklogin(final String token) {
        final HashMap<String, String> params = new HashMap<>();
        params.put("check_login", String.valueOf(true));
        params.put("user_email", logPre.getString("user_email", ""));
        params.put("user_pass", logPre.getString("user_pass", ""));
        params.put("new_token", logPre.getString("token", ""));
        params.put("old_token", token);

        CustomVolley.getInsertPOSTData3(context, params, "login/check_login.php", new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {



                try {
                    boolean error = result.getBoolean("error");
                    if (error) {

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

                        MyDialog dialog = new MyDialog(context, "Warning!", result.getString("msg"));
                        dialog.onPositiveClick("OK", new myOnClickListener() {
                            @Override
                            public void onButtonClick(MyDialog dialog) {
                                dialog.dismiss();
                                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dialog.setDialog(MyDialog.DANGER);
                    } else {
                        preEditor.putString("token", token);
                        preEditor.apply();
                        preEditor.commit();

                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 1000);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });
    }


}
