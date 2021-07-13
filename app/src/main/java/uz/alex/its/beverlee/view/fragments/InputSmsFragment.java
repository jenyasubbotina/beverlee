package uz.alex.its.beverlee.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkInfo;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.VerifyPhoneCounterTask;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.activities.MainActivity;
import uz.alex.its.beverlee.viewmodel.AuthViewModel;
import uz.alex.its.beverlee.viewmodel.factory.AuthViewModelFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class InputSmsFragment extends Fragment {

    private AuthViewModel authViewModel;

    private TextView openInstructionTextView;
    private TextView instructionTextView;
    private TextView counterTextView;
    private EditText phoneEditText;
    private EditText verificationCodeEditText;
    private Button sendAgainBtn;
    private Button callBtn;

    private Animation bubbleAnimation;

    private VerifyPhoneCounterTask verifyPhoneCounterTask;

    private String phone, firstName, lastName;
    private boolean isSignUp;

    public InputSmsFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.phone = InputSmsFragmentArgs.fromBundle(getArguments()).getPhone();
            this.firstName = InputSmsFragmentArgs.fromBundle(getArguments()).getFirstName();
            this.lastName = InputSmsFragmentArgs.fromBundle(getArguments()).getLastName();
            this.isSignUp = InputSmsFragmentArgs.fromBundle(getArguments()).getSignUp();
        }
        if (requireActivity().getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
                requireActivity().getCurrentFocus().clearFocus();
        }
        final AuthViewModelFactory factory = new AuthViewModelFactory(requireContext());
        authViewModel = new ViewModelProvider(getViewModelStore(), factory).get(AuthViewModel.class);
        authViewModel.verifyPhoneBySms();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_input_sms, container, false);

        openInstructionTextView = root.findViewById(R.id.open_verification_instruction_text_view);
        instructionTextView = root.findViewById(R.id.verification_instruction_text_view);
        phoneEditText = root.findViewById(R.id.phone_edit_text);
        verificationCodeEditText = root.findViewById(R.id.verification_code_edit_text);
        counterTextView = root.findViewById(R.id.counter_text_view);
        sendAgainBtn = root.findViewById(R.id.send_again_btn);
        callBtn = root.findViewById(R.id.call_btn);

        bubbleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bubble);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (verifyPhoneCounterTask != null) {
            if (!verifyPhoneCounterTask.isCancelled()) {
                verifyPhoneCounterTask.cancel(true);
            }
            verifyPhoneCounterTask = null;
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verifyPhoneCounterTask = new VerifyPhoneCounterTask(getResources(), counterTextView, sendAgainBtn, callBtn);
        verifyPhoneCounterTask.execute();

        phoneEditText.setText(phone);
        phoneEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(phoneEditText, hasFocus, R.string.phone_hint));

        verificationCodeEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(verificationCodeEditText, hasFocus, R.string.enter_code));
        verificationCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    authViewModel.submitVerification(s.toString());
                }
            }
        });

        openInstructionTextView.setOnClickListener(v -> {
            if (instructionTextView.getVisibility() == View.VISIBLE) {
                instructionTextView.setVisibility(View.GONE);
            }
            else if (instructionTextView.getVisibility() == View.GONE) {
                instructionTextView.setVisibility(View.VISIBLE);
            }
        });

        sendAgainBtn.setOnClickListener(v -> {
            sendAgainBtn.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bubble));

            authViewModel.verifyPhoneBySms();
            new VerifyPhoneCounterTask(getResources(), counterTextView, sendAgainBtn, callBtn).execute();
        });

        callBtn.setOnClickListener(v -> {
            callBtn.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bubble));

            authViewModel.verifyPhoneByCall();
            new VerifyPhoneCounterTask(getResources(), counterTextView, sendAgainBtn, callBtn).execute();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        authViewModel.getSubmitVerificationResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();

                if (verifyPhoneCounterTask != null) {
                    if (!verifyPhoneCounterTask.isCancelled()) {
                        verifyPhoneCounterTask.cancel(true);
                    }
                    verifyPhoneCounterTask = null;
                }
                return;
            }
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                if (verifyPhoneCounterTask != null) {
                    if (!verifyPhoneCounterTask.isCancelled()) {
                        verifyPhoneCounterTask.cancel(true);
                    }
                    verifyPhoneCounterTask = null;
                }
                SharedPrefs.getInstance(requireContext()).putString(Constants.PHONE, phone);
                SharedPrefs.getInstance(requireContext()).putBoolean(Constants.PHONE_VERIFIED, true);

                startActivity(new Intent(requireActivity(), MainActivity.class)
                        .putExtra(Constants.PIN_ASSIGNED, false)
                        .putExtra(Constants.FIRST_NAME, firstName)
                        .putExtra(Constants.LAST_NAME, lastName)
                        .putExtra(Constants.IS_SIGN_UP, isSignUp));
                requireActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private static final String TAG = InputSmsFragment.class.toString();
}
