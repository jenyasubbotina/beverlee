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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.model.actor.ContactModel;
import uz.alex.its.beverlee.repository.ContactsRepository;

public class ContactsViewModel extends ViewModel {
    private final ContactsRepository contactsRepository;

    private final MutableLiveData<List<ContactData>> contactList;

    private final MutableLiveData<Boolean> isFavorite;

    private final MutableLiveData<UUID> deleteContactUUID;
    private final MutableLiveData<UUID> addToFavsUUID;
    private final MutableLiveData<UUID> removeFromFavsUUID;

    public ContactsViewModel(final Context context) {
        this.contactsRepository = new ContactsRepository(context);

        this.contactList = new MutableLiveData<>();

        this.isFavorite = new MutableLiveData<>();
        this.isFavorite.setValue(false);

        this.deleteContactUUID = new MutableLiveData<>();
        this.addToFavsUUID = new MutableLiveData<>();
        this.removeFromFavsUUID = new MutableLiveData<>();
    }

    public LiveData<List<ContactData>> getContactList() {
        return Transformations.switchMap(isFavorite, input -> {
            if (!input) {
                return contactList;
            }
            return contactsRepository.getFavContactList();
        });
    }

    public void fetchContactList(final Integer page, final Integer perPage) {
        contactsRepository.fetchContactList(null, page, perPage, new Callback<ContactModel>() {
            @Override
            public void onResponse(@NonNull Call<ContactModel> call, @NonNull Response<ContactModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final ContactModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.e(TAG, "onResponse(): response=null");
                        return;
                    }
                    if (customizableObject.getContactData() == null) {
                        Log.e(TAG, "onResponse(): contactList=null");
                        return;
                    }
                    contactList.setValue(customizableObject.getContactData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContactModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public void deleteContact(final long id) {
        deleteContactUUID.setValue(contactsRepository.deleteContact(id));
    }

    public void addToFavs(final ContactData contact) {
        addToFavsUUID.setValue(contactsRepository.addToFavs(contact));
    }

    public void removeFromFavs(final ContactData contact) {
        removeFromFavsUUID.setValue(contactsRepository.removeFromFavs(contact));
    }

    public LiveData<WorkInfo> getDeleteContactResult(final Context context) {
        return Transformations.switchMap(deleteContactUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getAddToFavsResult(final Context context) {
        return Transformations.switchMap(addToFavsUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getRemoveFromFavsResult(final Context context) {
        return Transformations.switchMap(removeFromFavsUUID, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    private static final String TAG = ContactsViewModel.class.toString();

    public void setIsFavorite(final boolean b) {
        isFavorite.setValue(b);
    }

    public void searchContactList(final String name) {
        contactsRepository.fetchContactList(name, null, null, new Callback<ContactModel>() {
            @Override
            public void onResponse(@NonNull Call<ContactModel> call, @NonNull Response<ContactModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final ContactModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.e(TAG, "onResponse(): response=null");
                        return;
                    }
                    if (customizableObject.getContactData() == null) {
                        Log.e(TAG, "onResponse(): contactList=null");
                        return;
                    }
                    contactList.setValue(customizableObject.getContactData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContactModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }
}
