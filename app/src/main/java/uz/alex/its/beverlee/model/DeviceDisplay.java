package uz.alex.its.beverlee.model;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

public class DeviceDisplay {
    private static DeviceDisplay INSTANCE;

    private final int screenHeight;
    private final int screenWidth;

    public DeviceDisplay(final FragmentActivity activity) {
        final Display display = activity.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getRealSize(size);
        this.screenWidth = size.x;
        this.screenHeight = size.y;
    }

    public static DeviceDisplay getInstance(final FragmentActivity activity) {
        if (INSTANCE == null) {
            synchronized (DeviceDisplay.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeviceDisplay(activity);
                }
            }
        }
        return INSTANCE;
    }

    public int getKeyboardHeight(final View parentLayout) {
        Rect r = new Rect();
        parentLayout.getWindowVisibleDisplayFrame(r);
        int screenHeight = parentLayout.getRootView().getHeight();
        return screenHeight - (r.bottom);
    }

    public int pxToDp(final Context context, final int px) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int dpToPx(final Context context, final int dp) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
}
