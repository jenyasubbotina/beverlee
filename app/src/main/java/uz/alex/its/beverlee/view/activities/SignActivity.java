package uz.alex.its.beverlee.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.push.NotifyManager;
import uz.alex.its.beverlee.push.TokenReceiver;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        final TokenReceiver tokenReceiver = new TokenReceiver(this);

        final NotifyManager notifyManager = new NotifyManager(this);
        notifyManager.createNotificationChannel(Constants.DEFAULT_CHANNEL_ID, Constants.DEFAULT_CHANNEL_NAME);
        notifyManager.createNotificationChannel(Constants.NEWS_CHANNEL_ID, Constants.NEWS_CHANNEL_NAME);
        notifyManager.createNotificationChannel(Constants.BONUS_CHANNEL_ID, Constants.BONUS_CHANNEL_NAME);
        notifyManager.createNotificationChannel(Constants.TRANSFER_CHANNEL_ID, Constants.INCOME_CHANNEL_NAME);
        notifyManager.createNotificationChannel(Constants.PURCHASE_CHANNEL_ID, Constants.PURCHASE_CHANNEL_NAME);
        notifyManager.createNotificationChannel(Constants.REPLENISH_CHANNEL_ID, Constants.REPLENISH_CHANNEL_NAME);
        notifyManager.createNotificationChannel(Constants.WITHDRAWAL_CHANNEL_ID, Constants.WITHDRAWAL_CHANNEL_NAME);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(tokenReceiver.obtainFcmToken()).observe(this, workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                SharedPrefs.getInstance(this).putString(Constants.FCM_TOKEN, workInfo.getOutputData().getString(Constants.FCM_TOKEN));
            }
        });
        if (SharedPrefs.getInstance(this).isLogged(this)) {
            startActivity(new Intent(this, MainActivity.class).putExtra(Constants.PIN_ASSIGNED, true));
            overridePendingTransition(0, 0);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    private static final String TAG = SignActivity.class.toString();
}
