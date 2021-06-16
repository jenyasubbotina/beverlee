package uz.alex.its.beverlee.viewmodel;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.model.transaction.PurchaseModel;
import uz.alex.its.beverlee.repository.PurchaseRepository;

public class PurchaseViewModel extends ViewModel {
    private final PurchaseRepository purchaseRepository;

    private final MutableLiveData<List<PurchaseModel.Purchase>> purchaseList;
    private final MutableLiveData<UUID> makePurchaseUUID;
    private final MutableLiveData<UUID> deletePurchaseUUID;

    public PurchaseViewModel(final Context context) {
        this.purchaseRepository = new PurchaseRepository(context);
        this.purchaseList = new MutableLiveData<>();
        this.makePurchaseUUID = new MutableLiveData<>();
        this.deletePurchaseUUID = new MutableLiveData<>();
    }

    public void fetchPurchaseList() {
        purchaseRepository.fetchPurchaseList(new Callback<PurchaseModel>() {
            @Override
            public void onResponse(@NonNull Call<PurchaseModel> call, @NonNull Response<PurchaseModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final PurchaseModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.e(TAG, "fetchPurchaseList(): empty response from server");
                        return;
                    }
                    final List<PurchaseModel.Purchase> fetchedPurchaseList = customizableObject.getPurchaseList();

                    if (fetchedPurchaseList == null) {
                        Log.w(TAG, "fetchPurchaseList(): empty purchaseList from server");
                        return;
                    }
                    purchaseList.setValue(fetchedPurchaseList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PurchaseModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public void makePurchase(final long requestId, final String pin) {
        makePurchaseUUID.setValue(purchaseRepository.makePurchase(requestId, pin));
    }

    public void deletePurchase(final long requestId) {
        deletePurchaseUUID.setValue(purchaseRepository.deletePurchase(requestId));
    }

    public LiveData<WorkInfo> getMakePurchaseResult(final Context context) {
        return Transformations.switchMap(makePurchaseUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getDeletePurchaseResult(final Context context) {
        return Transformations.switchMap(deletePurchaseUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    private static final String TAG = PurchaseViewModel.class.toString();

    public LiveData<List<PurchaseModel.Purchase>> getPurchaseList() {
        return purchaseList;
    }
}
