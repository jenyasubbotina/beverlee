package uz.alex.its.beverlee.view.fragments;

import android.Manifest;
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
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.PermissionManager;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.ContactAdapter;
import uz.alex.its.beverlee.view.interfaces.ContactCallback;
import uz.alex.its.beverlee.viewmodel.ContactsViewModel;
import uz.alex.its.beverlee.viewmodel_factory.ContactsViewModelFactory;

public class ContactsFragment extends Fragment implements ContactCallback {
    /* pull tor refresh */
    private SwipeRefreshLayout swipeRefreshLayout;

    private RadioGroup radioGroup;
    private RadioButton allRadioBtn;
    private RadioButton favoritesRadioBtn;
    private EditText searchFieldEditText;
    private FloatingActionButton fab;
    private ProgressBar progressBar;

    /* bottomSheet for contactList */
    private BottomNavigationView bottomNavigationView;
    private LinearLayout bottomSheetContacts;
    private TextView bottomSheetContactsTransfer;
    private TextView bottomSheetAddToFavs;
    private TextView bottomSheetDelete;
    private BottomSheetBehavior contactsSheetBehavior;

    /* bottomSheet for favContactList */
    private LinearLayout bottomSheetFavContacts;
    private TextView bottomSheetFavContactsTransfer;
    private TextView bottomSheetRemoveFromFav;
    private BottomSheetBehavior favsSheetBehavior;

    /* select/deselect contactItem */
    private ContactAdapter.ContactVerticalViewHolder selectedHolder = null;
    private boolean contactSelected = false;

    /* contact list */
    private TextView contactListEmptyTextView;
    private RecyclerView contactListRecyclerView;
    private ContactAdapter adapter;

    private ContactsViewModel contactsViewModel;

    private static volatile ContactData selectedContact;

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
        contactListRecyclerView = root.findViewById(R.id.contact_list_recycler_view);
        contactListEmptyTextView = root.findViewById(R.id.contact_list_empty_text_view);

        adapter = new ContactAdapter(requireContext(), this, ContactAdapter.TYPE_VERTICAL);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        contactListRecyclerView.setLayoutManager(layoutManager);
        contactListRecyclerView.setAdapter(adapter);

        fab = requireActivity().findViewById(R.id.floating_btn);
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        progressBar = root.findViewById(R.id.progress_bar);

        //contacts bottom sheet
        bottomSheetContacts = requireActivity().findViewById(R.id.bottom_sheet_contacts);
        bottomSheetContactsTransfer = requireActivity().findViewById(R.id.contacts_transfer);
        bottomSheetAddToFavs = requireActivity().findViewById(R.id.add_to_favorites);
        bottomSheetDelete = requireActivity().findViewById(R.id.delete_contact);

        //fav contacts bottom sheet
        bottomSheetFavContacts = requireActivity().findViewById(R.id.bottom_sheet_fav_contacts);
        bottomSheetFavContactsTransfer = requireActivity().findViewById(R.id.fav_contacts_transfer);
        bottomSheetRemoveFromFav = requireActivity().findViewById(R.id.remove_from_favorites);

        contactsSheetBehavior = BottomSheetBehavior.from(bottomSheetContacts);
        favsSheetBehavior = BottomSheetBehavior.from(bottomSheetFavContacts);
        contactsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        favsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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
                favoritesRadioBtn.setTextColor(getResources().getColor(R.color.colorDarkGrey, null));
                contactsViewModel.setIsFavorite(false);
                revertItems();
                return;
            }
            if (checkedId == favoritesRadioBtn.getId()) {
                radioGroup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_group_exp, null));
                allRadioBtn.setTextColor(getResources().getColor(R.color.colorDarkGrey, null));
                favoritesRadioBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));
                contactsViewModel.setIsFavorite(true);
                revertItems();
            }
        });

        BottomSheetBehavior.BottomSheetCallback callback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING: {
                        if (!contactSelected) {
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                return;
            }
        };

        contactsSheetBehavior.addBottomSheetCallback(callback);
        favsSheetBehavior.addBottomSheetCallback(callback);

        bottomSheetContactsTransfer.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(
                ContactsFragmentDirections.actionContactsFragmentToTransferFragment().setContactId(selectedContact.getId())));

        bottomSheetAddToFavs.setOnClickListener(v -> contactsViewModel.addToFavs(selectedContact));

        bottomSheetDelete.setOnClickListener(v -> contactsViewModel.deleteContact(selectedContact.getId()));

        bottomSheetRemoveFromFav.setOnClickListener(v -> contactsViewModel.removeFromFavs(selectedContact));

        bottomSheetFavContactsTransfer.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(
                ContactsFragmentDirections.actionContactsFragmentToTransferFragment().setContactId(selectedContact.getId())));

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
                    contactsViewModel.searchContactList(s.toString());
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

        contactsViewModel.getAddToFavsResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Toast.makeText(requireContext(), "Добавлено в Избранное", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                bottomSheetAddToFavs.setEnabled(true);
                deselectContact();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), "Контакт уже в Избранных", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                bottomSheetAddToFavs.setEnabled(true);
                deselectContact();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            bottomSheetAddToFavs.setEnabled(false);
        });

        contactsViewModel.getRemoveFromFavsResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Toast.makeText(requireContext(), "Удалено из Избранных", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                bottomSheetRemoveFromFav.setEnabled(true);
                deselectContact();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), "Контакт уже удален из Избранных", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                bottomSheetRemoveFromFav.setEnabled(true);
                deselectContact();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            bottomSheetRemoveFromFav.setEnabled(false);
        });

        contactsViewModel.getDeleteContactResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                adapter.getContactList().remove(selectedContact);
                adapter.notifyDataSetChanged();

                Toast.makeText(requireContext(), "Контакт удален", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                bottomSheetDelete.setEnabled(true);
                deselectContact();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                bottomSheetDelete.setEnabled(true);
                deselectContact();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            bottomSheetDelete.setEnabled(false);
        });
    }

    @Override
    public void onContactSelected(final ContactData contact, final ContactAdapter.ContactViewHolder holder) {
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

            if (radioGroup.getCheckedRadioButtonId() == allRadioBtn.getId()) {
                contactsSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == favoritesRadioBtn.getId()) {
                favsSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
            return;
        }
        deselectContact();
    }

    private void revertItems() {
        contactSelected = false;
        if (selectedHolder != null) {
            final ImageView selectedAvatarImageView = selectedHolder.avatarImageView;
            final TextView selectedNameTextView = selectedHolder.contactNameTextView;
            final ImageView selectedCheckImageView = selectedHolder.checkImageView;

            final RelativeLayout.LayoutParams avatarParams = (RelativeLayout.LayoutParams) selectedAvatarImageView.getLayoutParams();
            final RelativeLayout.LayoutParams nameParams = (RelativeLayout.LayoutParams) selectedNameTextView.getLayoutParams();
            final RelativeLayout.LayoutParams checkParams = (RelativeLayout.LayoutParams) selectedCheckImageView.getLayoutParams();

            avatarParams.leftMargin = avatarParams.leftMargin - 32;
            nameParams.leftMargin = nameParams.leftMargin - 32;
            checkParams.leftMargin = checkParams.leftMargin - 32;
            selectedAvatarImageView.setLayoutParams(avatarParams);
            selectedNameTextView.setLayoutParams(nameParams);
            selectedCheckImageView.setLayoutParams(checkParams);
            selectedCheckImageView.setVisibility(View.INVISIBLE);
            selectedHolder = null;
        }
        fab.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);

        if (radioGroup.getCheckedRadioButtonId() == allRadioBtn.getId()) {
            Log.i(TAG, "revertItems: all" );
            contactsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        if (radioGroup.getCheckedRadioButtonId() == favoritesRadioBtn.getId()) {
            Log.i(TAG, "revertItems: favs");
            favsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void deselectContact() {
        if (contactSelected) {
            selectedContact = null;
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

            contactSelected = false;
            selectedHolder = null;

            if (radioGroup.getCheckedRadioButtonId() == allRadioBtn.getId()) {
                contactsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == favoritesRadioBtn.getId()) {
                favsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }

    private static final String TAG = ContactsFragment.class.toString();
}
