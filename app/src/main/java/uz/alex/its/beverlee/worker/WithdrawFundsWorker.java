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
import uz.alex.its.beverlee.model.requestParams.WithdrawalParams;
import uz.alex.its.beverlee.utils.Constants;

public class WithdrawFundsWorker extends Worker {
    private final WithdrawalParams params;

    public WithdrawFundsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.params = new WithdrawalParams(
                getInputData().getString(Constants.WITHDRAW_TYPE),
                getInputData().getString(Constants.WITHDRAW_METHOD),
                getInputData().getDouble(Constants.AMOUNT, 0),
                getInputData().getString(Constants.CARD_NUMBER),
                getInputData().getString(Constants.FULL_NAME),
                getInputData().getString(Constants.PHONE),
                getInputData().getString(Constants.COUNTRY_TITLE),
                getInputData().getString(Constants.CITY));
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
            final Response<Void> response = RetrofitClient.getInstance(getApplicationContext()).withdrawFunds(params);

            if ((response.code() == 200 || response.code() == 201) && response.isSuccessful()) {
                return Result.success();
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

    private static final String TAG = WithdrawFundsWorker.class.toString();
}
