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
import uz.alex.its.beverlee.model.requestParams.NotificationSettingsParams;
import uz.alex.its.beverlee.utils.Constants;

public class SaveNotificationSettingsWorker extends Worker {
    private final Context context;
    private final NotificationSettingsParams params;

    public SaveNotificationSettingsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
        this.params = new NotificationSettingsParams(
                getInputData().getBoolean(Constants.NOTIFY_NEWS, false),
                getInputData().getBoolean(Constants.NOTIFY_BONUSES, false),
                getInputData().getBoolean(Constants.NOTIFY_INCOME, false),
                getInputData().getBoolean(Constants.NOTIFY_PURCHASE, false),
                getInputData().getBoolean(Constants.NOTIFY_REPLENISH, false),
                getInputData().getBoolean(Constants.NOTIFY_WITHDRAWAL, false));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        if (params == null) {
            Log.e(TAG, "doWork(): input data is NULL");
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "input data is NULL").build());
        }

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<Void> response = RetrofitClient.getInstance(context).saveNotificationSettings(params);

            if (response.code() == 200 && response.isSuccessful()) {
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

    private static final String TAG = SaveNotificationSettingsWorker.class.toString();
}
