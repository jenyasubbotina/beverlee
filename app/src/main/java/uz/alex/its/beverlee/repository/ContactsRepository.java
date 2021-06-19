package uz.alex.its.beverlee.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.model.actor.ContactModel;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.storage.dao.ContactDao;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.worker.AddToFavsWorker;
import uz.alex.its.beverlee.worker.DeleteContactWorker;
import uz.alex.its.beverlee.worker.RemoveFromFavsWorker;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;

public class ContactsRepository {
    private final Context context;
    private final ContactDao contactDao;

    public ContactsRepository(final Context context) {
        this.context = context;
        this.contactDao = LocalDatabase.getInstance(context).contactDao();
    }

    public void fetchContactList(final String searchQuery, final Integer page, final Integer perPage) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getContactList(searchQuery, page, perPage, new Callback<ContactModel>() {
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
                    final List<Contact> contactList = new ArrayList<>();

                    for (final ContactData contact : customizableObject.getContactData()) {
                        contactList.add(new Contact(contact.getContact().getId(), contact.getContact().getFio(), false));
                    }
                    saveContactList(contactList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ContactModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public void fetchContactData(final long contactId, final Callback<ContactModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getContactData(contactId, callback);
    }

    public UUID deleteContact(final long id) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putLong(Constants.ID, id)
                .build();
        final OneTimeWorkRequest deleteContactRequest = new OneTimeWorkRequest.Builder(DeleteContactWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(deleteContactRequest);
        return deleteContactRequest.getId();
    }

    public UUID addToFavs(final Contact contact) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(true)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putLong(Constants.ID, contact.getId())
                .putLong(Constants.CONTACT_ID, contact.getId())
                .putString(Constants.CONTACT_FULL_NAME, contact.getFio())
                .build();
        final OneTimeWorkRequest addToFavsRequest = new OneTimeWorkRequest.Builder(AddToFavsWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(addToFavsRequest);
        return addToFavsRequest.getId();
    }

    public UUID removeFromFavs(final Contact contact) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putLong(Constants.ID, contact.getId())
                .putLong(Constants.CONTACT_ID, contact.getId())
                .putString(Constants.CONTACT_FULL_NAME, contact.getFio())
                .build();
        final OneTimeWorkRequest removeFromFavsRequest = new OneTimeWorkRequest.Builder(RemoveFromFavsWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(removeFromFavsRequest);
        return removeFromFavsRequest.getId();
    }

    public LiveData<List<Contact>> getFavContactList() {
        return contactDao.selectFavContactList(true);
    }

    public void saveContactList(final List<Contact> contactList) {
        new Thread(() -> contactDao.insertContactList(contactList)).start();
    }

    private static final String TAG = ContactsRepository.class.toString();

    public LiveData<List<Contact>> getContactList() {
        return contactDao.selectContactList(false);
    }

    public LiveData<List<Contact>> getContactListBySearchQuery(final String query) {
        return contactDao.selectContactListBySearchQuery(query);
    }
}
