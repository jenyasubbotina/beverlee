package uz.alex.its.beverlee.push;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OverwritingInputMerger;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;

import java.util.UUID;

import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;

public class TokenReceiver {
    private final Context context;

    public TokenReceiver(final Context context) {
        this.context = context;
    }

    public UUID obtainFcmToken() {
        final Constraints.Builder getTokenConstraintsBuilder = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .setRequiresStorageNotLow(false);
        final OneTimeWorkRequest getFcmTokenRequest = new OneTimeWorkRequest.Builder(FcmTokenReceiver.class)
                .setInputMerger(OverwritingInputMerger.class)
                .setConstraints(getTokenConstraintsBuilder.build())
                .build();
        WorkManager.getInstance(context).enqueue(getFcmTokenRequest);
        return getFcmTokenRequest.getId();
    }

    public void onNewToken(final String token) {
        SharedPrefs.getInstance(context).putString(Constants.FCM_TOKEN, token);
    }

    public boolean googleServicesAvailable(final Context context) {
        return GoogleApiAvailabilityLight.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    private static final String TAG = TokenReceiver.class.toString();
}
