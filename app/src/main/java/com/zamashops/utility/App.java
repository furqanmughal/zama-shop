package com.zamashops.utility;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class App extends Application {

    public static SharedPreferences logPre;
    public static SharedPreferences.Editor preEditor;
    public static String token;

    public static final String message_style_notification = "message_style_notification";





    @Override
    public void onCreate() {
        super.onCreate();


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


        logPre = getSharedPreferences("Login", MODE_PRIVATE);
        preEditor = logPre.edit();

        createNotificationChannels();

    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    message_style_notification,
                    "Message Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This notification is for chating");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

}
