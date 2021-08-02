package uz.alex.its.beverlee.view.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.actor.ContactModel;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.ContactAdapter;
import uz.alex.its.beverlee.view.dialog.ContactsBottomSheet;
import uz.alex.its.beverlee.view.dialog.FavContactsBottomSheet;
import uz.alex.its.beverlee.view.interfaces.ContactCallback;
import uz.alex.its.beverlee.view.interfaces.DialogCallback;
import uz.alex.its.beverlee.viewmodel.ContactsViewModel;
import uz.alex.its.beverlee.viewmodel.factory.ContactsViewModelFactory;

public class ContactsFragment extends Fragment implements ContactCallback, DialogCallback {
    /* pull tor refresh */
    private SwipeRefreshLayout swipeRefreshLayout;

    private RadioGroup radioGroup;
    private RadioButton allRadioBtn;
    private RadioButton favoritesRadioBtn;
    private EditText searchFieldEditText;
    private FloatingActionButton fab;
    private ProgressBar progressBar;

    /* bottomSheet for contactList */
    private ContactsBottomSheet contactsBottomSheet;

    /* bottomSheet for favContactList */
    private FavContactsBottomSheet favContactsBottomSheet;

    /* select/deselect contactItem */
    private ContactAdapter.ContactVerticalViewHolder selectedHolder = null;
    private boolean contactSelected = false;

    /* contact list */
    private TextView contactListEmptyTextView;
    private ContactAdapter adapter;

    private ContactsViewModel contactsViewModel;

    private static volatile ContactModel.Contact selectedContact;

    public ContactsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedContact = null;

        final ContactsViewModelFactory contactsFactory = new ContactsViewModelFactory(requireContext());
        contactsViewModel = new ViewModelProvider(getViewModelStore(), contactsFactory).get(ContactsViewModel.class);

        contactsViewModel.fetchContactList(null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_contacts, container, false);

        /* pull to refresh */
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);

        radioGroup = root.findViewById(R.id.radio_group);
        allRadioBtn = root.findViewById(R.id.all_radio_btn);
        favoritesRadioBtn = root.findViewById(R.id.favorites_radio_btn);
        searchFieldEditText = root.findViewById(R.id.contacts_search_edit_text);
        final RecyclerView contactListRecyclerView = root.findViewById(R.id.contact_list_recycler_view);
        contactListEmptyTextView = root.findViewById(R.id.contact_list_empty_text_view);

        adapter = new ContactAdapter(requireContext(), this, ContactAdapter.TYPE_VERTICAL);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        contactListRecyclerView.setLayoutManager(layoutManager);
        contactListRecyclerView.setAdapter(adapter);

        progressBar = root.findViewById(R.id.progress_bar);

        /* bottom navigation */
        final BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        fab = requireActivity().findViewById(R.id.floating_btn);
        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_contactsFragment_to_transferFragment));
        searchFieldEditText.setOnFocusChangeListener((v, hasFocus) ->  {
            if (hasFocus) {
                searchFieldEditText.setBackgroundResource(R.drawable.light_search_field);
                searchFieldEditText.setHint("");
                return;
            }
            if (searchFieldEditText.getText().length() > 0) {
                searchFieldEditText.setBackgroundResource(R.drawable.light_search_field);
                searchFieldEditText.setHint("");
                return;
            }
            searchFieldEditText.setBackgroundResource(R.drawable.dark_search_field);
            searchFieldEditText.setHint(R.string.search);
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == allRadioBtn.getId()) {
                radioGroup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_group_income, null));

                allRadioBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));
                allRadioBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_btn_active, null));

                favoritesRadioBtn.setTextColor(getResources().getColor(R.color.colorDarkGrey, null));
                favoritesRadioBtn.setBackground(null);

                contactsViewModel.setIsFavorite(false);
                return;
            }
            if (checkedId == favoritesRadioBtn.getId()) {
                radioGroup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_group_income, null));

                allRadioBtn.setTextColor(getResources().getColor(R.color.colorDarkGrey, null));
                allRadioBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_btn_active, null));

                favoritesRadioBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));
                favoritesRadioBtn.setBackground(null);

                contactsViewModel.setIsFavorite(true);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (radioGroup.getCheckedRadioButtonId() == favoritesRadioBtn.getId()) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == allRadioBtn.getId()) {
                contactsViewModel.fetchContactList(null, null);
            }
        });

        searchFieldEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 4) {
                    contactsViewModel.setSearchQuery(s.toString());
                    contactsViewModel.setSearchFieldEmpty(false);
                    return;
                }
                contactsViewModel.setSearchFieldEmpty(true);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contactsBottomSheet = ContactsBottomSheet.newInstance(this);
        contactsBottomSheet.setTargetFragment(this, Constants.REQUEST_CODE_BOTTOM_SHEET_CONTACTS);

        favContactsBottomSheet = FavContactsBottomSheet.newInstance(this);
        favContactsBottomSheet.setTargetFragment(this, Constants.REQUEST_CODE_BOTTOM_SHEET_FAVORITES);

        contactsViewModel.isLoading().observe(getViewLifecycleOwner(), isInLoading -> {
            if (isInLoading) {
                swipeRefreshLayout.setRefreshing(true);
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        contactsViewModel.getContactList().observe(getViewLifecycleOwner(), contactList -> {
            if (contactList != null && !contactList.isEmpty()) {
                contactListEmptyTextView.setVisibility(View.GONE);
            }
            else {
                contactListEmptyTextView.setVisibility(View.VISIBLE);
            }
            adapter.setContactList(contactList);
            swipeRefreshLayout.setRefreshing(false);
        });

        contactsViewModel.clearObservers();

        contactsViewModel.getAddToFavsResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Toast.makeText(requireContext(), "Добавлено в Избранное", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), "Контакт уже в Избранных", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
        });

        contactsViewModel.getDeleteContactResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Toast.makeText(requireContext(), "Контакт удален", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
        });

        contactsViewModel.getRemoveFromFavsResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Toast.makeText(requireContext(), "Удалено из Избранных", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), "Контакт уже удален из Избранных", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Constants.RESULT_CODE_TRANSFER_TO_CONTACT:
            case Constants.RESULT_CODE_TRANSFER_TO_FAVORITE: {
                NavHostFragment.findNavController(this).navigate(
                        ContactsFragmentDirections.actionContactsFragmentToTransferFragment().setContactId(selectedContact.getId()));
                contactSelected = false;
                selectedHolder = null;
                selectedContact = null;
                break;
            }
            case Constants.RESULT_CODE_DELETE_CONTACT: {
                contactsViewModel.deleteContact(selectedContact.getContactId());
                break;
            }
            case Constants.RESULT_CODE_ADD_TO_FAVS: {
                contactsViewModel.addToFavs(selectedContact);
                break;
            }
            case Constants.RESULT_CODE_REMOVE_FROM_FAVS: {
                contactsViewModel.removeFromFavs(selectedContact);
                break;
            }
        }
    }

    @Override
    public void onContactSelected(final ContactModel.Contact contact, final ContactAdapter.ContactViewHolder holder) {
        if (!contactSelected) {
            contactSelected = true;
            selectedHolder = (ContactAdapter.ContactVerticalViewHolder) holder;
            selectedContact = contact;

            final ConstraintLayout.LayoutParams avatarParams = (ConstraintLayout.LayoutParams) selectedHolder.avatarImageView.getLayoutParams();
            final ConstraintLayout.LayoutParams nameParams = (ConstraintLayout.LayoutParams) selectedHolder.contactNameTextView.getLayoutParams();
            final ConstraintLayout.LayoutParams checkParams = (ConstraintLayout.LayoutParams) selectedHolder.checkImageView.getLayoutParams();

            avatarParams.leftMargin = avatarParams.leftMargin + 32;
            nameParams.leftMargin = nameParams.leftMargin + 32;
            checkParams.leftMargin = checkParams.leftMargin + 32;
            selectedHolder.avatarImageView.setLayoutParams(avatarParams);
            selectedHolder.contactNameTextView.setLayoutParams(nameParams);
            selectedHolder.checkImageView.setLayoutParams(checkParams);
            selectedHolder.checkImageView.setVisibility(View.VISIBLE);

            if (selectedContact.isFav()) {
                favContactsBottomSheet.show(getParentFragmentManager().beginTransaction(), TAG);
                return;
            }
            if (!selectedContact.isFav()) {
                contactsBottomSheet.show(getParentFragmentManager().beginTransaction(), TAG);
            }
        }
    }

    @Override
    public void onDismiss() {
        if (contactSelected) {
            final ImageView selectedAvatarImageView = selectedHolder.avatarImageView;
            final TextView selectedNameTextView = selectedHolder.contactNameTextView;
            final ImageView selectedCheckImageView = selectedHolder.checkImageView;

            final ConstraintLayout.LayoutParams avatarParams = (ConstraintLayout.LayoutParams) selectedAvatarImageView.getLayoutParams();
            final ConstraintLayout.LayoutParams nameParams = (ConstraintLayout.LayoutParams) selectedNameTextView.getLayoutParams();
            final ConstraintLayout.LayoutParams checkParams = (ConstraintLayout.LayoutParams) selectedCheckImageView.getLayoutParams();

            avatarParams.leftMargin = avatarParams.leftMargin - 32;
            nameParams.leftMargin = nameParams.leftMargin - 32;
            checkParams.leftMargin = checkParams.leftMargin - 32;
            selectedAvatarImageView.setLayoutParams(avatarParams);
            selectedNameTextView.setLayoutParams(nameParams);
            selectedCheckImageView.setLayoutParams(checkParams);
            selectedCheckImageView.setVisibility(View.INVISIBLE);

            if (selectedContact.isFav()) {
                favContactsBottomSheet.dismiss();
            }
            else {
                contactsBottomSheet.dismiss();
            }
            contactSelected = false;
            selectedHolder = null;
            selectedContact = null;
        }
    }

    private static final String TAG = ContactsFragment.class.toString();
}
