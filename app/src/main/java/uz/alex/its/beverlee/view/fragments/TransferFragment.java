package uz.alex.its.beverlee.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.actor.ContactModel;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.requestParams.VerifyTransferParams;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.ContactAdapter;
import uz.alex.its.beverlee.view.dialog.VerifyTransactionDialog;
import uz.alex.its.beverlee.view.interfaces.ContactCallback;
import uz.alex.its.beverlee.viewmodel.ContactsViewModel;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel_factory.ContactsViewModelFactory;
import uz.alex.its.beverlee.viewmodel_factory.TransactionViewModelFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferFragment extends Fragment implements ContactCallback {
    /* header */
    private ImageView backArrowImageView;

    /* parent layout */
    private ConstraintLayout parentLayout;

    /* views */
    private TextView currentBalanceTextView;
    private TextView transferErrorTextView;
    private ImageView recipientImageView;
    private TextView amountTextView;
    private EditText amountEditText;
    private TextView saveContactTextView;
    private CheckBox saveContactCheckBox;
    private CircularProgressButton transferBtn;

    private EditText recipientEditText;
    private View recyclerViewBackground;
    private ContactAdapter contactAdapter;

    private TransactionViewModel transactionViewModel;
    private ContactsViewModel contactsViewModel;

    private static volatile long selectedContactId;
    private static volatile boolean contactListHidden;

    private NetworkConnectivity networkConnectivity;

    public TransferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selectedContactId = TransferFragmentArgs.fromBundle(getArguments()).getContactId();
        }
        else {
            selectedContactId = 0L;
        }
        contactListHidden = true;

        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());

        final TransactionViewModelFactory transactionFactory = new TransactionViewModelFactory(requireContext());
        final ContactsViewModelFactory contactFactory = new ContactsViewModelFactory(requireContext());
        transactionViewModel = new ViewModelProvider(getViewModelStore(), transactionFactory).get(TransactionViewModel.class);
        contactsViewModel = new ViewModelProvider(getViewModelStore(), contactFactory).get(ContactsViewModel.class);

        transactionViewModel.fetchCurrentBalance();
        contactsViewModel.fetchContactList(null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_transfer, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        currentBalanceTextView = root.findViewById(R.id.current_balance_text_view);
        transferErrorTextView = root.findViewById(R.id.transfer_error_text_view);
        recipientEditText = root.findViewById(R.id.recipient_edit_text);
        recipientImageView = root.findViewById(R.id.recipient_image_view);
        amountTextView = root.findViewById(R.id.amount_text_view);
        amountEditText = root.findViewById(R.id.amount_edit_text);
        saveContactTextView = root.findViewById(R.id.save_contact_text_view);
        saveContactCheckBox = root.findViewById(R.id.save_contact_check_box);
        transferBtn = root.findViewById(R.id.transfer_btn);
        parentLayout = root.findViewById(R.id.card_layout);
        recyclerViewBackground = root.findViewById(R.id.recycler_view_background);

        final RecyclerView contactRecyclerView = root.findViewById(R.id.contact_list_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        contactRecyclerView.setHasFixedSize(true);
        contactAdapter = new ContactAdapter(requireContext(), this, ContactAdapter.TYPE_SPINNER);
        contactRecyclerView.setLayoutManager(layoutManager);
        contactRecyclerView.setAdapter(contactAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UiUtils.hideBottomNav(requireActivity());

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

        if (selectedContactId > 0L) {
            recipientEditText.setText(String.valueOf(selectedContactId));
        }
        recipientEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(recipientEditText, hasFocus, R.string.phone_or_id));

        amountEditText.setOnFocusChangeListener((v, hasFocus) -> UiUtils.setFocusChange(amountEditText, hasFocus, R.string.zero));

        saveContactTextView.setOnClickListener(v -> saveContactCheckBox.setChecked(!saveContactCheckBox.isChecked()));

        transferBtn.setOnClickListener(v -> {
            transferBtn.startAnimation();
            transferErrorTextView.setVisibility(View.GONE);

            String recipientIdText = recipientEditText.getText().toString().trim();
            final String amountText = amountEditText.getText().toString().trim();

            if (recipientIdText.contains("+")) {
                recipientIdText = recipientIdText.replace("+", "");
            }
            if (!TextUtils.isDigitsOnly(recipientIdText)) {
                Toast.makeText(requireContext(), "Неверный формат. Укажите номер телефона или ID получателя", Toast.LENGTH_SHORT).show();
                return;
            }
            final long recipientId = TextUtils.isEmpty(recipientIdText) ? 0L : Long.parseLong(recipientIdText);
            final double amount = TextUtils.isEmpty(amountText) ? 0 : Double.parseDouble(amountText);
            String addNotebook = null;

            if (recipientId <= 0) {
                Toast.makeText(requireContext(), "Укажите получателя", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount <= 0) {
                Toast.makeText(requireContext(), "Укажите сумму перевода", Toast.LENGTH_SHORT).show();
                return;
            }
            if (saveContactCheckBox.isChecked()) {
                addNotebook = "on";
            }
            else {
                addNotebook = "0";
            }
            final String finalAddNotebook = addNotebook;

            networkConnectivity.checkInternetConnection(isConnected -> {
                if (!isConnected) {
                    NavHostFragment.findNavController(this).navigate(
                            TransferFragmentDirections.actionTransferFragmentToTransactionResultFragment()
                                    .setResult(false)
                                    .setType(Constants.RESULT_TYPE_TRANSFER)
                                    .setErrorMessage(Constants.NO_INTERNET));
                    return;
                }
                transactionViewModel.verifyTransfer(recipientId, amount, finalAddNotebook);
            });
        });

        recipientImageView.setOnClickListener(v -> {
            if (contactListHidden) {
                contactListHidden = false;
                showDropDown();
                return;
            }
            contactListHidden = true;
            dismissDropDown();
        });

        parentLayout.setOnClickListener(v -> {
            if (!contactListHidden) {
                contactListHidden = true;
                dismissDropDown();
            }
        });

        recipientEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        transactionViewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                currentBalanceTextView.setText(getString(R.string.current_balance, String.valueOf(balance.getBalance())));
            }
        });

        transactionViewModel.getVerifyTransferResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                final VerifyTransactionDialog dialog = VerifyTransactionDialog.newInstance(new VerifyTransferParams(
                        workInfo.getOutputData().getLong(Constants.RECIPIENT_ID, 0L),
                        workInfo.getOutputData().getDouble(Constants.AMOUNT, 0),
                        workInfo.getOutputData().getString(Constants.NOTE)));
                dialog.setTargetFragment(this, Constants.REQUEST_CODE_VERIFY_TRANSFER);
                dialog.show(getParentFragmentManager().beginTransaction(), TAG);
                recipientEditText.setEnabled(true);
                amountEditText.setEnabled(true);
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                transferErrorTextView.setText(workInfo.getOutputData().getString(Constants.REQUEST_ERROR));
                transferErrorTextView.setVisibility(View.VISIBLE);
                transferBtn.revertAnimation();
                recipientEditText.setEnabled(true);
                amountEditText.setEnabled(true);
                return;
            }
            recipientEditText.setEnabled(false);
            amountEditText.setEnabled(false);
        });

        transactionViewModel.getTransferResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                transferBtn.revertAnimation();
                NavHostFragment.findNavController(this).navigate(
                        TransferFragmentDirections.actionTransferFragmentToTransactionResultFragment().setResult(true).setType("transfer"));
                recipientEditText.setEnabled(true);
                amountEditText.setEnabled(true);
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                transferErrorTextView.setText(workInfo.getOutputData().getString(Constants.REQUEST_ERROR));
                NavHostFragment.findNavController(this).navigate(
                        TransferFragmentDirections.actionTransferFragmentToTransactionResultFragment()
                                .setResult(false)
                                .setType(Constants.RESULT_TYPE_TRANSFER));
                transferBtn.revertAnimation();
                recipientEditText.setEnabled(true);
                amountEditText.setEnabled(true);
                return;
            }
            recipientEditText.setEnabled(false);
            amountEditText.setEnabled(false);
        });

        contactsViewModel.getContactList().observe(getViewLifecycleOwner(), contactList -> {
            contactAdapter.setContactList(contactList);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_VERIFY_TRANSFER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                transactionViewModel.transferFunds(
                        data.getLongExtra(Constants.RECIPIENT_ID, 0L),
                        data.getDoubleExtra(Constants.AMOUNT, 0),
                        data.getStringExtra(Constants.NOTE),
                        data.getStringExtra(Constants.PINCODE));
            }
        }
    }

    @Override
    public void onContactSelected(final ContactModel.ContactData contact, final ContactAdapter.ContactViewHolder holder) {
        if (!contactListHidden) {
            contactListHidden = true;
            dismissDropDown();
            recipientEditText.setText(String.valueOf(contact.getContact().getId()));
        }
    }

    private void showDropDown() {
        recyclerViewBackground.setVisibility(View.VISIBLE);
        amountTextView.setVisibility(View.INVISIBLE);
        amountEditText.setVisibility(View.INVISIBLE);
        saveContactCheckBox.setVisibility(View.INVISIBLE);
        saveContactTextView.setVisibility(View.INVISIBLE);
    }

    private void dismissDropDown() {
        recyclerViewBackground.setVisibility(View.GONE);
        amountTextView.setVisibility(View.VISIBLE);
        amountEditText.setVisibility(View.VISIBLE);
        saveContactCheckBox.setVisibility(View.VISIBLE);
        saveContactTextView.setVisibility(View.VISIBLE);
    }

    private static final String TAG = TransferFragment.class.toString();
}
