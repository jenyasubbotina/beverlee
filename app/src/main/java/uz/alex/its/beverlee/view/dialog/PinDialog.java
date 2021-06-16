package uz.alex.its.beverlee.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.core.widgets.Rectangle;
import androidx.fragment.app.DialogFragment;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.requestParams.MakePurchaseParams;
import uz.alex.its.beverlee.model.requestParams.VerifyTransferParams;
import uz.alex.its.beverlee.utils.Constants;

public class PinDialog extends DialogFragment {
    private EditText pinEditText;
    private Button submitBtn;

    private int action;
    private long requestId;
    private VerifyTransferParams verifyTransferParams;

    public PinDialog() { }

    public static PinDialog newInstance(final VerifyTransferParams params) {
        PinDialog dialog = new PinDialog();
        Bundle args = new Bundle();
        args.putSerializable(Constants.VERIFY_TRANSFER_PARAMS, params);
        args.putInt(ACTION, ACTION_VERIFY_TRANSFER);
        dialog.setArguments(args);
        return dialog;
    }

    public static PinDialog newInstance(final long requestId) {
        PinDialog dialog = new PinDialog();
        Bundle args = new Bundle();
        args.putLong(Constants.REQUEST_ID, requestId);
        args.putInt(ACTION, ACTION_VERIFY_PURCHASE);
        dialog.setArguments(args);
        return dialog;
    }

    private void setDimensions(final int percentage, final Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final Rectangle rect = new Rectangle();
        rect.setBounds(0, 0, metrics.widthPixels, metrics.heightPixels);
        final float width = rect.width * (float) percentage / 100;
        getDialog().getWindow().setLayout((int) width, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.action = getArguments().getInt(ACTION);
            this.requestId = getArguments().getLong(Constants.REQUEST_ID);
            this.verifyTransferParams = (VerifyTransferParams) getArguments().getSerializable(Constants.VERIFY_TRANSFER_PARAMS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_verify_transfer, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pinEditText = view.findViewById(R.id.transfer_code_edit_text);
        submitBtn = view.findViewById(R.id.submit_btn);

        submitBtn.setOnClickListener(v -> {
            submitBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));

            final String code = pinEditText.getText().toString().trim();

            if (TextUtils.isEmpty(code)) {
                Toast.makeText(requireContext(), R.string.error_transfer_empty_code, Toast.LENGTH_SHORT).show();
                return;
            }
            if (getTargetFragment() == null) {
                Log.e(TAG, "onViewCreated(): targetFragment is NULL");
                return;
            }
            if (action == 1) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent()
                        .putExtra(Constants.PINCODE, code)
                        .putExtra(Constants.RECIPIENT_ID, verifyTransferParams.getRecipientId())
                        .putExtra(Constants.AMOUNT, verifyTransferParams.getAmount())
                        .putExtra(Constants.NOTE, verifyTransferParams.getNote()));
            }
            else if (action == 2) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent()
                        .putExtra(Constants.PINCODE, code)
                        .putExtra(Constants.REQUEST_ID, requestId));
            }
            dismiss();
        });
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDimensions(90, requireContext());
    }

    private static final String TAG = PinDialog.class.toString();
    private static final String ACTION = "action";
    private static final int ACTION_VERIFY_TRANSFER = 1;
    private static final int ACTION_VERIFY_PURCHASE = 2;
}
