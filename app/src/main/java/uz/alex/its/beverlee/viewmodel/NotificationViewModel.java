package uz.alex.its.beverlee.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.repository.PushRepository;
import uz.alex.its.beverlee.storage.LocalDatabase;

public class NotificationViewModel extends ViewModel {
    private final PushRepository notificationRepository;

    public NotificationViewModel(final Context context) {
        this.notificationRepository = new PushRepository(context);
    }

    public LiveData<Integer> getNotificationCount() {
        return notificationRepository.getNotificationCount();
    }

    public LiveData<List<Push>> selectNewPushList() {
        return notificationRepository.selectNewPushList();
    }
    public LiveData<List<Push>> selectAllPushList() {
        return notificationRepository.selectAllPushList();
    }

    public void insertPush(final Push processedMessage) {
        notificationRepository.insertPush(processedMessage);
    }

    public void deletePush(final Push push) {
        new Thread(() -> notificationRepository.deletePush(push)).start();
    }

    public void updateStatusRead(final long notificationId) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<Integer> callable = () -> notificationRepository.updateStatusRead(notificationId);
        executorService.submit(callable);
        executorService.shutdown();
    }
}
