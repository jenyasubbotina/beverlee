package uz.alex.its.beverlee.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import uz.alex.its.beverlee.model.notification.Push;

@Dao
public interface PushDao {
    @Insert
    long insertPush(final Push push);

    @Delete
    void deletePush(final Push push);

    @Query("SELECT * FROM push ORDER BY id DESC")
    LiveData<List<Push>> selectAllPushList();

    @Query("SELECT * FROM push WHERE id <= :id ORDER BY ID DESC")
    LiveData<List<Push>> selectPushListAfter(final String id);

    @Query("SELECT * FROM push WHERE id LIKE :id")
    LiveData<Push> selectPush(final String id);
}
