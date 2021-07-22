package uz.alex.its.beverlee.push;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.repository.PushRepository;
import uz.alex.its.beverlee.utils.Constants;

public class FcmMessagingService extends FirebaseMessagingService {
    private PushRepository pushRepository;
    private NotifyManager notifyManager;
    private TokenReceiver tokenReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        tokenReceiver = new TokenReceiver(getApplicationContext());
        pushRepository = new PushRepository(getApplicationContext());
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

        final Push processedMessage = pushRepository.parsePush(remoteMessage);

        if (processedMessage == null) {
            Log.e(TAG, "onMessageReceived(): processedMessage is NULL");
            return;
        }
        String channelId = null;

        switch (processedMessage.getType()) {
            case Push.TYPE_BONUS:
                channelId = Constants.BONUS_CHANNEL_ID;
                break;
            case Push.TYPE_PURCHASE:
                channelId = Constants.PURCHASE_CHANNEL_ID;
                break;
            case Push.TYPE_REPLENISH:
                channelId = Constants.REPLENISH_CHANNEL_ID;
                break;
            case Push.TYPE_WITHDRAWAL:
                channelId = Constants.WITHDRAWAL_CHANNEL_ID;
                break;
            case Push.TYPE_TRANSFER:
                channelId = Constants.TRANSFER_CHANNEL_ID;
                break;
            case Push.TYPE_NEWS:
                channelId = Constants.NEWS_CHANNEL_ID;
                break;
        }
        notifyManager.showPush(processedMessage, channelId);
        pushRepository.insertPush(processedMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private static final String TAG = FcmMessagingService.class.toString();
}
