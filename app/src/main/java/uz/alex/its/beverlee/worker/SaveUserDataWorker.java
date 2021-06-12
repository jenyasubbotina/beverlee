package uz.alex.its.beverlee.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.actor.UserModel.User;
import uz.alex.its.beverlee.model.requestParams.UserDataParams;
import uz.alex.its.beverlee.model.actor.UserModel;
import uz.alex.its.beverlee.utils.Constants;

public class SaveUserDataWorker extends Worker {
    private final Context context;

    private final UserDataParams params;

    public SaveUserDataWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
        this.params = new UserDataParams(
                getInputData().getString(Constants.FIRST_NAME),
                getInputData().getString(Constants.LAST_NAME),
                getInputData().getString(Constants.MIDDLE_NAME),
                getInputData().getString(Constants.EMAIL),
                getInputData().getString(Constants.CITY),
                getInputData().getString(Constants.ADDRESS));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        if (params == null) {
            Log.e(TAG, "doWork(): empty input data");
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "empty input data").build());
        }

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<UserModel> response = RetrofitClient.getInstance(context).saveUserData(params);

            if (response.code() == 200 && response.isSuccessful()) {
                final UserModel customizableObject = response.body();

                if (customizableObject == null) {
                    Log.e(TAG, "doWork(): empty response from server");
                    return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "empty response from server").build());
                }
                final User userData = customizableObject.getUser();

                if (userData == null) {
                    Log.e(TAG, "doWork(): userData is NULL");
                    return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "userData is NULL").build());
                }
                return Result.success(outputDataBuilder
                        .putLong(Constants.USER_ID, userData.getId())
                        .putLong(Constants.CLUB_NUMBER, userData.getClubNumber())
                        .putString(Constants.FIRST_NAME, userData.getFirstName())
                        .putString(Constants.LAST_NAME, userData.getLastName())
                        .putString(Constants.MIDDLE_NAME, userData.getMiddleName())
                        .putString(Constants.PHONE, userData.getPhone())
                        .putString(Constants.EMAIL, userData.getEmail())
                        .putLong(Constants.COUNTRY_ID, userData.getCountry() != null ? userData.getCountry().getId() : 0L)
                        .putString(Constants.COUNTRY_TITLE, userData.getCountry() != null ? userData.getCountry().getTitle() : null)
                        .putString(Constants.COUNTRY_CODE, userData.getCountry() != null ? userData.getCountry().getCode() : null)
                        .putString(Constants.CITY, userData.getCity())
                        .putString(Constants.POSITION, userData.getPosition())
                        .putString(Constants.ADDRESS, userData.getAddress())
                        .putString(Constants.PHOTO_URL, userData.getPhotoUrl())
                        .build());
            }
            final ResponseBody error = response.errorBody();

            if (error == null) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, Constants.UNKNOWN_ERROR).build());
            }
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, error.string()).build());
        }
        catch (IOException e) {
            Log.e(TAG, "doWork(): ", e);
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, e.getMessage()).build());
        }
    }

    private static final String TAG = SaveUserDataWorker.class.toString();
}
