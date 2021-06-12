package uz.alex.its.beverlee.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.io.File;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.model.actor.UserModel.User;
import uz.alex.its.beverlee.model.notification.NotificationSettingsModel;
import uz.alex.its.beverlee.model.notification.NotificationSettingsModel.NotificationSettings;
import uz.alex.its.beverlee.model.actor.UserModel;
import uz.alex.its.beverlee.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    private final MutableLiveData<User> currentUserData;
    private final MutableLiveData<NotificationSettings> notificationSettings;

    private final MutableLiveData<UUID> userDataSafeUUID;
    private final MutableLiveData<UUID> deleteAvatarUUID;
    private final MutableLiveData<UUID> changePasswordUUID;
    private final MutableLiveData<UUID> saveNotificationSettingsUUID;

    public UserViewModel(final Context context) {
        this.userRepository = new UserRepository(context);
        this.currentUserData = new MutableLiveData<>();
        this.notificationSettings = new MutableLiveData<>();

        this.userDataSafeUUID = new MutableLiveData<>();
        this.deleteAvatarUUID = new MutableLiveData<>();
        this.changePasswordUUID = new MutableLiveData<>();
        this.saveNotificationSettingsUUID = new MutableLiveData<>();
    }

    public void fetchUserData() {
        userRepository.getUserData(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final UserModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.w(TAG, "fetchUserData(): empty response from server");
                        return;
                    }
                    final int itemCount = customizableObject.getRecordsTotal();
                    final User userData = customizableObject.getUser();

                    if (userData == null) {
                        Log.w(TAG, "fetchUserData(): userData is NULL");
                        return;
                    }
                    currentUserData.setValue(userData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void saveUserData(final String firstName,
                             final String lastName,
                             final String middleName,
                             final String email,
                             final String city,
                             final String address) {
        userDataSafeUUID.setValue(userRepository.saveUserData(firstName, lastName, middleName, email, city, address));
    }

    public LiveData<User> getUserData() {
        return currentUserData;
    }

    public LiveData<WorkInfo> getSaveUserDataResult(final Context context) {
        return Transformations.switchMap(userDataSafeUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public void uploadAvatarAsync(final File avatarImageFile) {
        userRepository.uploadAvatarAsync(avatarImageFile, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    Log.i(TAG, "onResponse(): success");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public void deleteAvatar() {
        deleteAvatarUUID.setValue(userRepository.deleteAvatar());
    }

    public LiveData<WorkInfo> getDeleteAvatarResult(final Context context) {
        return Transformations.switchMap(deleteAvatarUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public void changePassword(final String oldPwd, final String newPwd, final String newPwdConfirmation) {
        changePasswordUUID.setValue(userRepository.changePassword(oldPwd, newPwd, newPwdConfirmation));
    }

    public LiveData<WorkInfo> getChangePasswordResult(final Context context) {
        return Transformations.switchMap(changePasswordUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public void fetchNotificationSettings() {
        userRepository.fetchNotificationSettings(new Callback<NotificationSettingsModel>() {
            @Override
            public void onResponse(@NonNull Call<NotificationSettingsModel> call, @NonNull Response<NotificationSettingsModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final NotificationSettingsModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.w(TAG, "fetchNotificationSettings(): empty response from server");
                        return;
                    }
                    final int itemCount = customizableObject.getRecordsTotal();
                    final NotificationSettings notificationsSettingsData = customizableObject.getNotificationData();

                    if (notificationsSettingsData == null) {
                        Log.w(TAG, "fetchNotificationSettings(): notificationSettingsData is NULL");
                        return;
                    }
                    notificationSettings.setValue(notificationsSettingsData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotificationSettingsModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public LiveData<NotificationSettings> getNotificationSettings() {
        return notificationSettings;
    }

    public void saveNotificationSettings(
            final boolean news,
            final boolean bonus,
            final boolean income,
            final boolean purchase,
            final boolean replenish,
            final boolean withdrawal) {
        this.saveNotificationSettingsUUID.setValue(userRepository.saveNotificationSettings(news, bonus, income, purchase, replenish, withdrawal));
    }

    public LiveData<WorkInfo> getSaveNotificationSettingsResult(final Context context) {
        return Transformations.switchMap(saveNotificationSettingsUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    private static final String TAG = UserViewModel.class.toString();
}
