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
import uz.alex.its.beverlee.model.requestParams.ChangePasswordParams;
import uz.alex.its.beverlee.utils.Constants;

public class ChangePasswordWorker extends Worker {
    private final Context context;
    private final ChangePasswordParams params;

    public ChangePasswordWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = context;
        this.params = new ChangePasswordParams(
                getInputData().getString(Constants.PASSWORD),
                getInputData().getString(Constants.NEW_PASSWORD),
                getInputData().getString(Constants.NEW_PASSWORD_CONFIRMATION));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<Void> response = RetrofitClient.getInstance(context).changePassword(params);

            if ((response.code() == 201 || response.code() == 200) && response.isSuccessful()) {
                return Result.success(outputDataBuilder.putString(Constants.NEW_PASSWORD, params.getNewPassword()).build());
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

    private static final String TAG = ChangePasswordWorker.class.toString();
}
