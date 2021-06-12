package uz.alex.its.beverlee.push;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.messaging.FirebaseMessaging;

import uz.alex.its.beverlee.utils.Constants;

public class FcmTokenReceiver extends ListenableWorker {
    public FcmTokenReceiver(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(completer -> {
            final OnCompleteListener<String> onCompleteListener = task -> {
                if (task.isSuccessful()) {
                    if (task.getResult() == null) {
                        Log.e(TAG, "startWork(): obtaining FCM token returned null result");
                        completer.setException(new NullPointerException("obtaining FCM token returned null result"));
                    }
                    else {
                        final String token = task.getResult();

                        final Data outputData = new Data.Builder()
                                .putString(Constants.FCM_TOKEN, token)
                                .build();
                        completer.set(Result.success(outputData));
                    }
                }
                if (task.isCanceled()) {
                    completer.setCancelled();
                }
            };
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(onCompleteListener);
            return onCompleteListener;
        });
    }

    private static final String TAG = FcmTokenReceiver.class.toString();
}