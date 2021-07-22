package uz.alex.its.beverlee.storage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.model.news.NewsModel;
import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.transaction.TransactionModel;
import uz.alex.its.beverlee.storage.converters.DateConverter;
import uz.alex.its.beverlee.storage.converters.ProductConverter;
import uz.alex.its.beverlee.storage.converters.RequestConverter;
import uz.alex.its.beverlee.storage.converters.TransactionParticipantConverter;
import uz.alex.its.beverlee.storage.dao.ContactDao;
import uz.alex.its.beverlee.storage.dao.NewsDao;
import uz.alex.its.beverlee.storage.dao.PushDao;
import uz.alex.its.beverlee.storage.dao.TransactionDao;
import uz.alex.its.beverlee.utils.Constants;

@Database(entities = {
        Contact.class,
        Push.class,
        NewsModel.News.class,
        TransactionModel.Transaction.class}, version = 15, exportSchema = false)
@TypeConverters({ DateConverter.class, TransactionParticipantConverter.class, RequestConverter.class, ProductConverter.class})
public abstract class LocalDatabase extends RoomDatabase {
    /* Declare dao objects here */
    public abstract PushDao pushDao();
    public abstract ContactDao contactDao();
    public abstract NewsDao newsDao();
    public abstract TransactionDao transactionDao();

    private static volatile LocalDatabase instance;

    public static LocalDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (LocalDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, Constants.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                }
                            }).build();
                }
            }
        }
        return instance;
    }

    private static final String TAG = LocalDatabase.class.toString();
}
