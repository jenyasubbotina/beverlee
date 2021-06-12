package uz.alex.its.beverlee.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.storage.dao.PushDao;

public class PushRepository {
    private final PushDao pushDao;

    public PushRepository(final Context context) {
        final LocalDatabase database = LocalDatabase.getInstance(context);
        this.pushDao = database.pushDao();
    }

    void insertCountry(final Push push) {
        new Thread(() -> pushDao.insertPush(push)).start();
    }

    void deleteCountry(final Push push) {
        new Thread(() -> pushDao.deletePush(push)).start();
    }

    LiveData<List<Push>> selectAllPushList() {
        return pushDao.selectAllPushList();
    }

    LiveData<List<Push>> selectPushListAfter(final String id) {
        return pushDao.selectPushListAfter(id);
    }

    LiveData<Push> selectPush(final String id) {
        return pushDao.selectPush(id);
    }
}
