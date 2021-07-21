package uz.alex.its.beverlee.view.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.activities.MainActivity;

public class SplashFragment extends Fragment {
    private final Handler handler = new Handler();

    private TextView firstNameTextView;

    private String firstName;
    private String lastName;
    private boolean isSignUp;
    private boolean pinAssigned;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            firstName = SplashFragmentArgs.fromBundle(getArguments()).getFirstName();
            lastName = SplashFragmentArgs.fromBundle(getArguments()).getLastName();
            isSignUp = SplashFragmentArgs.fromBundle(getArguments()).getIsSignUp();
            pinAssigned = SplashFragmentArgs.fromBundle(getArguments()).getPinAssigned();
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

        firstNameTextView.setText(firstName);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler.postDelayed(() -> {
            startActivity(new Intent(requireContext(), MainActivity.class)
                    .putExtra(Constants.PIN_ASSIGNED, pinAssigned)
                    .putExtra(Constants.FIRST_NAME, firstName)
                    .putExtra(Constants.LAST_NAME, lastName)
                    .putExtra(Constants.IS_SIGN_UP, isSignUp));
            requireActivity().overridePendingTransition(0, 0);
            requireActivity().finish();
        }, 1000);
    }
}
