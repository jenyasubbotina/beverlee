package uz.alex.its.beverlee.view.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.viewmodel.PinViewModel;
import uz.alex.its.beverlee.viewmodel_factory.PinViewModelFactory;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class PinFragment extends Fragment {

    View v;

    private static volatile boolean pinAssigned;

    private PinViewModel pinViewModel;
    private NetworkConnectivity networkConnectivity;

    private TextView pinTextView, pinErrorTextView;

    private PinLockView pinLockView;
    private IndicatorDots pinIndicator;
    private PinLockListener mPinLockListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requireActivity().getIntent() != null) {
            pinAssigned = requireActivity().getIntent().getBooleanExtra(Constants.PIN_ASSIGNED, false);
        }
        else {
            pinAssigned = true;
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

        pinTextView = v.findViewById(R.id.pin_text_view);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pinViewModel.getAssignPinResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                SharedPrefs.getInstance(requireContext()).putBoolean(Constants.PIN_ASSIGNED, true);
                pinAssigned = true;

                pinErrorTextView.setVisibility(View.INVISIBLE);
                pinTextView.setText(R.string.repeat_pin);

                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                SharedPrefs.getInstance(requireContext()).putBoolean(Constants.PIN_ASSIGNED, false);
                pinErrorTextView.setText(R.string.error_pin_asign);
                pinErrorTextView.setVisibility(View.VISIBLE);
            }
        });

        pinViewModel.getVerifyPinResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                NavHostFragment.findNavController(this).navigate(PinFragmentDirections.actionPinFragmentToHomeFragment());
                pinErrorTextView.setVisibility(View.INVISIBLE);
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                pinErrorTextView.setText(R.string.error_wrong_pin);
                pinErrorTextView.setVisibility(View.VISIBLE);
                pinLockView.resetPinLockView();
                return;
            }
            pinErrorTextView.setVisibility(View.INVISIBLE);
        });

    }
}
