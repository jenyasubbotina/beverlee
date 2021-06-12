package uz.alex.its.beverlee.push;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.concurrent.ExecutionException;

import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.utils.Constants;

public class FcmMessagingService extends FirebaseMessagingService {
    private String intentClass;

    private PushProcessor pushProcessor;
    private NotifyManager notifyManager;
    private TokenReceiver tokenReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        Bundle serviceBundle = null;

        try {
            ComponentName serviceComponent = new ComponentName(this, this.getClass());
            final PackageManager packageManager = getApplicationContext().getPackageManager();

            if (packageManager != null) {
                final ServiceInfo serviceInfo = packageManager.getServiceInfo(serviceComponent, PackageManager.GET_META_DATA);
                serviceBundle = serviceInfo.metaData;
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "onCreate: ", e);
        }
        if (serviceBundle != null) {
            this.intentClass = serviceBundle.getString(Constants.PUSH_INTENT, null);
        }

        tokenReceiver = new TokenReceiver(getApplicationContext());
        pushProcessor = new PushProcessor(getApplicationContext());
        notifyManager = new NotifyManager(getApplicationContext());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        tokenReceiver.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final Push processedMessage = pushProcessor.parsePush(remoteMessage);

        if (processedMessage == null) {
            Log.e(TAG, "onMessageReceived(): processedMessage is NULL");
            return;
        }
        try {
            pushProcessor.processPush(processedMessage);
        }
        catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "onMessageReceived(): ", e);
        }

        notifyManager.showPush(getPackageName(),
                intentClass,
                Constants.PUSH,
                remoteMessage,
                processedMessage.getNotificationId(),
                String.valueOf(processedMessage.getChannelId() > 0 ? processedMessage.getChannelId() : Constants.DEFAULT_CHANNEL_ID));

        pushProcessor.updatePushStatus(processedMessage.getId(), Constants.DELIVERED);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private static final String TAG = FcmMessagingService.class.toString();
}
