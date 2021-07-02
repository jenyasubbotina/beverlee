package uz.alex.its.beverlee.worker.transaction;

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
import uz.alex.its.beverlee.model.requestParams.TransferFundsParams;
import uz.alex.its.beverlee.model.balance.Balance;
import uz.alex.its.beverlee.model.response.error.LoginErrorModel;
import uz.alex.its.beverlee.model.response.error.TransferErrorModel;
import uz.alex.its.beverlee.utils.Constants;

public class TransferWorker extends Worker {
    private final TransferFundsParams params;

    public TransferWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.params = new TransferFundsParams(
                getInputData().getLong(Constants.RECIPIENT_ID, 0L),
                getInputData().getDouble(Constants.AMOUNT, 0),
                getInputData().getString(Constants.NOTE),
                getInputData().getString(Constants.TRANSACTION_PIN));
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
            final Response<Balance> response = RetrofitClient.getInstance(getApplicationContext()).transferFunds(params);

            if (response.code() == 200 && response.isSuccessful()) {
                final Balance currentBalance = response.body();

                if (currentBalance == null) {
                    Log.w(TAG, "doWork(): balance is NULL");
                    return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "balance is NULL").build());
                }

                return Result.success(outputDataBuilder.putDouble(Constants.CURRENT_BALANCE, currentBalance.getBalance()).build());
            }
            final ResponseBody error = response.errorBody();

            if (error == null) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, Constants.UNKNOWN_ERROR).build());
            }
            if (response.code() == 422) {
                final Type transferErrorType = new TypeToken<TransferErrorModel>() {}.getType();
                final TransferErrorModel transferError = new GsonBuilder().setLenient().create().fromJson(error.string(), transferErrorType);
                return Result.failure(outputDataBuilder
                        .putString(Constants.REQUEST_ERROR, transferError.getTransferError().toString())
                        .build());
            }
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, error.string()).build());
        }
        catch (IOException e) {
            Log.e(TAG, "doWork(): ", e);
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, e.getMessage()).build());
        }
    }

    private static final String TAG = TransferWorker.class.toString();
}
