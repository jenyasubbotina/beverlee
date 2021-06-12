package uz.alex.its.beverlee.view;

import android.os.Handler;
import android.widget.TextView;

import java.util.Locale;

public class Counter implements Runnable {
    private final TextView textView;
    private final Handler handler;
    private volatile boolean isFinished = false;

    public Counter(TextView textView) {
        this.textView = textView;
        this.handler = new Handler();
    }

    @Override
    public void run() {
        while (!isFinished) {
            for (int i = 178; i > 0; i--) {
                handler.postDelayed(this, 1000L);
                final int minutes = i / 60;
                final int seconds = i % 60;
                String minuteFormat = "0%d";
                String secondFormat = "%d";

                if (seconds <= 9) {
                    secondFormat = String.format(Locale.US, "0%d", seconds);
                }
                final String minutesStr = String.format(Locale.US, minuteFormat, minutes);
                final String secondsStr = String.format(Locale.US, secondFormat, seconds);
                final String result = minutesStr + ":" + secondsStr;
                textView.setText(result);
            }
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void stop() {
        isFinished = true;
    }
}
