package uz.alex.its.beverlee.worker.login;

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
import uz.alex.its.beverlee.model.requestParams.VerifyCodeParams;
import uz.alex.its.beverlee.utils.Constants;

public class CheckCodeValidWorker extends Worker {
    private final Context context;

    private final VerifyCodeParams verifyCodeParams;

    public CheckCodeValidWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
        this.verifyCodeParams = new VerifyCodeParams(getInputData().getString(Constants.CODE));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<Void> response = RetrofitClient.getInstance(context).checkCodeValid(verifyCodeParams);

            if (response.code() == 422) {
                return Result.failure();
            }
            if (response.code() == 200 && response.isSuccessful()) {
                return Result.success(outputDataBuilder.putString(Constants.CODE, verifyCodeParams.getCode()).build());
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

    private static final String TAG = CheckCodeValidWorker.class.toString();
}
