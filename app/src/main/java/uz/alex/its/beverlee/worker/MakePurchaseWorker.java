package uz.alex.its.beverlee.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.requestParams.MakePurchaseParams;
import uz.alex.its.beverlee.model.transaction.PurchaseModel;
import uz.alex.its.beverlee.utils.Constants;

public class MakePurchaseWorker extends Worker {
    private final long requestId;
    private final MakePurchaseParams params;

    public MakePurchaseWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.requestId = getInputData().getLong(Constants.REQUEST_ID, 0);
        this.params = new MakePurchaseParams(getInputData().getString(Constants.PINCODE));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        if (requestId <= 0) {
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "requestId is null").build());
        }
        try {
            final Response<PurchaseModel.PurchaseResponse> response = RetrofitClient.getInstance(getApplicationContext()).makePurchase(requestId, params);

            if (response.code() == 201 && response.isSuccessful()) {
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

    private static final String TAG = MakePurchaseWorker.class.toString();
}
