package uz.alex.its.beverlee.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.viewmodel.PinViewModel;
import uz.alex.its.beverlee.viewmodel_factory.PinViewModelFactory;

public class ChangePinFragment extends Fragment {
    private ImageView backArrowImageView;
    private EditText newPinEditText;
    private Button requestPinBySmsBtn;
    private Button requestPinByCallBtn;
    private Button submitBtn;
    private ProgressBar progressBar;

    private PinViewModel pinViewModel;

    public ChangePinFragment() {
        // Required empty public constructor
    }
    public static ChangePinFragment newInstance() {
        ChangePinFragment fragment = new ChangePinFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PinViewModelFactory pinFactory = new PinViewModelFactory(requireContext());
        pinViewModel = new ViewModelProvider(getViewModelStore(), pinFactory).get(PinViewModel.class);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(ChangePinFragment.this).popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_change_pin, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        newPinEditText = root.findViewById(R.id.new_pin_edit_text);
        requestPinBySmsBtn = root.findViewById(R.id.request_pin_by_sms_btn);
        requestPinByCallBtn = root.findViewById(R.id.request_pin_by_call_btn);
        submitBtn = root.findViewById(R.id.submit_btn);
        progressBar = root.findViewById(R.id.progress_bar);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backArrowImageView.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        newPinEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(newPinEditText, hasFocus, R.string.password_hint));
        requestPinBySmsBtn.setOnClickListener(v -> pinViewModel.changePinBySms());
        requestPinByCallBtn.setOnClickListener(v -> pinViewModel.changePinByCall());

        submitBtn.setOnClickListener(v -> {
            submitBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));

            final String newPin = newPinEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newPin)) {
                Toast.makeText(requireContext(), "Заполните поле", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isDigitsOnly(newPin)) {
                Toast.makeText(requireContext(), "Введите только цифры", Toast.LENGTH_SHORT).show();
                return;
            }
            pinViewModel.verifyPin(newPin);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        pinViewModel.getVerifyPinResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Toast.makeText(requireContext(), "Pin код подтвержден успешно", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
                progressBar.setVisibility(View.GONE);
                submitBtn.setEnabled(true);
                requestPinBySmsBtn.setEnabled(true);
                requestPinByCallBtn.setEnabled(true);
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                submitBtn.setEnabled(true);
                requestPinBySmsBtn.setEnabled(true);
                requestPinByCallBtn.setEnabled(true);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            submitBtn.setEnabled(false);
            requestPinBySmsBtn.setEnabled(false);
            requestPinByCallBtn.setEnabled(false);
        });
    }
}