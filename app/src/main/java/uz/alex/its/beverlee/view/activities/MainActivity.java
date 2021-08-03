package uz.alex.its.beverlee.view.activities;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.PermissionManager;
import uz.alex.its.beverlee.viewmodel.NotificationViewModel;
import uz.alex.its.beverlee.viewmodel.factory.NotificationViewModelFactory;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {
    private BottomNavigationView bottomNavigationView;
    private static int currentNavItem;

    private NotificationViewModel notificationViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        currentNavItem = R.id.navigation_home;

        final NotificationViewModelFactory notificationFactory = new NotificationViewModelFactory(this);
        notificationViewModel = new ViewModelProvider(this, notificationFactory).get(NotificationViewModel.class);

        if (getIntent() != null) {
            final long notificationId = getIntent().getLongExtra(Push.NOTIFICATION_ID, 0L);
            final String type = getIntent().getStringExtra(Push.TYPE);
            final long newsId = getIntent().getLongExtra(Push.NEWS_ID, 0L);

            if (notificationId > 0 && type != null) {
                notificationViewModel.updateStatusRead(notificationId);

                switch (type) {
                    case Push.TYPE_BONUS:
                    case Push.TYPE_REPLENISH:
                    case Push.TYPE_WITHDRAWAL:
                    case Push.TYPE_TRANSFER:
                        break;
                    case Push.TYPE_PURCHASE:
//                    NavHostFragment.findNavController().navigate();
                        break;
                    case Push.TYPE_NEWS:
//                    NavHostFragment.findNavController().navigate();
                        break;
                }
            }
        }
    }

    public static void setCurrentNavItem(final int resourceId) {
        currentNavItem = resourceId;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState(currentNavItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
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