package uz.alex.its.beverlee.view.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.Serializable;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.viewmodel.AuthViewModel;
import uz.alex.its.beverlee.viewmodel_factory.AuthViewModelFactory;

public class InitializationFragment extends Fragment {
    private Button signInBtn;
    private Button signUpBtn;

    public InitializationFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        signInBtn.setOnClickListener(v -> {
            signInBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));
            signInBtn.postOnAnimationDelayed(() -> NavHostFragment.findNavController(InitializationFragment.this).navigate(R.id.action_initFragment_to_inputPhoneFragment), 100);
        });

        signUpBtn.setOnClickListener(v -> {
            signUpBtn.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.bubble));
            signUpBtn.postOnAnimationDelayed(() -> NavHostFragment.findNavController(InitializationFragment.this).navigate(R.id.action_init_fragment_to_signUpFragment), 100);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private static final String TAG = InitializationFragment.class.toString();
}
