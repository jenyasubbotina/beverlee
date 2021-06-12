package uz.alex.its.beverlee.worker;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.utils.Constants;

public class CheckPinAssignedWorker extends Worker {
    private final Context context;

    public CheckPinAssignedWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        try {
            RetrofitClient.getInstance(context).setAuthorizationHeader(context);
            final Response<Void> response = RetrofitClient.getInstance(context).checkPinAssigned();

            if (response.code() == 404) {
                return Result.success(outputDataBuilder.putBoolean(Constants.PIN_ASSIGNED, false).build());
            }
            if (response.code() == 200 && response.isSuccessful()) {
                return Result.success(outputDataBuilder.putBoolean(Constants.PIN_ASSIGNED, true).build());
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

    private static final String TAG = CheckPinAssignedWorker.class.toString();
}
