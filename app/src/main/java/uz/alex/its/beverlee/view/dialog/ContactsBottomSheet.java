package uz.alex.its.beverlee.view.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.interfaces.DialogCallback;

public class ContactsBottomSheet extends BottomSheet {
    private TextView bottomSheetContactsTransfer;
    private TextView bottomSheetAddToFavs;
    private TextView bottomSheetDelete;

    public ContactsBottomSheet(final DialogCallback callback) {
        super(callback);
    }

    public static ContactsBottomSheet newInstance(final DialogCallback callback) {
        return new ContactsBottomSheet(callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.bottom_sheet_contacts, container, false);

        bottomSheetContactsTransfer = root.findViewById(R.id.contacts_transfer);
        bottomSheetAddToFavs = root.findViewById(R.id.add_to_favorites);
        bottomSheetDelete = root.findViewById(R.id.delete_contact);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetContactsTransfer.setOnClickListener(v -> {
            triggerOnResult(Constants.RESULT_CODE_TRANSFER_TO_CONTACT);
            dismiss();
        });

        bottomSheetAddToFavs.setOnClickListener(v -> {
            triggerOnResult(Constants.RESULT_CODE_ADD_TO_FAVS);
            dismiss();
        });

        bottomSheetDelete.setOnClickListener(v -> {
            triggerOnResult(Constants.RESULT_CODE_DELETE_CONTACT);
            dismiss();
        });
    }

    private static final String TAG = ContactsBottomSheet.class.toString();
    private static final String CONTACT = "contact";
}
