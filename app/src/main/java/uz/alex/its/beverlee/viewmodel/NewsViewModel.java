package uz.alex.its.beverlee.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.model.news.NewsModel.News;
import uz.alex.its.beverlee.model.news.NewsDataModel.NewsData;
import uz.alex.its.beverlee.model.news.NewsDataModel;
import uz.alex.its.beverlee.model.news.NewsModel;
import uz.alex.its.beverlee.repository.NewsRepository;

public class NewsViewModel extends ViewModel {
    private final NewsRepository repository;
    private final MutableLiveData<List<News>> newsMutableLiveData;
    private final MutableLiveData<List<NewsData>> newsDataMutableLiveData;

    public NewsViewModel(final Context context) {
        this.repository = new NewsRepository(context);
        this.newsMutableLiveData = new MutableLiveData<>();
        this.newsDataMutableLiveData = new MutableLiveData<>();
    }

    public void fetchNews(final Integer page, final Integer perPage) {
        repository.fetchNews(page, perPage, new Callback<NewsModel>() {
            @Override
            public void onResponse(@NonNull Call<NewsModel> call, @NonNull Response<NewsModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final NewsModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.w(TAG, "fetchNews(): empty response from server");
                        return;
                    }
                    final int itemCount = customizableObject.getRecordsTotal();
                    final List<News> newsList = customizableObject.getNewsList();

                    if (newsList == null) {
                        Log.w(TAG, "fetchNews(): newsList is NULL");
                        return;
                    }
                    if (newsList.isEmpty()) {
                        Log.w(TAG, "fetchNews(): newsList is empty");
                        return;
                    }
                    newsMutableLiveData.setValue(newsList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public void fetchNewsData(final long newsId) {
        repository.fetchNewsData(newsId, new Callback<NewsDataModel>() {
            @Override
            public void onResponse(@NonNull Call<NewsDataModel> call, @NonNull Response<NewsDataModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final NewsDataModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.w(TAG, "fetchNewsData(): empty response from server");
                        return;
                    }
                    final int itemCount = customizableObject.getRecordsTotal();
                    final List<NewsData> newsData = customizableObject.getNewsData();

                    if (newsData == null) {
                        Log.w(TAG, "fetchNewsData(): newsList is NULL");
                        return;
                    }
                    if (newsData.isEmpty()) {
                        Log.w(TAG, "fetchNewsData(): newsList is empty");
                        return;
                    }
                    newsDataMutableLiveData.setValue(newsData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsDataModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public LiveData<List<News>> getNewsList() {
        return newsMutableLiveData;
    }

    public LiveData<List<NewsData>> getNewsData() {
        return newsDataMutableLiveData;
    }

    private static final String TAG = NewsViewModel.class.toString();
}
