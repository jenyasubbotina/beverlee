package uz.alex.its.beverlee.view.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uz.alex.its.beverlee.R;

public class SplashFragment extends Fragment {
    private final Handler handler = new Handler();

    private TextView firstNameTextView;

    private String fullName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            fullName = SplashFragmentArgs.fromBundle(getArguments()).getFullName();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_splash, container, false);

        firstNameTextView = root.findViewById(R.id.firstNameTextView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstNameTextView.setText(fullName);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler.postDelayed(() -> {
            NavHostFragment.findNavController(this).navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment());
        }, 1000);
    }
}
