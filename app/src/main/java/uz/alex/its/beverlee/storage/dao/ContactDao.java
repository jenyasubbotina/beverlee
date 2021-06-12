package uz.alex.its.beverlee.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;

@Dao
public interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertContact(final ContactData contact);

    @Delete
    int deleteContact(final ContactData contact);

    @Query("SELECT * FROM fav_contact_list ORDER BY full_name ASC")
    LiveData<List<ContactData>> selectAllContacts();
}
