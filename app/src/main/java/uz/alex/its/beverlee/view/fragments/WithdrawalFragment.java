package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.Country;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel.WithdrawalType;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.utils.Regex;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.CountryAdapter;
import uz.alex.its.beverlee.viewmodel.AuthViewModel;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel.factory.AuthViewModelFactory;
import uz.alex.its.beverlee.viewmodel.factory.TransactionViewModelFactory;

public class WithdrawalFragment extends Fragment {
    private ConstraintLayout rootLayout;

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

    private FormatWatcher cardFormatWatcher;

    public WithdrawalFragment() {
        // Required empty public constructor
    }

    public static final Slot[] CARD_NUMBER_MASK = {
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.hardcodedSlot(' ').withTags(Slot.TAG_DECORATION),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
            PredefinedSlots.digit(),
    };

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

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(WithdrawalFragment.this).popBackStack();
            }
        });

        cardFormatWatcher = new MaskFormatWatcher(
                MaskImpl.createTerminated(PredefinedSlots.CARD_NUMBER_STANDART));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_withdrawal, container, false);

        rootLayout = root.findViewById(R.id.card_layout);

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

        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(rootLayout);
        constraintSet.connect(withdrawBtn.getId(), ConstraintSet.START, amountEditText.getId(), ConstraintSet.START);
        constraintSet.connect(withdrawBtn.getId(), ConstraintSet.END, amountEditText.getId(), ConstraintSet.END);
        constraintSet.connect(withdrawBtn.getId(), ConstraintSet.BOTTOM, rootLayout.getId(), ConstraintSet.BOTTOM);

        /* if transfer -> fullName, phone, country, city, amount */
        /* if electronic -> walletNumber, amount */
        /* if card -> cardNumber, amount */
        if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_transfer))) {
            constraintSet.setVisibility(fullNameTextView.getId(), View.VISIBLE);
            constraintSet.setVisibility(phoneTextView.getId(), View.VISIBLE);
            constraintSet.setVisibility(countryTextView.getId(), View.VISIBLE);
            constraintSet.setVisibility(cityTextView.getId(), View.VISIBLE);
            constraintSet.setVisibility(fullNameEditText.getId(), View.VISIBLE);
            constraintSet.setVisibility(phoneEditText.getId(), View.VISIBLE);
            constraintSet.setVisibility(countrySpinner.getId(), View.VISIBLE);
            constraintSet.setVisibility(cityEditText.getId(), View.VISIBLE);
            constraintSet.setVisibility(cardWalletNumberTextView.getId(), View.GONE);
            constraintSet.setVisibility(cardWalletNumberEditText.getId(), View.GONE);

            constraintSet.connect(withdrawBtn.getId(), ConstraintSet.TOP, amountWithCommissionTextView.getId(), ConstraintSet.BOTTOM);
        }
        else {
            constraintSet.setVisibility(fullNameTextView.getId(), View.GONE);
            constraintSet.setVisibility(phoneTextView.getId(), View.GONE);
            constraintSet.setVisibility(countryTextView.getId(), View.GONE);
            constraintSet.setVisibility(cityTextView.getId(), View.GONE);
            constraintSet.setVisibility(fullNameEditText.getId(), View.GONE);
            constraintSet.setVisibility(phoneEditText.getId(), View.GONE);
            constraintSet.setVisibility(countrySpinner.getId(), View.GONE);
            constraintSet.setVisibility(cityEditText.getId(), View.GONE);
        }
        if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_card))) {
            cardWalletNumberTextView.setText(getString(R.string.recipient_card_number, currentWithdrawalType.getMethod()));
            cardWalletNumberEditText.setHint(getString(R.string.recipient_card_number, ""));
            cardFormatWatcher.installOn(cardWalletNumberEditText);
        }
        else if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_wallet))) {
            cardWalletNumberTextView.setText(getString(R.string.recipient_wallet_number, currentWithdrawalType.getMethod()));
            cardWalletNumberEditText.setHint(getString(R.string.recipient_wallet_number, ""));
            cardFormatWatcher.installOn(cardWalletNumberEditText);
        }
        recipientDataTextView.setText(getString(R.string.recipient_data, currentWithdrawalType.getMethod()));
        amountWithCommissionTextView.setText(getString(R.string.amount_with_commission, 0.0));

        constraintSet.applyTo(rootLayout);

        backArrowImageView.setOnClickListener(v -> {
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

        withdrawBtn.setOnClickListener(v -> {
            final String amountStr = amountEditText.getText().toString().trim();
            final String cardNumber = cardWalletNumberEditText.getText().toString().trim();
            final String recipientFullName = fullNameEditText.getText().toString().trim();
            final String phone = phoneEditText.getText().toString().trim();
            final String city = cityEditText.getText().toString().trim();
            final Country country = (Country) countrySpinner.getSelectedItem();

            if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_transfer))) {
                if (TextUtils.isEmpty(recipientFullName)) {
                    Toast.makeText(requireContext(), "Укажите Ф.И.О.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(requireContext(), "Укажите номер телефона", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Regex.isPhoneNumber(phone)) {
                    Toast.makeText(requireContext(), "Неверный формат номера телефона", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(city)) {
                    Toast.makeText(requireContext(), "Укажите город", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_card))) {
                if (TextUtils.isEmpty(cardNumber)) {
                    Toast.makeText(requireContext(), "Укажите номер карты получателя", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else if (currentWithdrawalType.getType().equalsIgnoreCase(getString(R.string.withdrawal_type_wallet))) {
                if (TextUtils.isEmpty(cardNumber)) {
                    Toast.makeText(requireContext(), "Укажите номер кошелька получателя", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (TextUtils.isEmpty(amountStr)) {
                Toast.makeText(requireContext(), "Укажите сумму", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(amountEditText.getText().toString().trim())) {
                Toast.makeText(requireContext(), "Введите сумму для вывода", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isDigitsOnly(amountStr)) {
                Toast.makeText(requireContext(), "Неверный формат суммы", Toast.LENGTH_SHORT).show();
                return;
            }

            withdrawBtn.startAnimation();

            networkConnectivity.checkInternetConnection(isConnected -> {
                if (!isConnected) {
                    NavHostFragment.findNavController(this).navigate(
                            WithdrawalFragmentDirections.actionWithdrawalFragmentToTransactionResultFragment()
                                    .setResult(false)
                                    .setType(Constants.RESULT_TYPE_WITHDRAWAL)
                                    .setErrorMessage(Constants.NO_INTERNET),
                            new NavOptions.Builder().setPopUpTo(R.id.transferFragment, false).build());
                    return;
                }
                transactionViewModel.withdrawFinds(
                        currentWithdrawalType.getType(),
                        currentWithdrawalType.getMethod(),
                        Double.parseDouble(amountStr),
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
            if (countryList != null && !countryList.isEmpty()) {
                final List<Country> tempList = new ArrayList<>();

                for (final Country country: countryList) {
                    if (country.getTitle().equals(getString(R.string.russia))
                            || country.getTitle().equals(getString(R.string.uzbekistan))
                            || country.getTitle().equals(getString(R.string.kazakhstan))) {
                        tempList.add(0, country);
                    }
                    else {
                        tempList.add(country);
                    }
                }
                countryAdapter.setCountryList(tempList);
                countryAdapter.notifyDataSetChanged();
            }
        });

        transactionViewModel.getWithdrawalResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                NavHostFragment.findNavController(this).navigate(
                        WithdrawalFragmentDirections.actionWithdrawalFragmentToTransactionResultFragment()
                                .setResult(true)
                                .setType("withdrawal"),
                        new NavOptions.Builder().setPopUpTo(R.id.transferFragment, false).build());

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
                                .setType("withdrawal"),
                        new NavOptions.Builder().setPopUpTo(R.id.transferFragment, false).build());

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