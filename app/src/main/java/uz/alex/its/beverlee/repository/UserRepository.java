package uz.alex.its.beverlee.repository;

import android.content.Context;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.util.UUID;

import retrofit2.Callback;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.requestParams.AvatarParams;
import uz.alex.its.beverlee.model.notification.NotificationSettingsModel;
import uz.alex.its.beverlee.model.actor.UserModel;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.worker.ChangePasswordWorker;
import uz.alex.its.beverlee.worker.DeleteAvatarWorker;
import uz.alex.its.beverlee.worker.SaveNotificationSettingsWorker;
import uz.alex.its.beverlee.worker.SaveUserDataWorker;
import uz.alex.its.beverlee.worker.UploadAvatarWorker;

public class UserRepository {
    private final Context context;

    public UserRepository(final Context context) {
        this.context = context;
    }

    public void getUserData(final Callback<UserModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getUserData(callback);
    }

    public UUID saveUserData(final String firstName,
                             final String lastName,
                             final String patronymic,
                             final String email,
                             final String city,
                             final String address) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putString(Constants.FIRST_NAME, firstName)
                .putString(Constants.LAST_NAME, lastName)
                .putString(Constants.MIDDLE_NAME, patronymic)
                .putString(Constants.EMAIL, email)
                .putString(Constants.CITY, city)
                .putString(Constants.ADDRESS, address)
                .build();
        final OneTimeWorkRequest saveUserDataRequest = new OneTimeWorkRequest.Builder(SaveUserDataWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(saveUserDataRequest);
        return saveUserDataRequest.getId();
    }

    public UUID uploadAvatar(final String encodedBitmap) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putString(Constants.PHOTO_URL, encodedBitmap)
                .build();
        final OneTimeWorkRequest uploadAvatarRequest = new OneTimeWorkRequest.Builder(UploadAvatarWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(uploadAvatarRequest);
        return uploadAvatarRequest.getId();
    }

    public void uploadAvatarAsync(final File avatarImageFile, final Callback<Void> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).uploadAvatarAsync(new AvatarParams(avatarImageFile), callback);
    }

    public UUID deleteAvatar() {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final OneTimeWorkRequest deleteAvatarRequest = new OneTimeWorkRequest.Builder(DeleteAvatarWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(deleteAvatarRequest);
        return deleteAvatarRequest.getId();
    }

    public UUID changePassword(final String pwd, final String newPwd, final String newPwdConfirm) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putString(Constants.PASSWORD, pwd)
                .putString(Constants.NEW_PASSWORD, newPwd)
                .putString(Constants.NEW_PASSWORD_CONFIRMATION, newPwdConfirm)
                .build();
        final OneTimeWorkRequest changePasswordWorker = new OneTimeWorkRequest.Builder(ChangePasswordWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(changePasswordWorker);
        return changePasswordWorker.getId();
    }

    public void fetchNotificationSettings(final Callback<NotificationSettingsModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getNotificationSettings(callback);
    }

    public UUID saveNotificationSettings(
            final boolean news,
            final boolean bonus,
            final boolean income,
            final boolean purchase,
            final boolean replenish,
            final boolean withdrawal) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putBoolean(Constants.NOTIFY_NEWS, news)
                .putBoolean(Constants.NOTIFY_BONUSES, bonus)
                .putBoolean(Constants.NOTIFY_INCOME, income)
                .putBoolean(Constants.NOTIFY_PURCHASE, purchase)
                .putBoolean(Constants.NOTIFY_REPLENISH, replenish)
                .putBoolean(Constants.NOTIFY_WITHDRAWAL, withdrawal)
                .build();
        final OneTimeWorkRequest changePasswordWorker = new OneTimeWorkRequest.Builder(SaveNotificationSettingsWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(changePasswordWorker);
        return changePasswordWorker.getId();
    }
}
