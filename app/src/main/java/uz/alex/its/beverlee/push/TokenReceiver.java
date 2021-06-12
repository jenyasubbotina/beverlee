package uz.alex.its.beverlee.push;

import android.content.Context;
import android.util.Log;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OverwritingInputMerger;
import androidx.work.WorkManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.UUID;

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

        if (!googleServicesAvailable(context)) {
            Log.w(TAG, "obtainFcmToken(): no google services available");
            return null;
        }
        WorkManager.getInstance(context).enqueue(getFcmTokenRequest);
        return getFcmTokenRequest.getId();
    }

    public void onNewToken(final String token) {
        //todo: refresh subscription with the server
    }

    private boolean googleServicesAvailable(final Context context) {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    private static final String TAG = TokenReceiver.class.toString();
}
