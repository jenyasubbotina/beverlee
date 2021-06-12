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
import uz.alex.its.beverlee.model.requestParams.PinParams;
import uz.alex.its.beverlee.utils.Constants;

public class AssignPinWorker extends Worker {
    private final Context context;

    private final PinParams pinParams;

    public AssignPinWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
        this.pinParams = new PinParams(getInputData().getString(Constants.PINCODE));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        try {
            final Response<Void> response = RetrofitClient.getInstance(context).assignPin(pinParams);

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

    private static final String TAG = AssignPinWorker.class.toString();
}
