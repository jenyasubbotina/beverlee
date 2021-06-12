package uz.alex.its.beverlee.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class TransactionResultFragment extends Fragment {
    private TextView transactionResultHeadingTextView;
    private TextView transactionResultTextView;
    private ImageView transactionResultImageView;
    private Button returnBtn;

    private Animation bubbleAnimation;

    private boolean result;
    private String type;
    private String errorMessage;

    private NetworkConnectivity networkConnectivity;

    public TransactionResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requireActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
        }
        if (getArguments() != null) {
            this.result = TransactionResultFragmentArgs.fromBundle(getArguments()).getResult();
            this.type = TransactionResultFragmentArgs.fromBundle(getArguments()).getType();
            this.errorMessage = TransactionResultFragmentArgs.fromBundle(getArguments()).getErrorMessage();
        }
        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_transaction_result, container, false);

        transactionResultHeadingTextView = root.findViewById(R.id.transaction_result_heading_text_view);
        transactionResultTextView = root.findViewById(R.id.transaction_result_text_view);
        transactionResultImageView = root.findViewById(R.id.transaction_result_image_view);
        returnBtn = root.findViewById(R.id.return_btn);

        bubbleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bubble);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (type.equalsIgnoreCase(Constants.RESULT_TYPE_TRANSFER)) {
            transactionResultHeadingTextView.setText(R.string.transfer);
        }
        else if (type.equalsIgnoreCase(Constants.RESULT_TYPE_WITHDRAWAL)) {
            transactionResultHeadingTextView.setText(R.string.withdrawal);
        }
        else if (type.equalsIgnoreCase(Constants.RESULT_TYPE_REPLENISH)) {
            transactionResultHeadingTextView.setText(R.string.replenish);
        }
        else if (type.equalsIgnoreCase(Constants.RESULT_TYPE_PROFILE)) {
            transactionResultHeadingTextView.setText(R.string.profile);
        }
        else if (type.equalsIgnoreCase(Constants.RESULT_TYPE_CONTACTS)) {
            transactionResultHeadingTextView.setText(R.string.contacts);
        }
        if (result) {
            transactionResultTextView.setText(R.string.successful_transaction);
            transactionResultTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGreen, null));
            transactionResultImageView.setImageResource(R.drawable.ic_check);
            returnBtn.setOnClickListener(v -> toMain());
        }
        else {
            if (errorMessage.equalsIgnoreCase(Constants.NO_INTERNET)) {
                transactionResultTextView.setText(R.string.error_check_internet);
                returnBtn.setText(R.string.reload_app);
                returnBtn.setOnClickListener(v -> reloadApp());
            }
            else {
                transactionResultTextView.setText(R.string.failed_transaction);
                returnBtn.setText(R.string.to_main);
                returnBtn.setOnClickListener(v -> toMain());
            }
            transactionResultTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, null));
            transactionResultImageView.setImageResource(R.drawable.ic_cross);
        }
    }

    private void toMain() {
        returnBtn.startAnimation(bubbleAnimation);
        NavHostFragment.findNavController(this).navigate(R.id.action_transactionResultFragment_to_homeFragment);
    }

    private void reloadApp() {
        networkConnectivity.checkInternetConnection(isConnected -> {
            if (!isConnected) {
                Toast.makeText(requireContext(), R.string.error_check_internet, Toast.LENGTH_SHORT).show();
                return;
            }
            returnBtn.startAnimation(bubbleAnimation);
            Intent intent = requireContext().getPackageManager().getLaunchIntentForPackage(requireContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}