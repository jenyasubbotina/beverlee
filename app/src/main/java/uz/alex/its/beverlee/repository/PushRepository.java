package uz.alex.its.beverlee.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Query;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.storage.dao.PushDao;

public class PushRepository {
    private final Context context;
    private final PushDao pushDao;

    public PushRepository(final Context context) {
        this.context = context;
        this.pushDao = LocalDatabase.getInstance(context).pushDao();
    }

    public LiveData<List<Push>> selectNewPushList() {
        return pushDao.selectNewPushList();
    }

    public LiveData<Integer> getNotificationCount() {
        return pushDao.getNotificationCount();
    }

    public LiveData<List<Push>> selectAllPushList() {
        return pushDao.selectAllPushList();
    }

    public void insertPush(final Push processedMessage) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<Long> callable = () -> LocalDatabase.getInstance(context).pushDao().insertPush(processedMessage);
        executorService.submit(callable);
        executorService.shutdown();
    }

    public void deletePush(final Push push) {
        new Thread(() -> pushDao.deletePush(push)).start();
    }

    public int updateStatusRead(final long notificationId) throws ExecutionException, InterruptedException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<Integer> callable = () -> LocalDatabase.getInstance(context).pushDao().updateStatusRead(notificationId);
        final int result = executorService.submit(callable).get();
        executorService.shutdown();
        return result;
    }

    public Push parsePush(@NonNull final RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        Log.i(TAG, "messageId=" + remoteMessage.getMessageId());
        Log.i(TAG, "dataPayload=" + data);

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "notificationCount=" + remoteMessage.getNotification().getNotificationCount());
            Log.i(TAG, "notificationBody=" + remoteMessage.getNotification().getBody());
            Log.i(TAG, "notificationBodyLocalizationKey=" + remoteMessage.getNotification().getBodyLocalizationKey());
            Log.i(TAG, "notificationChannelId=" + remoteMessage.getNotification().getChannelId());
            Log.i(TAG, "notificationClickAction=" + remoteMessage.getNotification().getClickAction());
            Log.i(TAG, "notificationColor=" + remoteMessage.getNotification().getColor());
            Log.i(TAG, "notificationIcon=" + remoteMessage.getNotification().getIcon());
            Log.i(TAG, "notificationSound=" + remoteMessage.getNotification().getSound());
            Log.i(TAG, "notificationTag=" + remoteMessage.getNotification().getTag());
            Log.i(TAG, "notificationTicker=" + remoteMessage.getNotification().getTicker());
            Log.i(TAG, "notificationTitle=" + remoteMessage.getNotification().getTitle());
            Log.i(TAG, "notificationLink=" + remoteMessage.getNotification().getLink());
            Log.i(TAG, "notificationImageUrl=" + remoteMessage.getNotification().getImageUrl());
            Log.i(TAG, "notificationPriority=" + remoteMessage.getNotification().getNotificationPriority());
        }

        /**
         bonus бонус
         buy Покупка
         refill Пополнение баланса
         transfer Перевод средств
         withdrawal Вывод средств
         news Новая новость
         **/

        if (data.isEmpty() || remoteMessage.getMessageId() == null) {
            Log.e(TAG, "processMessage(): dataPayload is NULL or messageId is NULL");
            return null;
        }
        final long notificationId = remoteMessage.getMessageId().hashCode();
        final String newsIdStr = data.get(Push.NEWS_ID);
        final String timestampStr = data.get(Push.TIMESTAMP);

        return new Push(notificationId,
                data.get(Push.TITLE),
                data.get(Push.BODY),
                data.get(Push.TYPE),
                timestampStr == null || TextUtils.isEmpty(timestampStr) ? new Date().getTime() : Long.parseLong(timestampStr),
                0,
                newsIdStr == null || TextUtils.isEmpty(newsIdStr) ? 0L : Long.parseLong(newsIdStr));
    }

    private static final String TAG = PushRepository.class.toString();
}
