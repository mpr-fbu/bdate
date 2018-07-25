package io.github.rgdagir.mpr;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

public class CustomPushReceiver extends BroadcastReceiver {
    private static final String TAG = "CustomPushReceiver";
    public static final String intentAction = "com.parse.push.intent.RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(intentAction)) {
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.d(TAG, "got action " + action + " with:");
                // Iterate the parse keys if needed
                Iterator<String> itr = json.keys();
                while (itr.hasNext()) {
                    String key = itr.next();
                    String body = json.getString(key);
                    Log.d(TAG, "..." + key + " => " + body);
                    // Check if user is already in chat activity before pushing
                    SharedPreferences sp = context.getSharedPreferences("ACTIVEINFO", MODE_PRIVATE);
                    Boolean chatActive = sp.getBoolean("active", false);
                    // Extract custom push data
                    if (key.equals("mydata") && !chatActive) {
                        // create a local notification
                        Log.d(TAG, "mydata was sent");
                        createNotification(context, R.drawable.ic_stat_ic_notification, "fbu-bdate", body);
                    }
                }
            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }
    }

    public static final int NOTIFICATION_ID = 45;
    public static final String CHANNEL_ID = "my_channel_01";
    // Create a local dashboard notification to tell user about the event
    private void createNotification(Context context, int iconRes, String title, String body) {
        // Create an explicit intent for MainActivity of app
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(body)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "General Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
