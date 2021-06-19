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
    long insertContact(final Contact contact);

    @Delete
    int deleteContact(final Contact contact);

    @Query("SELECT * FROM contact_list WHERE is_fav == :isFav ORDER BY full_name ASC")
    LiveData<List<Contact>> selectFavContactList(final boolean isFav);

    @Query("SELECT * FROM contact_list WHERE is_fav == :isFav ORDER BY full_name ASC")
    LiveData<List<Contact>> selectContactList(final boolean isFav);

    @Query("SELECT * FROM contact_list WHERE full_name LIKE '%' || :query || '%' ORDER BY full_name ASC")
    LiveData<List<Contact>> selectContactListBySearchQuery(final String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertContactList(List<Contact> contactData);
}
