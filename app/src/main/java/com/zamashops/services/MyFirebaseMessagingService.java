package com.zamashops.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zamashops.R;
import com.zamashops.activities.ChatRoomActivity;
import com.zamashops.activities.SellerProfileActivity;
import com.zamashops.interfaces.VolleyCallback;
import com.zamashops.models.MessageModel;
import com.zamashops.utility.CustomVolley;
import com.zamashops.utility.MyNotification;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zamashops.utility.App.logPre;
import static com.zamashops.utility.App.message_style_notification;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManagerCompat notificationManager;
    private MediaSessionCompat mediaSession;

    static ArrayList<MessageModel> messageModels = new ArrayList<>();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
//Notification//
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
//        mediaSession = new MediaSessionCompat(getApplicationContext(), "tag");


        messageModels.clear();

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("TAG : ", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("TAG : ", "Message data payload: " + remoteMessage.getData());

            Map<String, String> data = remoteMessage.getData();

            String type = data.get("type");

            if(type.equals("message")) {
                String token = data.get("token");
                String user_id_sender = data.get("user_id_sender");
                String user_id_reciver = data.get("user_id_reciver");
                String product_id = data.get("product_id");
                String message = data.get("message");
                String time = data.get("time");
                String user_name = data.get("user_name");
                String chat_id = data.get("chat_id");


                if (chat_id == null) {
                    chat_id = "1000";
                }else if(chat_id.equals("")){
                    chat_id = "1000";
                }


                messageModels.add(new MessageModel("", message, time, time, user_id_sender, user_name, "", ""));

                Intent activityIntent = new Intent(this, ChatRoomActivity.class).
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activityIntent.putExtra("chat_id", product_id);
                activityIntent.putExtra("user_id_1", user_id_sender);
                activityIntent.putExtra("user_id_2", user_id_reciver);
                activityIntent.putExtra("product_id", product_id);
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                activityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );


                if (logPre.getBoolean("login_stauts", false)) {
                    if (logPre.getString("user_id", "").equals(user_id_reciver) || logPre.getString("user_id_2", "").equals(user_id_reciver)) {
                        sendChannel1Notification(getApplicationContext(), resultPendingIntent, chat_id);
                    }
                }


                Intent intent = new Intent("chatroom");
                sendLocationBroadcast(intent);
                Intent intent2 = new Intent("allchat");
                sendLocationBroadcast(intent2);

            }else if(type.equals("follow")){

                String user_id = data.get("user_id");
                String follow_id = data.get("follow_id");
                String time = data.get("time");
                String user_name = data.get("user_name");

                int notification_id = (int) System.currentTimeMillis();
                if (logPre.getBoolean("login_stauts", false)) {
                    if (logPre.getString("user_id", "").equals(user_id)) {
                        Intent notification_intent = new Intent(getApplicationContext(), SellerProfileActivity.class);
                        notification_intent.putExtra("user_id",follow_id);
                        notification_intent.putExtra("notification_id",notification_id);
                        MyNotification.showNotification(getApplicationContext(),user_name +" Follow You",
                                "Click Here to View the follower Details", notification_id,notification_intent);
                    }
                }



            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
              //  scheduleJob();
            } else {
                // Handle message within 10 seconds
              //  handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("TAG : ", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    private void sendLocationBroadcast(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static void sendChannel1Notification(Context context,PendingIntent intent,String chat_id) {


        NotificationCompat.MessagingStyle messagingStyle =
                new NotificationCompat.MessagingStyle("Me");
        messagingStyle.setConversationTitle("Zama Shops");

        for (MessageModel chatMessage : messageModels) {
            NotificationCompat.MessagingStyle.Message notificationMessage =
                    new NotificationCompat.MessagingStyle.Message(
                            chatMessage.getMessage(),
                           Integer.parseInt(chatMessage.getTime()),
                            chatMessage.getUser_name()
                    );
            messagingStyle.addMessage(notificationMessage);
        }

        int color = ContextCompat.getColor(context, R.color.colorPrimaryDark);

        Notification notification = new NotificationCompat.Builder(context, message_style_notification)
                .setSmallIcon(MyNotification.getNotificationIcon())
                .setStyle(messagingStyle)
                .setColor(color)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (!TextUtils.isEmpty(chat_id) && TextUtils.isDigitsOnly(chat_id)) {
            notificationManager.notify(Integer.parseInt(chat_id), notification);
        } else {
            notificationManager.notify(89375, notification);
        }


    }




    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            componentInfo = runningTaskInfo.get(0).topActivity;
        }
        return componentInfo.getPackageName().equals(myPackage);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d("TAG : ", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    void notification(){

    }


    void sendRegistrationToServer(String token){


        if (logPre.getBoolean("login_stauts", false)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", logPre.getString("user_id",""));
            params.put("token", token);

            CustomVolley.getInsertPOSTData2(getApplicationContext(), params, "updatetoken.php", new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                }
            });

        }

    }




}
