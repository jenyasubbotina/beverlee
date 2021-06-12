package uz.alex.its.beverlee.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.utils.Constants;

public class RemoveFromFavsWorker extends Worker {
    private final Context context;
    private final long id;
    private final long contactId;
    private final String contactFullName;

    public RemoveFromFavsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = getApplicationContext();
        this.id = getInputData().getLong(Constants.ID, 0L);
        this.contactId = getInputData().getLong(Constants.CONTACT_ID, 0L);
        this.contactFullName = getInputData().getString(Constants.CONTACT_FULL_NAME);
    }

    @NonNull
    @Override
    public Result doWork() {
        return LocalDatabase.getInstance(context).contactDao().deleteContact(new ContactData(id, new Contact(contactId, contactFullName, false))) <= 0
                ? Result.failure(new Data.Builder().putString(Constants.REQUEST_ERROR, "no such record found").build())
                : Result.success();
    }
}
