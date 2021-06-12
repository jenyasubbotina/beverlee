package uz.alex.its.beverlee.repository;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OverwritingInputMerger;
import androidx.work.WorkManager;

import java.util.UUID;

import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.worker.CheckPinAssignedWorker;
import uz.alex.its.beverlee.worker.CheckVerifiedWorker;
import uz.alex.its.beverlee.worker.LoginWorker;
import uz.alex.its.beverlee.worker.RegisterWorker;
import uz.alex.its.beverlee.worker.SubmitVerificationWorker;
import uz.alex.its.beverlee.worker.VerifyPhoneByCallWorker;
import uz.alex.its.beverlee.worker.VerifyPhoneBySmsWorker;

public class AuthRepository {
    private final Context context;

    public AuthRepository(final Context context) {
        this.context = context;
    }

    public UUID login(final String phone, final String password) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putString(Constants.PHONE, phone)
                .putString(Constants.PASSWORD, password)
                .build();
        final OneTimeWorkRequest loginRequest = new OneTimeWorkRequest.Builder(LoginWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(loginRequest);
        return loginRequest.getId();
    }

    public void verifyPhoneBySms() {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final OneTimeWorkRequest verifyPhoneBySmsRequest = new OneTimeWorkRequest.Builder(VerifyPhoneBySmsWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(verifyPhoneBySmsRequest);
    }

    public void verifyPhoneByCall() {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final OneTimeWorkRequest verifyPhoneByCallRequest = new OneTimeWorkRequest.Builder(VerifyPhoneByCallWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(verifyPhoneByCallRequest);
    }

//    public UUID checkVerified() {
//        final Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresDeviceIdle(false)
//                .setRequiresStorageNotLow(false)
//                .setRequiresCharging(false)
//                .setRequiresBatteryNotLow(false)
//                .build();
//        final OneTimeWorkRequest checkVerifiedRequest = new OneTimeWorkRequest.Builder(CheckVerifiedWorker.class)
//                .setConstraints(constraints)
//                .build();
//        WorkManager.getInstance(context).enqueue(checkVerifiedRequest);
//        return checkVerifiedRequest.getId();
//    }

    public UUID submitVerification(final String code) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putString(Constants.CODE, code)
                .build();
        final OneTimeWorkRequest submitVerificationRequest = new OneTimeWorkRequest.Builder(SubmitVerificationWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(submitVerificationRequest);
        return submitVerificationRequest.getId();
    }

    public UUID register(final String firstName,
                         final String lastName,
                         final String phone,
                         final String email,
                         final long countryId,
                         final String city,
                         final String password,
                         final String passwordConfirmation) {
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
                .putString(Constants.PHONE, phone)
                .putString(Constants.EMAIL, email)
                .putLong(Constants.COUNTRY_ID, countryId)
                .putString(Constants.CITY, city)
                .putString(Constants.PASSWORD, password)
                .putString(Constants.PASSWORD_CONFIRMATION, passwordConfirmation)
                .build();
        final OneTimeWorkRequest registerRequest = new OneTimeWorkRequest.Builder(RegisterWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(registerRequest);
        return registerRequest.getId();
    }

    private static final String TAG = AuthRepository.class.toString();
}
