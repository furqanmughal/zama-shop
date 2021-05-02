package com.zamashops.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.zamashops.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyNotification {


    public static  void showNotification(Context context, String title, String message, int notify_id, Intent intent){




        int color = ContextCompat.getColor(context, R.color.colorPrimaryDark);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setContentText(message)
                .setColor(color)
                ;

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId2 = "2";
            String channelName2 = "NormalNotification";
            NotificationChannel channel = new NotificationChannel(channelId2, channelName2, NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.enableVibration(true);
            mBuilder.setChannelId(channelId2);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        } else {
            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        }

        if (notificationManager != null) {
            notificationManager.notify(notify_id, mBuilder.build());
        }


    }

    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_stat_name : R.mipmap.ic_launcher;
    }

}
