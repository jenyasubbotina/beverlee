package uz.alex.its.beverlee.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private static PermissionManager instance;

    private PermissionManager() {
    }

    public static PermissionManager getInstance() {
        if (instance == null) {
            synchronized (PermissionManager.class) {
                if (instance == null) {
                    instance = new PermissionManager();
                }
            }
        }
        return instance;
    }

    public boolean permissionsGranted(final Context context, final String[] appPermissions, final int REQUEST_CODE) {
        List<String> permissionsRequired = new ArrayList<>();

        for (String permission : appPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsRequired.add(permission);
            }
        }
        return permissionsRequired.isEmpty();
    }

    public void requestPermissions(final Activity activity, final String[] appPermissions, final int REQUEST_CODE)  {
        ActivityCompat.requestPermissions(activity, appPermissions, REQUEST_CODE);
    }


    public AlertDialog showExplanationDialog(final Context context,
                                             final String title,
                                             final String msg,
                                             final String positiveLabel,
                                             final DialogInterface.OnClickListener positiveOnClick,
                                             final String negativeLabel,
                                             final DialogInterface.OnClickListener negativeOnClick,
                                             final boolean isCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(isCancelable);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);
        return builder.create();
    }

    public void openSettingsDialog(final Context context, final String msg) {
        showExplanationDialog(context, "Grant Required Permission", msg,
                "Open Settings", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    final Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                },"Cancel", (dialogInterface, i) -> dialogInterface.dismiss(),  false).show();
    }

    private static final String TAG = "RuntimePermissionChecker.class";
}
