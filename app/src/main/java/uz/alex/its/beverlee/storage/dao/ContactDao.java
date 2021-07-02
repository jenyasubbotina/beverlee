package uz.alex.its.beverlee.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;

@Dao
public interface ContactDao {
    @Query("UPDATE contact_list SET is_fav = :isFav WHERE id == :id")
    int updateContact(final long id, final boolean isFav);

    @Delete
    int deleteContact(final Contact contact);

    @Query("SELECT * FROM contact_list WHERE is_fav == :isFav ORDER BY full_name ASC")
    LiveData<List<Contact>> selectFavContactList(final boolean isFav);

    @Query("SELECT * FROM contact_list ORDER BY full_name ASC")
    LiveData<List<Contact>> selectContactList();

    @Query("SELECT * FROM contact_list WHERE full_name LIKE '%' || :query || '%' ORDER BY full_name ASC")
    LiveData<List<Contact>> selectContactListBySearchQuery(final String query);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertContactList(List<Contact> contactData);
}
