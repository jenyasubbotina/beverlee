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

    @Query("SELECT * FROM push ORDER BY notification_id DESC")
    LiveData<List<Push>> selectAllPushList();

    @Query("SELECT * FROM push WHERE status == 0 ORDER BY timestamp DESC")
    LiveData<List<Push>> selectNewPushList();

    @Query("UPDATE push SET status = 1 WHERE notification_id == :notificationId")
    int updateStatusRead(final long notificationId);

    @Query("SELECT count() FROM push WHERE status == 0")
    LiveData<Integer> getNotificationCount();
}
