package uz.alex.its.beverlee.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.Country;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.CountryAdapter;
import uz.alex.its.beverlee.viewmodel.AuthViewModel;
import uz.alex.its.beverlee.viewmodel_factory.AuthViewModelFactory;

public class SignUpFragment extends Fragment {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText cityEditText;
    private EditText passwordEditText;
    private ImageView passwordEyeImageView;
    private EditText passwordRepeatedEditText;
    private ImageView passwordRepeatedEyeImageView;
    private TextView userAgreementTextView;
    private Button signUpBtn;
    private ProgressBar progressBar;

    private ConstraintLayout cardLayout;
    private NestedScrollView nestedScrollView;

    private Spinner countrySpinner;
    private CountryAdapter adapter;

    private static boolean showPassword = false;
    private static boolean showPasswordRepeated = false;

    private AuthViewModel authViewModel;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AuthViewModelFactory authViewModelFactory = new AuthViewModelFactory(requireContext());
        authViewModel = new ViewModelProvider(getViewModelStore(), authViewModelFactory).get(AuthViewModel.class);
        authViewModel.fetchCountryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        firstNameEditText = root.findViewById(R.id.first_name_edit_text);
        lastNameEditText = root.findViewById(R.id.last_name_edit_text);
        phoneEditText = root.findViewById(R.id.phone_edit_text);
        emailEditText = root.findViewById(R.id.email_edit_text);
        countrySpinner = root.findViewById(R.id.country_spinner);
        cityEditText = root.findViewById(R.id.city_edit_text);
        passwordEditText = root.findViewById(R.id.password_edit_text);
        passwordEyeImageView = root.findViewById(R.id.password_eye_image_view);
        passwordRepeatedEditText = root.findViewById(R.id.password_repeated_edit_text);
        passwordRepeatedEyeImageView = root.findViewById(R.id.password_repeated_eye_image_view);
        userAgreementTextView = root.findViewById(R.id.user_agreement_text_view);
        signUpBtn = root.findViewById(R.id.sign_up_button);
        progressBar = root.findViewById(R.id.progress_bar);

        nestedScrollView = root.findViewById(R.id.scroll_layout);
        cardLayout = root.findViewById(R.id.card_layout);

        //populate
        adapter = new CountryAdapter(requireContext(), R.layout.view_holder_country, R.id.country_name_text_view);
        countrySpinner.setAdapter(adapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstNameEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(firstNameEditText, hasFocus, R.string.first_name_hint));
        lastNameEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(lastNameEditText, hasFocus, R.string.last_name_hint));
        phoneEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(phoneEditText, hasFocus, R.string.phone_hint));
        emailEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(emailEditText, hasFocus, R.string.email_hint));
        cityEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(cityEditText, hasFocus, R.string.city));
        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(passwordEditText, hasFocus, R.string.password_hint));
        passwordRepeatedEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(passwordRepeatedEditText, hasFocus, R.string.password_hint));

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

        passwordRepeatedEyeImageView.setOnClickListener(v -> {
            if (showPasswordRepeated) {
                passwordRepeatedEyeImageView.setImageResource(R.drawable.ic_eye);
                passwordRepeatedEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordRepeated = false;
            }
            else {
                passwordRepeatedEyeImageView.setImageResource(R.drawable.ic_eye_purple);
                passwordRepeatedEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordRepeated = true;
            }
        });

        userAgreementTextView.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.beverlee_web_url))));
        });

        signUpBtn.setOnClickListener(v -> {
            final String firstName = firstNameEditText.getText().toString().trim();
            final String lastName = lastNameEditText.getText().toString().trim();
            final String phone = phoneEditText.getText().toString().trim();
            final String email = emailEditText.getText().toString().trim();
            final String city = cityEditText.getText().toString().trim();
            final String password = passwordEditText.getText().toString().trim();
            final String passwordConfirmation = passwordRepeatedEditText.getText().toString().trim();

            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(requireContext(), "Введите имя", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(requireContext(), "Введите фамилию", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(requireContext(), "Введите номер телефона", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(requireContext(), "Введите email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(city)) {
                Toast.makeText(requireContext(), "Введите город", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(passwordConfirmation)) {
                Toast.makeText(requireContext(), "Подтвердите пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(passwordConfirmation)) {
                Toast.makeText(requireContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }

            final long countryId = ((Country) countrySpinner.getSelectedItem()).getId();

            signUpBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));

            authViewModel.signUp(firstName, lastName, phone, email, countryId, city, password, passwordConfirmation);
        });

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        authViewModel.getCountryList().observe(getViewLifecycleOwner(), countryList -> {
            adapter.setCountryList(countryList);
            adapter.notifyDataSetChanged();
        });

        authViewModel.getSignUpResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                progressBar.setVisibility(View.GONE);
                signUpBtn.setEnabled(true);
                signUpBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_purple, null));
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                progressBar.setVisibility(View.GONE);
                signUpBtn.setEnabled(true);
                signUpBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_purple, null));

                SharedPrefs.getInstance(requireContext()).putString(Constants.BEARER_TOKEN, workInfo.getOutputData().getString(Constants.BEARER_TOKEN));

                final SignUpFragmentDirections.ActionSignUpFragmentToInputSmsFragment action = SignUpFragmentDirections.actionSignUpFragmentToInputSmsFragment();
                action.setPhone(workInfo.getOutputData().getString(Constants.PHONE));
                NavHostFragment.findNavController(SignUpFragment.this).navigate(action);

                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            signUpBtn.setEnabled(false);
            signUpBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_locked, null));
        });
    }

    private static final String TAG = SignUpFragment.class.toString();
}
