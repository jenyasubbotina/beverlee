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

    private final MutableLiveData<List<Contact>> contactList;
    private final MutableLiveData<String> searchQuery;
    private final MutableLiveData<Boolean> searchFieldEmpty;

    private final MutableLiveData<Boolean> isFavorite;

    private final MutableLiveData<UUID> deleteContactUUID;
    private final MutableLiveData<UUID> addToFavsUUID;
    private final MutableLiveData<UUID> removeFromFavsUUID;

    public ContactsViewModel(final Context context) {
        this.contactsRepository = new ContactsRepository(context);

        this.contactList = new MutableLiveData<>();
        this.searchQuery = new MutableLiveData<>();
        this.searchFieldEmpty = new MutableLiveData<>();
        this.searchFieldEmpty.setValue(true);

        this.isFavorite = new MutableLiveData<>();
        this.isFavorite.setValue(false);

        this.deleteContactUUID = new MutableLiveData<>();
        this.addToFavsUUID = new MutableLiveData<>();
        this.removeFromFavsUUID = new MutableLiveData<>();
    }

    public LiveData<List<Contact>> getContactList() {
        return Transformations.switchMap(isFavorite, input -> {
            if (!input) {
                return Transformations.switchMap(searchFieldEmpty, isEmpty -> {
                    if (isEmpty) {
                        return contactsRepository.getContactList();
                    }
                    return Transformations.switchMap(searchQuery, contactsRepository::getContactListBySearchQuery);
                });
            }
            return contactsRepository.getFavContactList();
        });
    }

    public void fetchContactList(final Integer page, final Integer perPage) {
        contactsRepository.fetchContactList(null, page, perPage);
    }

    public void deleteContact(final long id) {
        deleteContactUUID.setValue(contactsRepository.deleteContact(id));
    }

    public void addToFavs(final Contact contact) {
        addToFavsUUID.setValue(contactsRepository.addToFavs(contact));
    }

    public void removeFromFavs(final Contact contact) {
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

    public void setIsFavorite(final boolean b) {
        isFavorite.setValue(b);
    }

    public void setSearchQuery(final String query) {
        this.searchQuery.setValue(query);
    }

    public void setSearchFieldEmpty(final boolean empty) {
        this.searchFieldEmpty.setValue(empty);
    }

    private static final String TAG = ContactsViewModel.class.toString();
}
