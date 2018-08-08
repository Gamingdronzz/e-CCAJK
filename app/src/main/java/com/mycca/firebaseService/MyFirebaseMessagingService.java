package com.mycca.firebaseService;

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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mycca.R;
import com.mycca.activity.TrackGrievanceResultActivity;
import com.mycca.notification.Constants;
import com.mycca.tools.CustomLogger;
import com.mycca.tools.FireBaseHelper;
import com.mycca.tools.Preferences;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context context = this;
    String groupKey = "grievanceGroupKey";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //startService(new Intent(getBaseContext(), MyIntentService.class));
        if (remoteMessage.getData().size() > 0) {
            CustomLogger.getInstance().logDebug( "data: " + remoteMessage.getData());
            String title = remoteMessage.getData().get(Constants.KEY_TITLE);
            CustomLogger.getInstance().logDebug( "title: " + title);

            String message = remoteMessage.getData().get(Constants.KEY_BODY);
            if (message != null && Preferences.getInstance().getBooleanPref(getApplicationContext(), Preferences.PREF_RECEIVE_NOTIFICATIONS)) {
                String pensionerCode = null;
                String grievanceType = null;
                if (remoteMessage.getData().containsKey(Constants.KEY_CODE)) {
                    pensionerCode = remoteMessage.getData().get(Constants.KEY_CODE);
                }
                if (remoteMessage.getData().containsKey(Constants.KEY_GTYPE)) {
                    grievanceType = remoteMessage.getData().get(Constants.KEY_GTYPE);
                }
                if (pensionerCode != null && grievanceType != null) {
                    sendUserNotification(title, message, pensionerCode, Long.parseLong(grievanceType));
                }
            }
        }

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        CustomLogger.getInstance().logDebug("new token");
        FireBaseHelper.getInstance(this).addTokenOnFirebaseDatabase();
    }

    private void sendUserNotification(String title, String mess, String pensionerCode, long grievanceType) {

        CustomLogger.getInstance().logDebug( "sendUserNotification: ");
        int notifyID = new Random().nextInt();
        Intent intent;
        NotificationChannel mChannel;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        intent = new Intent(context, TrackGrievanceResultActivity.class);
        intent.putExtra("Code", pensionerCode);
        intent.putExtra("grievanceType", grievanceType);
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
