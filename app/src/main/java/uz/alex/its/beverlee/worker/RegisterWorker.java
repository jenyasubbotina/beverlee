package uz.alex.its.beverlee.worker;

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
import uz.alex.its.beverlee.model.LoginModel;
import uz.alex.its.beverlee.model.requestParams.RegisterParams;
import uz.alex.its.beverlee.model.response.error.LoginErrorModel;
import uz.alex.its.beverlee.utils.Constants;

public class RegisterWorker extends Worker {
    private final Context context;

    private final RegisterParams registerParams;

    public RegisterWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        this.context = getApplicationContext();
        this.registerParams = new RegisterParams(
                getInputData().getString(Constants.FIRST_NAME),
                getInputData().getString(Constants.LAST_NAME),
                getInputData().getString(Constants.PHONE),
                getInputData().getString(Constants.EMAIL),
                getInputData().getLong(Constants.COUNTRY_ID, 0L),
                getInputData().getString(Constants.CITY),
                getInputData().getString(Constants.PASSWORD),
                getInputData().getString(Constants.PASSWORD_CONFIRMATION));
    }

    @NonNull
    @Override
    public Result doWork() {
        final Data.Builder outputDataBuilder = new Data.Builder();

        RetrofitClient.getInstance(getApplicationContext()).setAuthorizationHeader(getApplicationContext());

        try {
            final Response<LoginModel> response = RetrofitClient.getInstance(context).register(registerParams);

            if ((response.code() == 201 || response.code() == 200) && response.isSuccessful()) {
                final LoginModel bearerToken = response.body();
                return Result.success(outputDataBuilder
                        .putString(Constants.BEARER_TOKEN, bearerToken != null ? bearerToken.getToken() : null)
                        .putString(Constants.PHONE, registerParams.getPhone())
                        .build());
            }
            final ResponseBody error = response.errorBody();

            if (error == null) {
                return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, Constants.UNKNOWN_ERROR).build());
            }
            if (response.code() == 422) {
                final Type loginErrorType = new TypeToken<LoginErrorModel>() {}.getType();
                final LoginErrorModel phoneNotVerified = new GsonBuilder().setLenient().create().fromJson(error.string(), loginErrorType);

                if (phoneNotVerified.getLoginError().getPhone().equals(context.getString(R.string.error_phone_wrong_input)))
                    return Result.failure(outputDataBuilder
                            .putString(Constants.REQUEST_ERROR, context.getString(R.string.error_phone_wrong_input))
                            .build());
            }
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, error.string()).build());
        }
        catch (IOException e) {
            Log.e(TAG, "doWork(): ", e);
            return Result.failure(outputDataBuilder.putString(Constants.REQUEST_ERROR, e.getMessage()).build());
        }
    }

    private static final String TAG = RegisterWorker.class.toString();
}

