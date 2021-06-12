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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.core.widgets.Rectangle;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkInfo;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.requestParams.VerifyTransferParams;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel_factory.TransactionViewModelFactory;

public class VerifyTransactionDialog extends DialogFragment {
    private EditText transferCodeEditText;
    private Button submitBtn;

    private VerifyTransferParams params;

    public VerifyTransactionDialog() { }

    public static VerifyTransactionDialog newInstance(final VerifyTransferParams params) {
        VerifyTransactionDialog dialog = new VerifyTransactionDialog();
        Bundle args = new Bundle();
        args.putSerializable(Constants.VERIFY_TRANSFER_PARAMS, params);
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
            this.params = (VerifyTransferParams) getArguments().getSerializable(Constants.VERIFY_TRANSFER_PARAMS);
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

        transferCodeEditText = view.findViewById(R.id.transfer_code_edit_text);
        submitBtn = view.findViewById(R.id.submit_btn);

        submitBtn.setOnClickListener(v -> {
            submitBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));

            final String code = transferCodeEditText.getText().toString().trim();

            if (TextUtils.isEmpty(code)) {
                Toast.makeText(requireContext(), R.string.error_transfer_empty_code, Toast.LENGTH_SHORT).show();
                return;
            }
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent()
                    .putExtra(Constants.PINCODE, code)
                    .putExtra(Constants.RECIPIENT_ID, params.getRecipientId())
                    .putExtra(Constants.AMOUNT, params.getAmount())
                    .putExtra(Constants.NOTE, params.getNote()));
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

    private static final String TAG = VerifyTransactionDialog.class.toString();
}
