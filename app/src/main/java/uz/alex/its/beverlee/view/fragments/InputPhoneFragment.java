package uz.alex.its.beverlee.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.activities.MainActivity;
import uz.alex.its.beverlee.viewmodel.AuthViewModel;
import uz.alex.its.beverlee.viewmodel.PinViewModel;
import uz.alex.its.beverlee.viewmodel_factory.AuthViewModelFactory;
import uz.alex.its.beverlee.viewmodel_factory.PinViewModelFactory;

public class InputPhoneFragment extends Fragment {
    private EditText phoneEditText;
    private EditText passwordEditText;
    private ImageView passwordEyeImageView;
    private TextView forgotPasswordTextView;
    private Button signInBtn;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private PinViewModel pinViewModel;

    private static boolean showPassword = false;

    public InputPhoneFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AuthViewModelFactory authFactory = new AuthViewModelFactory(requireContext());
        final PinViewModelFactory pinFactory = new PinViewModelFactory(requireContext());

        authViewModel = new ViewModelProvider(getViewModelStore(), authFactory).get(AuthViewModel.class);
        pinViewModel = new ViewModelProvider(getViewModelStore(), pinFactory).get(PinViewModel.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_input_phone, container, false);

        phoneEditText = root.findViewById(R.id.phone_edit_text);
        passwordEditText = root.findViewById(R.id.password_edit_text);
        passwordEyeImageView = root.findViewById(R.id.password_eye_image_view);
        forgotPasswordTextView = root.findViewById(R.id.forgot_password_text_view);
        signInBtn = root.findViewById(R.id.login_btn);
        progressBar = root.findViewById(R.id.progress_bar);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        phoneEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(phoneEditText, hasFocus, R.string.phone_hint);
        });

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(passwordEditText, hasFocus, R.string.password_hint));

        passwordEyeImageView.setOnClickListener(v -> {
            if (showPassword) {
                passwordEyeImageView.setImageResource(R.drawable.ic_eye);
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPassword = false;
            }
            else {
                passwordEyeImageView.setImageResource(R.drawable.ic_eye_purple);
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPassword = true;
            }
        });

        signInBtn.setOnClickListener(v -> {
            signInBtn.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bubble));

            final String phone = phoneEditText.getText().toString().trim();

            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(requireContext(), "Введите номер телефона", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.length() <= 10) {
                Toast.makeText(requireContext(), "Неверный формат", Toast.LENGTH_SHORT).show();
                return;
            }

            final String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() <= 4) {
                Toast.makeText(requireContext(), "Введите минимум 5 символов", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.login(phone, password);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        authViewModel.getLoginResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                progressBar.setVisibility(View.GONE);
                signInBtn.setEnabled(true);
                signInBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_purple, null));
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                final boolean phoneVerified = workInfo.getOutputData().getBoolean(Constants.PHONE_VERIFIED, false);
                final String phone = workInfo.getOutputData().getString(Constants.PHONE);

                SharedPrefs.getInstance(requireContext()).putString(Constants.PHONE, phone);
                SharedPrefs.getInstance(requireContext()).putBoolean(Constants.PHONE_VERIFIED, phoneVerified);

                pinViewModel.checkPinAssigned();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            signInBtn.setEnabled(false);
            signInBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_locked, null));
        });

        pinViewModel.getCheckPinAssignedResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                progressBar.setVisibility(View.GONE);
                signInBtn.setEnabled(true);
                signInBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_purple, null));
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                progressBar.setVisibility(View.GONE);
                signInBtn.setEnabled(true);
                signInBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_purple, null));
                //user registered
                if (!TextUtils.isEmpty(SharedPrefs.getInstance(requireContext()).getString(Constants.BEARER_TOKEN))
                        && !TextUtils.isEmpty(SharedPrefs.getInstance(requireContext()).getString(Constants.PHONE))
                        && SharedPrefs.getInstance(requireContext()).getBoolean(Constants.PHONE_VERIFIED)) {
                    startActivity(new Intent(requireContext(), MainActivity.class).putExtra(Constants.PIN_ASSIGNED,
                            workInfo.getOutputData().getBoolean(Constants.PIN_ASSIGNED, false)));
                    requireActivity().overridePendingTransition(0, 0);
                    requireActivity().finish();
                    return;
                }
                NavHostFragment.findNavController(InputPhoneFragment.this)
                        .navigate(InputPhoneFragmentDirections
                                .actionInputPhoneFragmentToInputSmsFragment()
                                .setPhone(SharedPrefs.getInstance(requireContext()).getString(Constants.PHONE)));
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            signInBtn.setEnabled(false);
            signInBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_locked, null));
        });
    }

    private static final String TAG = InputPhoneFragment.class.toString();
}
