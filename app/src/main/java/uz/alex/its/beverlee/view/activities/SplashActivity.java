package uz.alex.its.beverlee.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import uz.alex.its.beverlee.R;

public class SplashActivity extends AppCompatActivity {
    private TextView firstNameTextView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firstNameTextView = findViewById(R.id.firstNameTextView);
        handler.postDelayed(() -> {
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1000);

    }
}
