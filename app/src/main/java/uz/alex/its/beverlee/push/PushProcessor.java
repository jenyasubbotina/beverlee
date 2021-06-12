package uz.alex.its.beverlee.push;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.utils.Constants;

public class PushProcessor {
    private final Context context;

    public PushProcessor(final Context context) {
        this.context = context;
    }

    public Push parsePush(@NonNull final RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();

        if (data.isEmpty()) {
            Log.e(TAG, "processMessage(): dataPayload is NULL");
            return null;
        }
        final String id = remoteMessage.getMessageId();
        final String title = data.get(Constants.PUSH_TITLE);
        final String body = data.get(Constants.PUSH_BODY);

        if (id == null) {
            Log.e(TAG, "processMessage(): message with empty ID");
            return null;
        }
        final Push push = new Push(id);
        push.setTitle(title);
        push.setBody(body);
        push.setStatus(Constants.NOT_DELIVERED);
        push.setReceiptDate(new Date());
        return push;
    }

    private boolean isPushDuplicate(final Push push) throws ExecutionException, InterruptedException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<Integer> callable = () -> {
            final int notificationId = (int) LocalDatabase.getInstance(context).pushDao().insertPush(push);
            push.setNotificationId(notificationId);
            return notificationId;
        };
        final Future<Integer> notificationId = executorService.submit(callable);
        executorService.shutdown();
        return notificationId.get() < 1;
    }

    public void processPush(@Nullable final Push push) throws ExecutionException, InterruptedException {
        if (push == null) {
            Log.e(TAG, "processPush(): push is NULL");
            return;
        }
        if (isPushDuplicate(push)) {
            updatePushStatus(push.getId(), Constants.DELIVERED);
        }
    }

    public void updatePushStatus(final String pushId, final int status) {
        if (pushId == null) {
            Log.e(TAG, "updateStatus(): pushId is NULL");
            return;
        }
        final Constraints.Builder constraintsBuilder = new Constraints.Builder();
        constraintsBuilder.setRequiredNetworkType(NetworkType.CONNECTED);
        constraintsBuilder.setRequiresBatteryNotLow(false);
        constraintsBuilder.setRequiresCharging(false);
        constraintsBuilder.setRequiresStorageNotLow(false);
        constraintsBuilder.setRequiresDeviceIdle(false);

        final Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putString(Constants.PUSH_ID, pushId);
        dataBuilder.putInt(Constants.PUSH_STATUS, status);

        WorkManager.getInstance(context).enqueue(new OneTimeWorkRequest.Builder(SendStatusWorker.class)
                .setConstraints(constraintsBuilder.build())
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5*1000L, TimeUnit.MILLISECONDS)
                .setInputData(dataBuilder.build())
                .build());
    }

    private static final String TAG = PushProcessor.class.toString();
}
