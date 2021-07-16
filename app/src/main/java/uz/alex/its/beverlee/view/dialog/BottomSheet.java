package uz.alex.its.beverlee.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import uz.alex.its.beverlee.view.interfaces.DialogCallback;

public class BottomSheet extends BottomSheetDialogFragment {
    private final DialogCallback callback;

    public BottomSheet(final DialogCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void triggerOnResult(final int resultCode) {
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, new Intent());
        }
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        callback.onDismiss();
    }
}
