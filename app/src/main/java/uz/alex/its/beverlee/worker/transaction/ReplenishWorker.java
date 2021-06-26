package uz.alex.its.beverlee.worker.transaction;

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
import uz.alex.its.beverlee.model.requestParams.ReplenishParams;
import uz.alex.its.beverlee.model.transaction.ReplenishModel;
import uz.alex.its.beverlee.utils.Constants;

public class ReplenishWorker extends Worker {
    private final ReplenishParams params;

    public ReplenishWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.params = new ReplenishParams(getInputData().getString(Constants.AMOUNT));
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
            final Response<ReplenishModel> response = RetrofitClient.getInstance(getApplicationContext()).replenish(params);

            if (response.code() == 200 && response.isSuccessful()) {
                final ReplenishModel replenishModel = response.body();

                if (replenishModel == null) {
                    Log.w(TAG, "doWork(): response is NULL");
                    return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "response is NULL").build());
                }
                return Result.success(outputDataBuilder.putString(Constants.REPLENISH_URL, replenishModel.getReplenishLink().getUrl()).build());
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

    private static final String TAG = ReplenishWorker.class.toString();
}
