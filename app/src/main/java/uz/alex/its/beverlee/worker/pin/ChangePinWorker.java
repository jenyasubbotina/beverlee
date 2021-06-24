package uz.alex.its.beverlee.worker.pin;

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
import uz.alex.its.beverlee.model.requestParams.ChangePinParams;
import uz.alex.its.beverlee.utils.Constants;

public class ChangePinWorker extends Worker {
    private final ChangePinParams params;

    public ChangePinWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.params = new ChangePinParams(
                getInputData().getString(Constants.OLD_PIN),
                getInputData().getString(Constants.NEW_PIN),
                getInputData().getString(Constants.NEW_PIN_CONFIRMATION));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        if (params.getNewPin() == null || params.getNewPinConfirmation() == null) {
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "новый pin код пуст").build());
        }
        if (!params.getNewPin().equals(params.getNewPinConfirmation())) {
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "pin коды не совпадают").build());
        }
        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<Void> response = RetrofitClient.getInstance(getApplicationContext()).changePin(params);

            if ((response.code() == 201 || response.code() == 200) && response.isSuccessful()) {
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

    private static final String TAG = ChangePinWorker.class.toString();
}
