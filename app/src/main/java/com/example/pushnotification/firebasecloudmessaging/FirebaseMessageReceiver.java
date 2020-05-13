package com.example.pushnotification.firebasecloudmessaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.pushnotification.MainActivity;
import com.example.pushnotification.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageReceiver extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
            Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message data if notification exists payload: " + remoteMessage.getNotification());
            Log.d(TAG, "Message data if notification title exists payload: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Message data if notification body exists payload: " + remoteMessage.getNotification().getBody());

        }
        if (remoteMessage.getData().size() > 0) {
            Log.d("", remoteMessage.getData().get("title"));
            sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }
        else if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Implement this method to send token to the app server.
    }

    // This is the custom part of the notification
    private RemoteViews getCustomNotificationDesign(String title, String messageBody) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message_body, messageBody);
        remoteViews.setImageViewResource(R.id.logo, R.drawable.roll_logo);
        return remoteViews;
    }

    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //String channelId = getString(R.string.default_notification_channel_id);
        String channelId = "push_notification_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder customNotification =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.roll_icon_192pt)
                        //.setContentTitle(title)
                        //.setContentText(messageBody)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        //.setCustomContentView(getCustomNotificationDesign(title, messageBody))
                        .setAutoCancel(true)
                        .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000, 1000})
                        .setOnlyAlertOnce(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder = notificationBuilder.setContent(getCustomNotificationDesign(title, messageBody));

        }
        else {
            notificationBuilder = notificationBuilder.setContentTitle(title)
                    .setContentText(messageBody)
                    .setSmallIcon(R.drawable.roll_logo);
        }*/
        customNotification = customNotification.setContent(getCustomNotificationDesign(title, messageBody));
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel push_notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, customNotification.build());
    }
}
