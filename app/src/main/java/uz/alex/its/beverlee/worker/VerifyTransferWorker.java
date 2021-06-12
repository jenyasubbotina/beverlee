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
import uz.alex.its.beverlee.model.requestParams.VerifyTransferParams;
import uz.alex.its.beverlee.utils.Constants;

public class VerifyTransferWorker extends Worker {
    private final VerifyTransferParams params;

    public VerifyTransferWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.params = new VerifyTransferParams(
                getInputData().getLong(Constants.RECIPIENT_ID, 0L),
                getInputData().getDouble(Constants.AMOUNT, 0),
                getInputData().getString(Constants.NOTE));
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
            final Response<Void> response = RetrofitClient.getInstance(getApplicationContext()).verifyTransfer(params);

            if (response.code() == 200 && response.isSuccessful()) {
                return Result.success(outputDataBuilder
                        .putLong(Constants.RECIPIENT_ID, params.getRecipientId())
                        .putDouble(Constants.AMOUNT, params.getAmount())
                        .putString(Constants.NOTE, params.getNote())
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

    private static final String TAG = VerifyTransferWorker.class.toString();
}
