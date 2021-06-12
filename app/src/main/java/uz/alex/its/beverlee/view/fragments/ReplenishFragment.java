package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.view.UiUtils;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class ReplenishFragment extends Fragment {
    private ImageView backArrowImageView;
    private EditText amountEditText;
    private TextView amountWithCommissionTextView;
    private CircularProgressButton replenishBtn;

    private NetworkConnectivity networkConnectivity;

    public ReplenishFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_replenish, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        amountEditText = root.findViewById(R.id.amount_edit_text);
        amountWithCommissionTextView = root.findViewById(R.id.amount_with_commission_text_view);
        replenishBtn = root.findViewById(R.id.replenish_btn);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UiUtils.hideBottomNav(requireActivity());

        amountWithCommissionTextView.setText(getString(R.string.amount_with_commission, 0.00));

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

        amountEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(amountEditText, hasFocus, R.string.zero);
        });

        replenishBtn.setOnClickListener(v -> {
            networkConnectivity.checkInternetConnection(isConnected -> {
                if (!isConnected) {
                    NavHostFragment.findNavController(this).navigate(
                            ReplenishFragmentDirections.actionDebitFragmentToTransactionResultFragment()
                                    .setResult(false)
                                    .setType(Constants.RESULT_TYPE_TRANSFER)
                                    .setErrorMessage(Constants.NO_INTERNET));
                    return;
                }
                //todo: do replenish
            });
        });
    }
}
