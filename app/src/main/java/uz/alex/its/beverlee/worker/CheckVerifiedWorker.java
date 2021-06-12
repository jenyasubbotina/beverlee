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
import uz.alex.its.beverlee.model.requestParams.AuthParams;
import uz.alex.its.beverlee.utils.Constants;

public class CheckVerifiedWorker extends Worker {
    private final Context context;
//    private final AuthParams authParams;

    public CheckVerifiedWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
//        this.authParams = new AuthParams(getInputData().getString(Constants.PHONE), getInputData().getString(Constants.PASSWORD));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<Void> response = RetrofitClient.getInstance(context).checkVerified();

            if (response.code() == 200 && response.isSuccessful()) {
                return Result.success(outputDataBuilder
//                        .putString(Constants.PHONE, authParams.getPhone())
//                        .putString(Constants.PASSWORD, authParams.getPassword())
                        .putBoolean(Constants.PHONE_VERIFIED, true)
                        .build());
            }
            if (response.code() == 403) {
                return Result.success(outputDataBuilder
//                        .putString(Constants.PHONE, authParams.getPhone())
//                        .putString(Constants.PASSWORD, authParams.getPassword())
                        .putBoolean(Constants.PHONE_VERIFIED, false)
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

    private static final String TAG = CheckVerifiedWorker.class.toString();
}
