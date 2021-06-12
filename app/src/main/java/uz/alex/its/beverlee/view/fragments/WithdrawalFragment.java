package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.Country;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel.WithdrawalType;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.CountryAdapter;
import uz.alex.its.beverlee.viewmodel.AuthViewModel;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel_factory.AuthViewModelFactory;
import uz.alex.its.beverlee.viewmodel_factory.TransactionViewModelFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class WithdrawalFragment extends Fragment {
    private ImageView backArrowImageView;

    private TextView recipientDataTextView;
    private TextView fullNameTextView;
    private TextView phoneTextView;
    private TextView countryTextView;
    private TextView cityTextView;
    private TextView cardWalletNumberTextView;

    private EditText fullNameEditText;
    private EditText phoneEditText;
    private Spinner countrySpinner;
    private EditText cityEditText;
    private EditText cardWalletNumberEditText;
    private EditText amountEditText;
    private TextView amountWithCommissionTextView;

    private CircularProgressButton withdrawBtn;

    private CountryAdapter countryAdapter;

    private WithdrawalType currentWithdrawalType;

    private TransactionViewModel transactionViewModel;
    private AuthViewModel authViewModel;

    private NetworkConnectivity networkConnectivity;

    public WithdrawalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.currentWithdrawalType = new WithdrawalType(
                    WithdrawalFragmentArgs.fromBundle(getArguments()).getType(),
                    WithdrawalFragmentArgs.fromBundle(getArguments()).getMethod(),
                    WithdrawalFragmentArgs.fromBundle(getArguments()).getCommission(),
                    null);
        }
        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());

        final TransactionViewModelFactory transactionFactory = new TransactionViewModelFactory(requireContext());
        final AuthViewModelFactory authViewModelFactory = new AuthViewModelFactory(requireContext());

        transactionViewModel = new ViewModelProvider(getViewModelStore(), transactionFactory).get(TransactionViewModel.class);
        authViewModel = new ViewModelProvider(getViewModelStore(), authViewModelFactory).get(AuthViewModel.class);

        authViewModel.fetchCountryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_withdrawal, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);

        recipientDataTextView = root.findViewById(R.id.recipient_data_text_view);
        fullNameTextView = root.findViewById(R.id.full_name_text_view);
        phoneTextView = root.findViewById(R.id.phone_text_view);
        countryTextView = root.findViewById(R.id.country_text_view);
        cityTextView = root.findViewById(R.id.city_text_view);
        cardWalletNumberTextView = root.findViewById(R.id.card_wallet_number_text_view);
        amountWithCommissionTextView = root.findViewById(R.id.amount_with_commission_text_view);

        fullNameEditText = root.findViewById(R.id.full_name_edit_text);
        phoneEditText = root.findViewById(R.id.phone_edit_text);
        countrySpinner = root.findViewById(R.id.country_spinner);
        cityEditText = root.findViewById(R.id.city_edit_text);
        cardWalletNumberEditText = root.findViewById(R.id.card_wallet_number_edit_text);
        amountEditText = root.findViewById(R.id.amount_edit_text);

        withdrawBtn = root.findViewById(R.id.withdraw_btn);

        countryAdapter = new CountryAdapter(requireContext(), R.layout.view_holder_country, R.id.country_name_text_view);
        countrySpinner.setAdapter(countryAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UiUtils.hideBottomNav(requireActivity());

        /* if transfer -> fullName, phone, country, city, amount */
        /* if electronic -> walletNumber, amount */
        /* if card -> cardNumber, amount */
        if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_transfer))) {
            fullNameTextView.setVisibility(View.VISIBLE);
            phoneTextView.setVisibility(View.VISIBLE);
            countryTextView.setVisibility(View.VISIBLE);
            cityTextView.setVisibility(View.VISIBLE);

            fullNameEditText.setVisibility(View.VISIBLE);
            phoneEditText.setVisibility(View.VISIBLE);
            countrySpinner.setVisibility(View.VISIBLE);
            cityEditText.setVisibility(View.VISIBLE);

            cardWalletNumberTextView.setVisibility(View.GONE);
            cardWalletNumberEditText.setVisibility(View.GONE);
        }
        else if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_card))) {
            fullNameTextView.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.GONE);
            countryTextView.setVisibility(View.GONE);
            cityTextView.setVisibility(View.GONE);

            fullNameEditText.setVisibility(View.GONE);
            phoneEditText.setVisibility(View.GONE);
            countrySpinner.setVisibility(View.GONE);
            cityEditText.setVisibility(View.GONE);

            cardWalletNumberTextView.setText(getString(R.string.recipient_card_number, currentWithdrawalType.getMethod()));
            cardWalletNumberEditText.setHint(getString(R.string.recipient_card_number, ""));
        }
        else if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_wallet))) {
            fullNameTextView.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.GONE);
            countryTextView.setVisibility(View.GONE);
            cityTextView.setVisibility(View.GONE);

            fullNameEditText.setVisibility(View.GONE);
            phoneEditText.setVisibility(View.GONE);
            countrySpinner.setVisibility(View.GONE);
            cityEditText.setVisibility(View.GONE);

            cardWalletNumberTextView.setText(getString(R.string.recipient_wallet_number, currentWithdrawalType.getMethod()));
            cardWalletNumberEditText.setHint(getString(R.string.recipient_wallet_number, ""));
        }
        recipientDataTextView.setText(getString(R.string.recipient_data, currentWithdrawalType.getMethod()));
        amountWithCommissionTextView.setText(getString(R.string.amount_with_commission, 0.0));

        backArrowImageView.setOnClickListener(v -> {
//            if (requireActivity().getCurrentFocus() == null) {
//                NavHostFragment.findNavController(this).popBackStack();
//                return;
//            }
//            InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);
//            requireActivity().getCurrentFocus().clearFocus();
            NavHostFragment.findNavController(this).popBackStack();
        });

        amountEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(amountEditText, hasFocus, R.string.zero);
        });

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() <= 0) {
                    amountWithCommissionTextView.setText(getString(R.string.amount_with_commission, 0.00));
                    return;
                }
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (!TextUtils.isDigitsOnly(s)) {
                    return;
                }
                final double numericAmount = Double.parseDouble(s.toString());
                final double amountWithCommission = numericAmount + (double) numericAmount*currentWithdrawalType.getCommission()/100;

                amountWithCommissionTextView.setText(getString(R.string.amount_with_commission, amountWithCommission));
            }
        });

//        if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_card))) {
//            if (currentWithdrawalType.getMethod().equalsIgnoreCase(getString(R.string.master_card))) {
//                cardWalletNumberEditText.addTextChangedListener(WithdrawalFormatter.getMasterCardFormat());
//            }
//            else if (currentWithdrawalType.getMethod().equalsIgnoreCase(getString(R.string.visa_card))) {
//                cardWalletNumberEditText.addTextChangedListener(WithdrawalFormatter.getVisaCardFormat());
//            }
//        }

        withdrawBtn.setOnClickListener(v -> {
            withdrawBtn.startAnimation();

            if (TextUtils.isEmpty(amountEditText.getText().toString().trim())) {
                Toast.makeText(requireContext(), "Введите сумму для вывода", Toast.LENGTH_SHORT).show();
                return;
            }
            final double amount = Double.parseDouble(amountEditText.getText().toString().trim());
            final String cardNumber = cardWalletNumberEditText.getText().toString().trim();
            final String recipientFullName = fullNameEditText.getText().toString().trim();
            final String phone = phoneEditText.getText().toString().trim();
            final String city = cityEditText.getText().toString().trim();
            final Country country = (Country) countrySpinner.getSelectedItem();

            networkConnectivity.checkInternetConnection(isConnected -> {
                if (!isConnected) {
                    NavHostFragment.findNavController(this).navigate(
                            WithdrawalFragmentDirections.actionWithdrawalFragmentToTransactionResultFragment()
                                    .setResult(false)
                                    .setType(Constants.RESULT_TYPE_WITHDRAWAL)
                                    .setErrorMessage(Constants.NO_INTERNET));
                    return;
                }
                transactionViewModel.withdrawFinds(
                        currentWithdrawalType.getType(),
                        currentWithdrawalType.getMethod(),
                        amount,
                        TextUtils.isEmpty(cardNumber) ? null : cardNumber,
                        TextUtils.isEmpty(recipientFullName) ? null : recipientFullName,
                        TextUtils.isEmpty(phone) ? null : phone,
                        country == null ? null : country.getTitle(),
                        TextUtils.isEmpty(city) ? null : city);
            });
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        authViewModel.getCountryList().observe(getViewLifecycleOwner(), countryList -> {
            countryAdapter.setCountryList(countryList);
            countryAdapter.notifyDataSetChanged();
        });

        transactionViewModel.getWithdrawalResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                NavHostFragment.findNavController(this).navigate(
                        WithdrawalFragmentDirections.actionWithdrawalFragmentToTransactionResultFragment().setResult(true).setType("withdrawal"));

                cardWalletNumberEditText.setEnabled(true);
                amountEditText.setEnabled(true);
                phoneEditText.setEnabled(true);
                fullNameEditText.setEnabled(true);
                cityEditText.setEnabled(true);
                countrySpinner.setEnabled(true);

                withdrawBtn.revertAnimation();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.CANCELLED || workInfo.getState() == WorkInfo.State.FAILED) {
                NavHostFragment.findNavController(this).navigate(
                        WithdrawalFragmentDirections.actionWithdrawalFragmentToTransactionResultFragment()
                                .setResult(false)
                                .setType("withdrawal"));

                cardWalletNumberEditText.setEnabled(true);
                amountEditText.setEnabled(true);
                phoneEditText.setEnabled(true);
                fullNameEditText.setEnabled(true);
                cityEditText.setEnabled(true);
                countrySpinner.setEnabled(true);

                withdrawBtn.revertAnimation();
                return;
            }
            cardWalletNumberEditText.setEnabled(false);
            amountEditText.setEnabled(false);
            phoneEditText.setEnabled(false);
            fullNameEditText.setEnabled(false);
            cityEditText.setEnabled(false);
            countrySpinner.setEnabled(false);
        });
    }

    private static final String TAG = WithdrawalFragment.class.toString();
}