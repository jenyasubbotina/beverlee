package uz.alex.its.beverlee.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.UUID;

import uz.alex.its.beverlee.repository.PinRepository;

public class PinViewModel extends ViewModel {
    private final PinRepository pinRepository;

    private final MutableLiveData<UUID> checkPinAssignedUUID;
    private final MutableLiveData<UUID> assignPinUUID;
    private final MutableLiveData<UUID> verifyPinUUID;
    private final MutableLiveData<UUID> changePinUUID;

    public PinViewModel(final Context context) {
        this.pinRepository = new PinRepository(context);
        this.checkPinAssignedUUID = new MutableLiveData<>();
        this.assignPinUUID = new MutableLiveData<>();
        this.verifyPinUUID = new MutableLiveData<>();
        this.changePinUUID = new MutableLiveData<>();
    }

    public void checkPinAssigned() {
        checkPinAssignedUUID.setValue(pinRepository.checkPinAssigned());
    }

    public void assignPin(final String pin) {
        assignPinUUID.setValue(pinRepository.assignPin(pin));
    }

    public void verifyPin(final String pincode) {
        verifyPinUUID.setValue(pinRepository.verifyPin(pincode));
    }

    public LiveData<WorkInfo> getCheckPinAssignedResult(final Context context) {
        return Transformations.switchMap(checkPinAssignedUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getAssignPinResult(final Context context) {
        return Transformations.switchMap(assignPinUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getVerifyPinResult(final Context context) {
        return Transformations.switchMap(verifyPinUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public void requestPinBySms() {
        pinRepository.requestPinBySms();
    }

    public void requestPinByCall() {
        pinRepository.requestPinByCall();
    }

    public void changePin(final String oldPin, final String newPin, final String newPinConfirmation) {
        changePinUUID.setValue(pinRepository.changePin(oldPin, newPin, newPinConfirmation));
    }

    public LiveData<WorkInfo> getChangePinResult(final Context context) {
        return Transformations.switchMap(changePinUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    private static final String TAG = PinViewModel.class.toString();
}
