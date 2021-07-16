package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.viewmodel.AuthViewModel;
import uz.alex.its.beverlee.viewmodel.factory.AuthViewModelFactory;

public class InitializationFragment extends Fragment {
    private Button signInBtn;
    private Button signUpBtn;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;
    private String googleToken;

    public InitializationFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AuthViewModelFactory authFactory = new AuthViewModelFactory(requireContext());
        authViewModel = new ViewModelProvider(getViewModelStore(), authFactory).get(AuthViewModel.class);

        authViewModel.obtainFcmToken();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_initialization, container, false);

        signInBtn = root.findViewById(R.id.sign_in_button);
        signUpBtn = root.findViewById(R.id.sign_up_button);
        progressBar = root.findViewById(R.id.progress_bar);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signInBtn.setOnClickListener(v -> {
            if (!authViewModel.isGoogleServiceAvailable(requireContext())) {
                Toast.makeText(requireContext(), "Приложение не может функционировать полноценно без наличия Google Сервисов", Toast.LENGTH_LONG).show();
            }
            signInBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));
            signInBtn.postOnAnimationDelayed(() -> NavHostFragment.findNavController(InitializationFragment.this)
                    .navigate(InitializationFragmentDirections
                            .actionInitFragmentToInputPhoneFragment()
                            .setGoogleToken(googleToken)), 100);
        });

        signUpBtn.setOnClickListener(v -> {
            if (!authViewModel.isGoogleServiceAvailable(requireContext())) {
                Toast.makeText(requireContext(), "Приложение не может функционировать полноценно без наличия Google Сервисов", Toast.LENGTH_LONG).show();
            }
            signUpBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));
            signUpBtn.postOnAnimationDelayed(() -> NavHostFragment.findNavController(InitializationFragment.this)
                    .navigate(InitializationFragmentDirections
                            .actionInitFragmentToSignUpFragment()
                            .setGoogleToken(googleToken)), 100);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        authViewModel.obtainFcmTokenResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                googleToken = workInfo.getOutputData().getString(Constants.FCM_TOKEN);

                progressBar.setVisibility(View.GONE);
                signInBtn.setEnabled(true);
                signUpBtn.setEnabled(true);
                signInBtn.setBackgroundResource(R.drawable.btn_purple);
                signUpBtn.setBackgroundResource(R.drawable.btn_transparent);
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                progressBar.setVisibility(View.GONE);
                signInBtn.setEnabled(false);
                signUpBtn.setEnabled(false);
                signInBtn.setBackgroundResource(R.drawable.btn_locked);
                signUpBtn.setBackgroundResource(R.drawable.btn_locked);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            signInBtn.setEnabled(false);
            signUpBtn.setEnabled(false);
            signInBtn.setBackgroundResource(R.drawable.btn_locked);
            signUpBtn.setBackgroundResource(R.drawable.btn_locked);
        });
    }

    private static final String TAG = InitializationFragment.class.toString();
}
