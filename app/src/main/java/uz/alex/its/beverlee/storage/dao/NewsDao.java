package uz.alex.its.beverlee.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import uz.alex.its.beverlee.model.news.NewsModel;
import uz.alex.its.beverlee.model.notification.Push;

@Dao
public interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertNewsList(final List<NewsModel.News> news);

    @Query("SELECT * FROM news ORDER BY id DESC")
    LiveData<List<NewsModel.News>> selectAllNews();

    @Query("SELECT * FROM news WHERE title LIKE '%' || :title || '%' ORDER BY id DESC")
    LiveData<List<NewsModel.News>> selectNewsListByTitle(final String title);
}
