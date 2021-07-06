package uz.alex.its.beverlee.worker.contact;

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
import uz.alex.its.beverlee.model.actor.ContactModel;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.utils.Constants;

public class DeleteContactWorker extends Worker {
    private final Context context;
    private final long contactId;

    public DeleteContactWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
        this.contactId = getInputData().getLong(Constants.CONTACT_ID, 0L);
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<Void> response = RetrofitClient.getInstance(context).deleteContact(contactId);

            if (response.code() == 204 && response.isSuccessful()) {
                LocalDatabase.getInstance(context).contactDao().deleteContact(contactId);
                return Result.success();
            }
            if (response.code() == 404) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "not found").build());
            }
            if (response.code() == 422) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "unprocessable entity").build());
            }
            if (response.code() == 405) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "method not allowed").build());
            }
            final ResponseBody error = response.errorBody();

            if (error == null) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, Constants.UNKNOWN_ERROR).build());
            }
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, "error").build());
        }
        catch (IOException e) {
            Log.e(TAG, "doWork(): " + e.getMessage());
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, e.getMessage()).build());
        }
    }

    private static final String TAG = DeleteContactWorker.class.toString();
}