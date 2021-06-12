package uz.alex.its.beverlee.view;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.lang.ref.WeakReference;
import java.util.Locale;
import uz.alex.its.beverlee.R;

public class CounterTask extends AsyncTask<Void, Integer, Integer> {
    private WeakReference<Resources> resourcesRef;
    private WeakReference<TextView> textViewRef;
    private WeakReference<Button> firstBtnRef;
    private WeakReference<Button> secondBtnRef;

    public CounterTask(@NonNull final Resources res,
                       @NonNull final TextView textView,
                       @NonNull final Button firstBtn,
                       @NonNull final Button secondBtn) {
        this.resourcesRef = new WeakReference<>(res);
        this.textViewRef = new WeakReference<>(textView);
        this.firstBtnRef = new WeakReference<>(firstBtn);
        this.secondBtnRef = new WeakReference<>(secondBtn);
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute: ");
        super.onPreExecute();
        //change to 01:00
        textViewRef.get().setVisibility(View.VISIBLE);
        textViewRef.get().setText("01:00");
        firstBtnRef.get().setEnabled(false);
        secondBtnRef.get().setEnabled(false);
        firstBtnRef.get().setBackground(ResourcesCompat.getDrawable(resourcesRef.get(), R.drawable.btn_locked, null));
        secondBtnRef.get().setBackground(ResourcesCompat.getDrawable(resourcesRef.get(), R.drawable.btn_locked, null));
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        Log.i(TAG, "doInBackground: ");
        try {
            //change to 59 seconds
            for (int i = 59; i >= 0; i--) {
                Thread.sleep(1000L);
                publishProgress(i);
            }
        }
        catch (InterruptedException e) {
            Log.e(TAG, "doInBackground(): ", e);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        final int minutes = values[0] / 60;
        final int seconds = values[0] % 60;
        String minuteFormat = "0%d";
        String secondFormat = "%d";

        if (seconds <= 9) {
            secondFormat = String.format(Locale.US, "0%d", seconds);
        }
        final String minutesStr = String.format(Locale.US, minuteFormat, minutes);
        final String secondsStr = String.format(Locale.US, secondFormat, seconds);
        final String result = minutesStr + ":" + secondsStr;
        textViewRef.get().setText(result);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        Log.i(TAG, "onPostExecute: ");
        super.onPostExecute(integer);
        //make editText active again
        //make btn active again
        firstBtnRef.get().setEnabled(true);
        firstBtnRef.get().setBackground(ResourcesCompat.getDrawable(resourcesRef.get(), R.drawable.btn_purple, null));
        secondBtnRef.get().setEnabled(true);
        secondBtnRef.get().setBackground(ResourcesCompat.getDrawable(resourcesRef.get(), R.drawable.btn_purple, null));
        textViewRef.get().setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    private static class Counter implements Runnable {
        private volatile boolean exit;
        Thread t;

        private Counter() {
            this.t = new Thread(this);
            this.exit = false;
            t.start();
        }

        @Override
        public void run() {
            while (!exit) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e) {
                    Log.e(TAG, "run(): ", e);
                }
            }
        }

        private void stop() {
            exit = true;
        }
    }

    private static final String TAG = CounterTask.class.toString();
}
