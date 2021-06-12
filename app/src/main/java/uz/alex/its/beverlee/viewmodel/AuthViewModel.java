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
import uz.alex.its.beverlee.model.Country;
import uz.alex.its.beverlee.model.CountryModel;
import uz.alex.its.beverlee.repository.AuthRepository;
import uz.alex.its.beverlee.repository.CountryRepository;
import uz.alex.its.beverlee.repository.PinRepository;

public class AuthViewModel extends ViewModel {
    private final CountryRepository countryRepository;
    private final AuthRepository authRepository;
    private final PinRepository pinRepository;
    private final MutableLiveData<List<Country>> countryList;

    private final MutableLiveData<UUID> loginUUID;
    private final MutableLiveData<UUID> signUpUUID;
    private final MutableLiveData<UUID> submitVerificationUUID;

    public AuthViewModel(final Context context) {
        this.countryRepository = new CountryRepository(context);
        this.authRepository = new AuthRepository(context);
        this.pinRepository = new PinRepository(context);
        this.countryList = new MutableLiveData<>();
        this.loginUUID = new MutableLiveData<>();
        this.signUpUUID = new MutableLiveData<>();
        this.submitVerificationUUID = new MutableLiveData<>();
    }

    public void signUp(final String firstName,
                       final String lastName,
                       final String phone,
                       final String email,
                       final long countryId,
                       final String city,
                       final String password,
                       final String passwordConfirmation) {
        signUpUUID.setValue(authRepository.register(firstName, lastName, phone, email, countryId, city, password, passwordConfirmation));
    }

    public void login(final String phone, final String password) {
        loginUUID.setValue(authRepository.login(phone, password));
    }

    public void verifyPhoneBySms() {
        authRepository.verifyPhoneBySms();
    }

    public void verifyPhoneByCall() {
        authRepository.verifyPhoneByCall();
    }

    public void submitVerification(final String code) {
        submitVerificationUUID.setValue(authRepository.submitVerification(code));
    }

    public void fetchCountryList() {
        countryRepository.fetchCountryList(new Callback<CountryModel>() {
            @Override
            public void onResponse(@NonNull Call<CountryModel> call, @NonNull Response<CountryModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final CountryModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.e(TAG, "onResponse(): customizableObject=null");
                        return;
                    }
                    countryList.setValue(customizableObject.getCountryList());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CountryModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public LiveData<List<Country>> getCountryList() {
        return countryList;
    }

    public LiveData<WorkInfo> getLoginResult(final Context context) {
        return Transformations.switchMap(loginUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getSignUpResult(final Context context) {
        return Transformations.switchMap(signUpUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getSubmitVerificationResult(final Context context) {
        return Transformations.switchMap(submitVerificationUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    private static final String TAG = AuthViewModel.class.toString();
}