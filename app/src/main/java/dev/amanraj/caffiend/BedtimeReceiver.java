package dev.amanraj.caffiend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class BedtimeReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "caffeine_bedtime_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create notification channel (Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Caffeine Bedtime Alerts";
            String description = "Reminds user to stop caffeine intake before bedtime";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build bedtime notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // you can replace with a coffee cup icon
                .setContentTitle("☕ Bedtime Reminder")
                .setContentText("It's bedtime! Avoid more caffeine now for better sleep. 🌙")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(2001, builder.build());
        }
    }
}
