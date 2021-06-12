package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.WorkInfo;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.Country;
import uz.alex.its.beverlee.model.actor.UserModel.User;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.viewmodel.UserViewModel;
import uz.alex.its.beverlee.viewmodel_factory.UserViewModelFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PersonalDataFragment extends Fragment {
    private ImageView backArrowImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText middleNameEditText;
    private EditText positionEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText countryEditText;
    private EditText cityEditText;
    private EditText addressEditText;
    private Button saveBtn;
    private ProgressBar progressBar;

    private Animation bubbleAnimation;

    private NestedScrollView nestedScrollView;

    private User currentUser;

    private UserViewModel userViewModel;

    private NetworkConnectivity networkConnectivity;

    public PersonalDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = new User(
                SharedPrefs.getInstance(requireContext()).getLong(Constants.USER_ID),
                SharedPrefs.getInstance(requireContext()).getLong(Constants.CLUB_NUMBER),
                SharedPrefs.getInstance(requireContext()).getString(Constants.FIRST_NAME),
                SharedPrefs.getInstance(requireContext()).getString(Constants.LAST_NAME),
                SharedPrefs.getInstance(requireContext()).getString(Constants.MIDDLE_NAME),
                SharedPrefs.getInstance(requireContext()).getString(Constants.PHONE),
                SharedPrefs.getInstance(requireContext()).getString(Constants.EMAIL),
                new Country(
                        SharedPrefs.getInstance(requireContext()).getLong(Constants.COUNTRY_ID),
                        SharedPrefs.getInstance(requireContext()).getString(Constants.COUNTRY_TITLE),
                        SharedPrefs.getInstance(requireContext()).getString(Constants.COUNTRY_CODE),
                        null),
                SharedPrefs.getInstance(requireContext()).getString(Constants.CITY),
                SharedPrefs.getInstance(requireContext()).getString(Constants.POSITION),
                SharedPrefs.getInstance(requireContext()).getString(Constants.ADDRESS),
                SharedPrefs.getInstance(requireContext()).getString(Constants.PHOTO_URL));

        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());

        final UserViewModelFactory factory = new UserViewModelFactory(requireContext());
        userViewModel = new ViewModelProvider(getViewModelStore(), factory).get(UserViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_personal_data, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        firstNameEditText = root.findViewById(R.id.first_name_edit_text);
        lastNameEditText = root.findViewById(R.id.last_name_edit_text);
        middleNameEditText = root.findViewById(R.id.middle_name_edit_text);
        positionEditText = root.findViewById(R.id.position_edit_text);
        phoneEditText = root.findViewById(R.id.contact_number_edit_text);
        emailEditText = root.findViewById(R.id.email_edit_text);
        countryEditText = root.findViewById(R.id.country_spinner);
        cityEditText = root.findViewById(R.id.city_edit_text);
        addressEditText = root.findViewById(R.id.address_edit_text);
        saveBtn = root.findViewById(R.id.save_btn);
        progressBar = root.findViewById(R.id.progress_bar);

        bubbleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bubble);

        nestedScrollView = root.findViewById(R.id.scroll_layout);

        firstNameEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getFirstName()) ? getString(R.string.not_assigned) : currentUser.getFirstName());
        lastNameEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getLastName()) ? getString(R.string.not_assigned) : currentUser.getLastName());
        middleNameEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getMiddleName()) ? getString(R.string.not_assigned) : currentUser.getMiddleName());
        positionEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getPosition()) ? getString(R.string.not_assigned) : currentUser.getPosition());
        phoneEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getPhone()) ? getString(R.string.not_assigned) : currentUser.getPhone());
        emailEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.not_assigned) : currentUser.getEmail());
        countryEditText.setText(currentUser == null || currentUser.getCountry() == null ? getString(R.string.not_assigned): currentUser.getCountry().getTitle());
        cityEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getCity()) ? getString(R.string.not_assigned) : currentUser.getCity());
        addressEditText.setText(currentUser == null || TextUtils.isEmpty(currentUser.getAddress()) ? getString(R.string.not_assigned) : currentUser.getAddress());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backArrowImageView.setOnClickListener(v -> {
//            if (getActivity() != null) {
//                if (getActivity().getCurrentFocus() == null) {
//                    NavHostFragment.findNavController(this).popBackStack();
//                    return;
//                }
//                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//                getActivity().getCurrentFocus().clearFocus();
//            }
            NavHostFragment.findNavController(this).popBackStack();
        });

        firstNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(firstNameEditText, hasFocus, R.string.first_name_hint);
        });

        lastNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(lastNameEditText, hasFocus, R.string.last_name_hint);
        });

        middleNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(middleNameEditText, hasFocus, R.string.middle_name);
        });

        positionEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(positionEditText, hasFocus, R.string.position);
        });

        phoneEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(phoneEditText, hasFocus, R.string.phone_hint);
        });

        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(emailEditText, hasFocus, R.string.email_hint);
        });

        countryEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(countryEditText, hasFocus, R.string.country);
        });

        cityEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(cityEditText, hasFocus, R.string.city);
        });

        addressEditText.setOnFocusChangeListener((v, hasFocus) -> {
            UiUtils.setFocusChange(addressEditText, hasFocus, R.string.address);
        });

        countryEditText.setEnabled(false);
        positionEditText.setEnabled(false);
        phoneEditText.setEnabled(false);

        saveBtn.setOnClickListener(v -> {
            saveBtn.startAnimation(bubbleAnimation);

            final String firstName = firstNameEditText.getText().toString().trim();
            final String lastName = lastNameEditText.getText().toString().trim();
            final String middleName = middleNameEditText.getText().toString().trim();
            final String email = emailEditText.getText().toString().trim();
            final String city = cityEditText.getText().toString().trim();
            final String address = addressEditText.getText().toString().trim();

            networkConnectivity.checkInternetConnection(isConnected -> {
                if (!isConnected) {
                    NavHostFragment.findNavController(this).navigate(
                            PersonalDataFragmentDirections.actionPersonalDataFragmentToTransactionResultFragment()
                                    .setResult(false)
                                    .setType(Constants.RESULT_TYPE_PROFILE)
                                    .setErrorMessage(Constants.NO_INTERNET));
                    return;
                }
                userViewModel.saveUserData(firstName, lastName, middleName, email, city, address);
            });
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel.getSaveUserDataResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                final User userData = new User(
                        workInfo.getOutputData().getLong(Constants.USER_ID, 0L),
                        workInfo.getOutputData().getLong(Constants.CLUB_NUMBER, 0L),
                        workInfo.getOutputData().getString(Constants.FIRST_NAME),
                        workInfo.getOutputData().getString(Constants.LAST_NAME),
                        workInfo.getOutputData().getString(Constants.MIDDLE_NAME),
                        workInfo.getOutputData().getString(Constants.PHONE),
                        workInfo.getOutputData().getString(Constants.EMAIL),
                        new Country(
                                workInfo.getOutputData().getLong(Constants.COUNTRY_ID, 0L),
                                workInfo.getOutputData().getString(Constants.COUNTRY_TITLE),
                                workInfo.getOutputData().getString(Constants.COUNTRY_CODE),
                                null),
                        workInfo.getOutputData().getString(Constants.CITY),
                        workInfo.getOutputData().getString(Constants.POSITION),
                        workInfo.getOutputData().getString(Constants.ADDRESS),
                        workInfo.getOutputData().getString(Constants.PHOTO_URL));
                SharedPrefs.getInstance(requireContext()).saveUserData(userData);
                progressBar.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);

                Toast.makeText(requireContext(), R.string.success_data_safe, Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();

                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                progressBar.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);

                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();

                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.GONE);
        });

    }

    private static final String TAG = PersonalDataFragment.class.toString();
}