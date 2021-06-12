package uz.alex.its.beverlee.worker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Response;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.LoginModel;
import uz.alex.its.beverlee.model.requestParams.AuthParams;
import uz.alex.its.beverlee.model.response.error.LoginErrorModel;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;

public class LoginWorker extends Worker {
    private final Context context;
    private final AuthParams authParams;

    public LoginWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
        this.authParams = new AuthParams(getInputData().getString(Constants.PHONE), getInputData().getString(Constants.PASSWORD));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<LoginModel> response = RetrofitClient.getInstance(context).login(authParams);

            if (response.code() == 200 && response.isSuccessful()) {
                final LoginModel bearerToken = response.body();

                if (bearerToken == null) {
                    return Result.failure();
                }
                SharedPrefs.getInstance(context).putString(Constants.BEARER_TOKEN, bearerToken.getToken());
                Log.i(TAG, "bearerToken=" + bearerToken.getToken());
                return Result.success(outputDataBuilder
                        .putString(Constants.PHONE, authParams.getPhone())
                        .putString(Constants.PASSWORD, authParams.getPassword())
                        .putBoolean(Constants.PHONE_VERIFIED, true)
                        .build());
            }
            final ResponseBody error = response.errorBody();

            if (error == null) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, Constants.UNKNOWN_ERROR).build());
            }
            if (response.code() == 422) {
                final Type loginErrorType = new TypeToken<LoginErrorModel>() {}.getType();
                final LoginErrorModel phoneNotVerified = new GsonBuilder().setLenient().create().fromJson(error.string(), loginErrorType);

                if (phoneNotVerified.getLoginError().getPhone().equals(context.getString(R.string.error_phone_not_verified)))
                return Result.failure(outputDataBuilder
                        .putString(Constants.REQUEST_ERROR, context.getString(R.string.error_phone_not_verified))
                        .build());
            }
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, error.string()).build());
        }
        catch (IOException e) {
            Log.e(TAG, "doWork(): ", e);
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, e.getMessage()).build());
        }
    }

    private static final String TAG = LoginWorker.class.toString();
}
