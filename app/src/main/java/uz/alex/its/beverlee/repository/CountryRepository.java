package uz.alex.its.beverlee.repository;

import android.content.Context;

import retrofit2.Callback;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.CountryModel;

public class CountryRepository {
    private final Context context;

    public CountryRepository(final Context context) {
        this.context = context;
    }

    public void fetchCountryList(final Callback<CountryModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getCountryList(callback);
    }
}
