package uz.alex.its.beverlee.view;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uz.alex.its.beverlee.R;

import static android.view.View.GONE;

public class UiUtils {
    public static void setFocusChange(final EditText editText, final boolean hasFocus, final int hintResId) {
        if (hasFocus) {
            editText.setBackgroundResource(R.drawable.edit_text_active);
            editText.setHint("");
            return;
        }
        if (editText.getText().length() > 0) {
            editText.setBackgroundResource(R.drawable.edit_text_filled);
            editText.setHint("");
            return;
        }
        editText.setBackgroundResource(R.drawable.edit_text_locked);
        editText.setHint(hintResId);
    }

    public static int dp2px(final Resources resource, final int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,resource.getDisplayMetrics());
    }

    public static float px2dp(final Resources resource, final float px)  {
        return (float)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, resource.getDisplayMetrics());
    }

    public static void hideBottomNav(final FragmentActivity fragmentActivity) {
        final BottomNavigationView bottomNavigationView = fragmentActivity.findViewById(R.id.bottom_nav);
        final FloatingActionButton fab = fragmentActivity.findViewById(R.id.floating_btn);
        bottomNavigationView.setVisibility(GONE);
        fab.setVisibility(GONE);
    }
}
