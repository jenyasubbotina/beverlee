package uz.alex.its.beverlee.repository;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.UUID;

import retrofit2.Callback;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.transaction.PurchaseModel;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.worker.purchase.DeletePurchaseWorker;
import uz.alex.its.beverlee.worker.purchase.MakePurchaseWorker;

public class PurchaseRepository {
    private final Context context;

    public PurchaseRepository(final Context context) {
        this.context = context;
    }

    public void fetchPurchaseList(final Callback<PurchaseModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getUserPurchases(callback);
    }

    public UUID makePurchase(final long requestId, final String pin) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putLong(Constants.REQUEST_ID, requestId)
                .putString(Constants.PINCODE, pin)
                .build();
        final OneTimeWorkRequest makePurchaseRequest = new OneTimeWorkRequest.Builder(MakePurchaseWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(makePurchaseRequest);
        return makePurchaseRequest.getId();
    }

    public UUID deletePurchase(final long requestId) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putLong(Constants.REQUEST_ID, requestId)
                .build();
        final OneTimeWorkRequest deletePurchaseRequest = new OneTimeWorkRequest.Builder(DeletePurchaseWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(deletePurchaseRequest);
        return deletePurchaseRequest.getId();
    }
}
