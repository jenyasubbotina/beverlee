package uz.alex.its.beverlee.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.activities.MainActivity;

public class NotifyManager {
    private final Context context;
    private final NotificationManager manager;

    public NotifyManager(final Context context) {
        this.context = context;
        this.manager = context.getSystemService(NotificationManager.class);
    }

    public void createNotificationChannel(final String channelId, final String channelName) {
        final NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.setShowBadge(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        if (manager == null) {
            Log.e(TAG, "notificationManager is NULL");
            return;
        }
        manager.createNotificationChannel(notificationChannel);
    }

    public void showPush(final Push push, final String channelId) {
        if (push == null) {
            return;
        }
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);

        final Intent notifyIntent = new Intent();
        final String cls = context.getPackageName() + MainActivity.class.getName();
        notifyIntent.setComponent(new ComponentName(context.getPackageName(), cls));
        notifyIntent.putExtra(Push.NOTIFICATION_ID, push.getNotificationId());
        notifyIntent.putExtra(Push.TYPE, push.getType());
        notifyIntent.putExtra(Push.NEWS_ID, push.getNewsId());

        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(notifyIntent);

        final PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        notificationBuilder.setContentTitle(push.getTitle());
        notificationBuilder.setContentText(push.getBody());
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_push);
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManagerCompat.from(context).notify((int) push.getNotificationId(), notificationBuilder.build());
    }

    private static final String TAG = NotifyManager.class.toString();
}
