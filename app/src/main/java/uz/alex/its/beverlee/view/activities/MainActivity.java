package uz.alex.its.beverlee.view.activities;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.PermissionManager;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener {
    private BottomNavigationView bottomNavigationView;
    private static int currentNavItem = R.id.navigation_home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (!PermissionManager.getInstance().permissionsGranted(this, permissionArray, Constants.REQUEST_CODE_READ_CONTACTS)) {
            PermissionManager.getInstance().requestPermissions(this, permissionArray, Constants.REQUEST_CODE_READ_CONTACTS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState(currentNavItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        overridePendingTransition(0, 0);
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        bottomNavigationView.postDelayed(() -> {
            final int itemId = item.getItemId();

            if (currentNavItem == itemId) {
                return;
            }
            if (itemId == R.id.navigation_home) {
                currentNavItem = R.id.navigation_home;
                Navigation.findNavController(this, R.id.home_fragment_container).navigate(R.id.homeFragment);
                return;
            }
            if (itemId == R.id.navigation_monitoring) {
                currentNavItem = R.id.navigation_monitoring;
                Navigation.findNavController(this, R.id.home_fragment_container).navigate(R.id.monitoringFragment);
                return;
            }
            if (itemId == R.id.navigation_contacts) {
                currentNavItem = R.id.navigation_contacts;
                Navigation.findNavController(this, R.id.home_fragment_container).navigate(R.id.contactsFragment);
            }

        }, 0);
        return true;
    }

    private void updateNavigationBarState(int itemId) {
        MenuItem item = bottomNavigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    private static final String[] permissionArray = { Manifest.permission.READ_CONTACTS };
    private static final String TAG = MainActivity.class.toString();
}