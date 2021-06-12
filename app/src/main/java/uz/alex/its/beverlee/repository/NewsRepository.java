package uz.alex.its.beverlee.repository;

import android.content.Context;

import retrofit2.Callback;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.news.NewsDataModel;
import uz.alex.its.beverlee.model.news.NewsModel;

public class NewsRepository {
    private final Context context;

    public NewsRepository(final Context context) {
        this.context = context;
    }

    public void fetchNews(final Integer page, final Integer perPage, final Callback<NewsModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getNews(page, perPage, callback);
    }

    public void fetchNewsData(final long newsId, final Callback<NewsDataModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getNewsData(newsId, callback);
    }

    private static final String TAG = NewsRepository.class.toString();
}
