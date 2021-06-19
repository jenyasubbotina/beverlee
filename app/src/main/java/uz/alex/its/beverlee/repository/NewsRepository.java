package uz.alex.its.beverlee.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.news.NewsDataModel;
import uz.alex.its.beverlee.model.news.NewsModel;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.storage.dao.NewsDao;

public class NewsRepository {
    private final Context context;
    private final NewsDao newsDao;

    public NewsRepository(final Context context) {
        this.context = context;
        this.newsDao = LocalDatabase.getInstance(context).newsDao();
    }

    public void fetchNews(final Integer page, final Integer perPage) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getNews(page, perPage, new Callback<NewsModel>() {
            @Override
            public void onResponse(@NonNull Call<NewsModel> call, @NonNull Response<NewsModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final NewsModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.w(TAG, "fetchNews(): empty response from server");
                        return;
                    }
                    final int itemCount = customizableObject.getRecordsTotal();
                    final List<NewsModel.News> newsList = customizableObject.getNewsList();

                    if (newsList == null) {
                        Log.w(TAG, "fetchNews(): newsList is NULL");
                        return;
                    }
                    if (newsList.isEmpty()) {
                        Log.w(TAG, "fetchNews(): newsList is empty");
                        return;
                    }
                    saveNewsList(newsList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public void saveNewsList(final List<NewsModel.News> newsList) {
        new Thread(() -> newsDao.insertNewsList(newsList)).start();
    }

    public void fetchNewsData(final long newsId, final Callback<NewsDataModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getNewsData(newsId, callback);
    }

    public LiveData<List<NewsModel.News>> getNewsList() {
        return newsDao.selectAllNews();
    }

    public LiveData<List<NewsModel.News>> getNewsListByQuery(final String query) {
        return newsDao.selectNewsListByTitle(query);
    }

    private static final String TAG = NewsRepository.class.toString();
}
