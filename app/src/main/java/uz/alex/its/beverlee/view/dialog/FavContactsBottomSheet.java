package uz.alex.its.beverlee.view.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.interfaces.DialogCallback;

public class FavContactsBottomSheet extends BottomSheet {
    private TextView bottomSheetFavContactsTransfer;
    private TextView bottomSheetRemoveFromFav;

    public FavContactsBottomSheet(final DialogCallback callback) {
        super(callback);
    }

    public static FavContactsBottomSheet newInstance(final DialogCallback callback) {
        return new FavContactsBottomSheet(callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.bottom_sheet_fav_contacts, container, false);

        bottomSheetFavContactsTransfer = root.findViewById(R.id.fav_contacts_transfer);
        bottomSheetRemoveFromFav = root.findViewById(R.id.remove_from_favorites);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetFavContactsTransfer.setOnClickListener(v -> {
            triggerOnResult(Constants.RESULT_CODE_TRANSFER_TO_FAVORITE);
            dismiss();
        });

        bottomSheetRemoveFromFav.setOnClickListener(v -> {
            triggerOnResult(Constants.RESULT_CODE_REMOVE_FROM_FAVS);
            dismiss();
        });
    }

    private static final String TAG = FavContactsBottomSheet.class.toString();
    private static final String CONTACT = "contact";
}
