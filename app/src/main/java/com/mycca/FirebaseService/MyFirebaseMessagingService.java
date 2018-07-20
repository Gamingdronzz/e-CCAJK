package com.mycca.FirebaseService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mycca.Activity.TrackGrievanceResultActivity;
import com.mycca.R;
import com.mycca.Tools.Preferences;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    private Context context = this;
    String groupKey = "grievanceGroupKey";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(Preferences.getInstance().getBooleanPref(getApplicationContext(),Preferences.PREF_RECEIVE_NOTIFICATIONS))
        {
            if (remoteMessage.getData().size() > 0) {
                String title = remoteMessage.getData().get("title");
                String message = remoteMessage.getData().get("body");
                String pensionerCode = remoteMessage.getData().get("pensionerCode");
                String grievanceType = remoteMessage.getData().get("grievanceType");
                sendUserNotification(title,message ,pensionerCode, Long.parseLong(grievanceType));
            }
        }
    }

    private void sendUserNotification(String title, String mess, String pensionerCode,long grievanceType) {

        Log.d(TAG, "sendUserNotification: ");
        int notifyID = new Random().nextInt();
        Intent intent;
        NotificationChannel mChannel;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        intent = new Intent(context, TrackGrievanceResultActivity.class);
        intent.putExtra("Code",pensionerCode);
        intent.putExtra("grievanceType",grievanceType);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = context.getPackageName();// The id of the channel.
        CharSequence name = "Sample one";// The user-visible name of the channel.
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setGroup(groupKey);
        notificationBuilder.setGroupSummary(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification_cca);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_cca_new));
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(mess));
        notificationBuilder.setContentText(mess);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        if (notificationManager != null) {
            notificationManager.notify(notifyID /* ID of notification */, notificationBuilder.build());
        }
    }

}
