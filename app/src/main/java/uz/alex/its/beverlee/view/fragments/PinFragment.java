package uz.alex.its.beverlee.view.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.viewmodel.PinViewModel;
import uz.alex.its.beverlee.viewmodel.factory.PinViewModelFactory;

public class PinFragment extends Fragment {

    View v;

    private static volatile boolean pinAssigned;
    private static volatile boolean isSignUp;
    private String firstName, lastName;

    private PinViewModel pinViewModel;
    private NetworkConnectivity networkConnectivity;

    private TextView pinTextView, pinErrorTextView, changePinTextView;

    private PinLockView pinLockView;
    private IndicatorDots pinIndicator;
    private PinLockListener mPinLockListener;

    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (requireActivity().getIntent() != null) {
            pinAssigned = requireActivity().getIntent().getBooleanExtra(Constants.PIN_ASSIGNED, false);
            isSignUp = requireActivity().getIntent().getBooleanExtra(Constants.IS_SIGN_UP, false);
            firstName = requireActivity().getIntent().getStringExtra(Constants.FIRST_NAME);
            lastName = requireActivity().getIntent().getStringExtra(Constants.LAST_NAME);
        }
        else {
            pinAssigned = true;
            isSignUp = false;
        }

        /*init ViewModel */
        final PinViewModelFactory factory = new PinViewModelFactory(requireContext());
        pinViewModel = new ViewModelProvider(getViewModelStore(), factory).get(PinViewModel.class);

        /* internet connection checker */
        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pin2, container, false);

        progressBar = v.findViewById(R.id.progress_bar);
        pinTextView = v.findViewById(R.id.pin_text_view);
        changePinTextView = v.findViewById(R.id.change_pin_text_view);
        pinErrorTextView = v.findViewById(R.id.pin_error_text_view);

        pinLockView = v.findViewById(R.id.pin_lock_view);
        pinIndicator = v.findViewById(R.id.indicator_dots);

        pinLockView.attachIndicatorDots(pinIndicator);

        mPinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                /* if pin does not exist on server -> assign new pin -> return */
                networkConnectivity.checkInternetConnection(isConnected -> {
                    if (!isConnected) {
                        NavHostFragment.findNavController(PinFragment.this).navigate(
                                PinFragmentDirections.actionPinFragmentToTransactionResultFragment()
                                        .setResult(false)
                                        .setType(Constants.RESULT_TYPE_PROFILE)
                                        .setErrorMessage(Constants.NO_INTERNET));
                        return;
                    }
                    if (!pinAssigned) {
                        pinViewModel.assignPin(pin);
                        return;
                    }
                    pinViewModel.verifyPin(pin);
                });
            }

            @Override
            public void onEmpty() {

            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {

            }
        };

        pinLockView.setPinLockListener(mPinLockListener);

        UiUtils.hideBottomNav(requireActivity());
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (pinAssigned) {
            pinTextView.setText(R.string.enter_pin);
        }
        else {
            pinTextView.setText(R.string.create_pin);
        }
        changePinTextView.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_pinFragment_to_changePinFragment2);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pinViewModel.getAssignPinResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                SharedPrefs.getInstance(requireContext()).putBoolean(Constants.PIN_ASSIGNED, true);
                pinAssigned = true;

                progressBar.setVisibility(View.GONE);
                pinErrorTextView.setVisibility(View.INVISIBLE);
                pinTextView.setText(R.string.repeat_pin);

                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                SharedPrefs.getInstance(requireContext()).putBoolean(Constants.PIN_ASSIGNED, false);
                pinErrorTextView.setText(R.string.error_pin_asign);
                pinErrorTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
        });

        pinViewModel.getVerifyPinResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                progressBar.setVisibility(View.GONE);

                if (isSignUp) {
                    NavHostFragment.findNavController(this).navigate(
                            PinFragmentDirections.actionPinFragmentToSplashFragment()
                                    .setFullName(firstName));
                }
                else {
                    NavHostFragment.findNavController(this).navigate(PinFragmentDirections.actionPinFragmentToHomeFragment());
                }

                pinErrorTextView.setVisibility(View.INVISIBLE);
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                progressBar.setVisibility(View.GONE);
                pinErrorTextView.setText(R.string.error_wrong_pin);
                pinErrorTextView.setVisibility(View.VISIBLE);
                pinLockView.resetPinLockView();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            pinErrorTextView.setVisibility(View.INVISIBLE);
        });

    }
}
