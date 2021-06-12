package uz.alex.its.beverlee.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import java.net.URL;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;

@Singleton
public class NetworkConnectivity {
    private final Context context;
    private final AppExecutors appExecutors;

    @Inject
    public NetworkConnectivity(final Context context, final AppExecutors appExecutors) {
        this.context = context;
        this.appExecutors = appExecutors;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }
        NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());

        if (cap == null) {
            return false;
        }
        return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public synchronized void checkInternetConnection(final ConnectivityCallback callback) {
        appExecutors.getNetworkIO().execute(() -> {
            if (isNetworkAvailable()) {
                try {
                    HttpsURLConnection urlc = (HttpsURLConnection)
                            new URL("https://clients3.google.com/generate_204").openConnection();
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1000);
                    urlc.connect();
                    boolean isConnected = urlc.getResponseCode() == 204 && urlc.getContentLength() == 0;
                    postCallback(callback, isConnected);
                } catch (Exception e) {
                    postCallback(callback, false);
                }
            } else {
                postCallback(callback, false);
            }
        });
    }

    private void postCallback(final ConnectivityCallback callback, boolean isConnected) {
        appExecutors.getMainThread().execute(() -> callback.onDetected(isConnected));
    }
}
