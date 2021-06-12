package uz.alex.its.beverlee.view.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.DeviceDisplay;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.viewmodel.UserViewModel;
import uz.alex.its.beverlee.viewmodel_factory.UserViewModelFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ChangePasswordFragment extends Fragment {
    private ImageView backArrowImageView;
    private TextView errorTextView;
    private ProgressBar progressBar;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText newPasswordRepeatedEditText;

    private ImageView newPasswordEye;
    private ImageView newPasswordRepeatedEye;

    private Button saveBtn;

    private boolean showNewPassword = false;
    private boolean showNewRepeatedPassword = false;
    private boolean keyboardIsShown = false;

    private Animation bubbleAnimation;

    private NestedScrollView nestedScrollView;

    //to handle keyboard popup
    private View parentLayout;
    private RelativeLayout confirmPasswordField;

    private UserViewModel userViewModel;

    private NetworkConnectivity networkConnectivity;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());

        final UserViewModelFactory userFactory = new UserViewModelFactory(requireContext());
        userViewModel = new ViewModelProvider(getViewModelStore(), userFactory).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_change_password, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        errorTextView = root.findViewById(R.id.change_password_error_text_view);
        oldPasswordEditText = root.findViewById(R.id.old_password_edit_text);
        newPasswordEditText = root.findViewById(R.id.new_password_edit_text);
        newPasswordRepeatedEditText = root.findViewById(R.id.confirm_new_password_edit_text);
        newPasswordEye = root.findViewById(R.id.new_password_eye_image_view);
        newPasswordRepeatedEye = root.findViewById(R.id.confirm_new__password_eye_image_view);
        progressBar = root.findViewById(R.id.progress_bar);
        saveBtn = root.findViewById(R.id.save_btn);

        bubbleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bubble);

        nestedScrollView = root.findViewById(R.id.scroll_layout);

        parentLayout = root;
        confirmPasswordField = root.findViewById(R.id.confirm_new_password_field);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backArrowImageView.setOnClickListener(v -> {
//            if (requireActivity().getCurrentFocus() == null) {
//                NavHostFragment.findNavController(this).popBackStack();
//                return;
//            }
//            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
//            requireActivity().getCurrentFocus().clearFocus();
            NavHostFragment.findNavController(this).popBackStack();
        });

        oldPasswordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                oldPasswordEditText.setBackgroundResource(R.drawable.edit_text_active);
                oldPasswordEditText.setHint("");
                return;
            }
            if (oldPasswordEditText.getText().length() > 0) {
                oldPasswordEditText.setBackgroundResource(R.drawable.edit_text_filled);
                oldPasswordEditText.setHint("");
                return;
            }
            oldPasswordEditText.setBackgroundResource(R.drawable.edit_text_locked);
            oldPasswordEditText.setHint(R.string.password_hint);
        });

        newPasswordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                newPasswordEditText.setBackgroundResource(R.drawable.edit_text_active);
                newPasswordEditText.setHint("");
                return;
            }
            if (newPasswordEditText.getText().length() > 0) {
                newPasswordEditText.setBackgroundResource(R.drawable.edit_text_filled);
                newPasswordEditText.setHint("");
                return;
            }
            newPasswordEditText.setBackgroundResource(R.drawable.edit_text_locked);
            newPasswordEditText.setHint(R.string.password_hint);
        });

        newPasswordEye.setOnClickListener(v -> {
            if (showNewPassword) {
                newPasswordEye.setImageResource(R.drawable.ic_eye);
                newPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showNewPassword = false;
            }
            else {
                newPasswordEye.setImageResource(R.drawable.ic_eye_purple);
                newPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showNewPassword = true;
            }
        });

        newPasswordRepeatedEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                nestedScrollView.postDelayed(() -> {
                    nestedScrollView.smoothScrollTo(0, saveBtn.getBottom());
                }, 100);
                newPasswordRepeatedEditText.setBackgroundResource(R.drawable.edit_text_active);
                newPasswordRepeatedEditText.setHint("");
                return;
            }

            if (newPasswordRepeatedEditText.getText().length() > 0) {
                newPasswordRepeatedEditText.setBackgroundResource(R.drawable.edit_text_filled);
                newPasswordRepeatedEditText.setHint("");
                return;
            }
            newPasswordRepeatedEditText.setBackgroundResource(R.drawable.edit_text_locked);
            newPasswordRepeatedEditText.setHint(R.string.password_hint);
        });

        newPasswordRepeatedEye.setOnClickListener(v -> {
            if (showNewRepeatedPassword) {
                newPasswordRepeatedEye.setImageResource(R.drawable.ic_eye);
                newPasswordRepeatedEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showNewRepeatedPassword = false;
            }
            else {
                newPasswordRepeatedEye.setImageResource(R.drawable.ic_eye_purple);
                newPasswordRepeatedEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showNewRepeatedPassword = true;
            }
        });

        saveBtn.setOnClickListener(v -> {
            saveBtn.startAnimation(bubbleAnimation);

            final String oldPassword = oldPasswordEditText.getText().toString().trim();
            final String newPassword = newPasswordEditText.getText().toString().trim();
            final String newPasswordConfirm = newPasswordRepeatedEditText.getText().toString().trim();

            if (TextUtils.isEmpty(oldPassword)) {
                errorTextView.setText(R.string.error_old_password_empty);
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }
            if (TextUtils.isEmpty(newPassword)) {
                errorTextView.setText(R.string.error_new_password_empty);
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }
            if (TextUtils.isEmpty(newPasswordConfirm)) {
                errorTextView.setText(R.string.error_new_password_confirm_empty);
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }
            if (!newPassword.equals(newPasswordConfirm)) {
                errorTextView.setText(R.string.error_password_mismatch_empty);
                errorTextView.setVisibility(View.VISIBLE);
                return;
            }
            networkConnectivity.checkInternetConnection(isConnected -> {
                if (!isConnected) {
                    NavHostFragment.findNavController(this).navigate(
                            ChangePasswordFragmentDirections.actionChangePasswordFragmentToTransactionResultFragment()
                                    .setResult(false)
                                    .setType(Constants.RESULT_TYPE_PROFILE)
                                    .setErrorMessage(Constants.NO_INTERNET));
                    return;
                }
                userViewModel.changePassword(oldPassword, newPassword, newPasswordConfirm);
            });

        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel.getChangePasswordResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                errorTextView.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                Toast.makeText(requireContext(), R.string.success_data_safe, Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(this).popBackStack();

                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(workInfo.getOutputData().getString(Constants.REQUEST_ERROR));
                saveBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            errorTextView.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private static final String TAG = ChangePasswordFragment.class.toString();
}