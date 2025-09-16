package dev.amanraj.caffiend;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationWorker extends Worker {

    private static final String CHANNEL_ID = "caffeine_notifications";
    private static final int NOTIFICATION_ID = 101;
    private static final String PREF_NAME = "CaffeinePrefs";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // User settings
        int limit = Integer.parseInt(prefs.getString("UserLimit", "400"));
        String bedtime = prefs.getString("UserBedtime", "22:00");

        // Current total caffeine
        String rawData = prefs.getString("CaffeineData", "");
        int currentAmount = 0;
        if (!rawData.isEmpty()) {
            String[] entries = rawData.split(";");
            for (String entry : entries) {
                String[] values = entry.split(",");
                if (values.length == 2) {
                    try {
                        currentAmount += Integer.parseInt(values[0].replaceAll("[^0-9]", ""));
                    } catch (Exception ignored) {}
                }
            }
        }

        // Current time
        String now = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Show bedtime reminder if it's bedtime
        if (now.equals(bedtime)) {
            sendNotification(context, "Bedtime Reminder",
                    "It's your set bedtime. Avoid more caffeine now.");
        }

        // Show over-limit warning
        if (currentAmount > limit) {
            sendNotification(context, "Caffeine Limit Exceeded",
                    "You have consumed " + currentAmount + " mg (limit: " + limit + " mg).");
        }

        return Result.success();
    }

    private void sendNotification(Context context, String title, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create channel for Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Caffeine Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // You can replace with your custom icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
