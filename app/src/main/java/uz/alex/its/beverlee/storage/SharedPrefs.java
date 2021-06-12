package uz.alex.its.beverlee.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uz.alex.its.beverlee.model.actor.UserModel.User;
import uz.alex.its.beverlee.utils.Constants;

public class SharedPrefs {
    private static SharedPrefs instance;
    private final SharedPreferences prefs;

    private SharedPrefs(final Context context) {
        this.prefs = context.getSharedPreferences(context.getPackageName() + Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPrefs getInstance(final Context context) {
        if (instance == null) {
            synchronized (SharedPrefs.class) {
                if (instance == null) {
                    instance = new SharedPrefs(context);
                }
            }
        }
        return instance;
    }

    public void putBoolean(final String key, final boolean value) {
        this.prefs.edit().putBoolean(key, value).apply();
    }

    public void putString(final String key, final String value) {
        this.prefs.edit().putString(key, value).apply();
    }

    public void putInt(final String key, final int value) {
        this.prefs.edit().putInt(key, value).apply();
    }

    public void putLong(final String key, final long value) {
        this.prefs.edit().putLong(key, value).apply();
    }

    public String getString(final String key) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<String> callable = () -> this.prefs.getString(key, null);
        final Future<String> value = executorService.submit(callable);
        executorService.shutdown();
        try {
            return value.get();
        }
        catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "getString(): ", e);
            return null;
        }
    }

    public int getInt(final String key) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<Integer> callable = () -> this.prefs.getInt(key, 0);
        final Future<Integer> value = executorService.submit(callable);
        executorService.shutdown();
        try {
            return value.get();
        }
        catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "getInt(): ", e);
            return 0;
        }
    }

    public boolean getBoolean(final String key) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<Boolean> callable = () -> this.prefs.getBoolean(key, false);
        final Future<Boolean> value = executorService.submit(callable);
        executorService.shutdown();
        try {
            return value.get();
        }
        catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "getBoolean(): ", e);
            return false;
        }
    }

    public long getLong(final String key) {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Callable<Long> callable = () -> this.prefs.getLong(key, 0L);
        final Future<Long> value = executorService.submit(callable);
        executorService.shutdown();
        try {
            return value.get();
        }
        catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "getBoolean(): ", e);
            return 0L;
        }
    }

    public void saveUserData(final User userData) {
        putLong(Constants.USER_ID, userData.getId());
        putLong(Constants.CLUB_NUMBER, userData.getClubNumber());
        putString(Constants.FIRST_NAME, userData.getFirstName());
        putString(Constants.LAST_NAME, userData.getLastName());
        putString(Constants.MIDDLE_NAME, userData.getMiddleName());
        putString(Constants.PHONE, userData.getPhone());
        putString(Constants.EMAIL, userData.getEmail());
        putString(Constants.POSITION, userData.getPosition());
        putString(Constants.ADDRESS, userData.getAddress());
        putString(Constants.CITY, userData.getCity());
        putString(Constants.PHOTO_URL, userData.getPhotoUrl());

        if (userData.getCountry() != null) {
            putLong(Constants.COUNTRY_ID, userData.getCountry().getId());
            putString(Constants.COUNTRY_CODE, userData.getCountry().getCode());
            putString(Constants.COUNTRY_TITLE, userData.getCountry().getTitle());
        }
    }

    private static final String TAG = SharedPrefs.class.toString();
}
