package uz.alex.its.beverlee.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
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

    private final MutableLiveData<Boolean> searchFieldEmpty;
    private final MutableLiveData<String> searchByTitle;

    public NewsViewModel(final Context context) {
        this.repository = new NewsRepository(context);

        this.searchByTitle = new MutableLiveData<>();
        this.searchFieldEmpty = new MutableLiveData<>();
        this.searchFieldEmpty.setValue(true);
    }

    public void fetchNews(final Integer page, final Integer perPage) {
        repository.fetchNews(page, perPage);
    }

    public LiveData<List<News>> getNewsList() {
        return Transformations.switchMap(searchFieldEmpty, input -> {
            if (input) {
                return repository.getNewsList();
            }
            Log.i(TAG, "searching by title: " + searchByTitle.getValue());
            return Transformations.switchMap(searchByTitle, repository::getNewsListByQuery);
        });
    }

    public void submitSearch(final String value) {
        this.searchByTitle.postValue(value);
    }

    public void setSearchFieldEmpty(final boolean value) {
        this.searchFieldEmpty.setValue(value);
    }

    private static final String TAG = NewsViewModel.class.toString();
}
