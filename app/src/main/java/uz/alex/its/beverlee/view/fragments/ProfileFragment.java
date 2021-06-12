package uz.alex.its.beverlee.view.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import uz.alex.its.beverlee.storage.FileManager;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.ImageProcessor;
import uz.alex.its.beverlee.viewmodel.UserViewModel;
import uz.alex.its.beverlee.viewmodel_factory.UserViewModelFactory;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    /* pull to refresh */
    private SwipeRefreshLayout swipeRefreshLayout;

    /* header */
    private ImageView backArrowImageView;

    /* views */
    private TextView fullNameTextView;
    private TextView idTextView;
    private TextView clubNumberTextView;
    private ImageView avatarImageView;
    private ImageView changeProfilePhotoImageView;
    private Button editBtn;
    private Button logoutBtn;

    private Animation bubbleAnimation;

    /*bottom sheet*/
    private LinearLayout bottomSheet;
    private TextView bottomSheetPersonalData;
    private TextView bottomSheetNotificationsSettings;
    private TextView bottomSheetChangePassword;
    private TextView bottomSheetChangePin;
    private TextView bottomSheetDeleteAvatar;
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private ConstraintLayout parentLayout;
    private ProgressBar progressBar;

    private boolean bottomSheetHidden = true;
    private boolean imageSelected = false;

    private UserViewModel userViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final UserViewModelFactory userFactory = new UserViewModelFactory(requireContext());
        userViewModel = new ViewModelProvider(getViewModelStore(), userFactory).get(UserViewModel.class);

        userViewModel.fetchUserData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        fullNameTextView = root.findViewById(R.id.profile_name_text_view);
        idTextView = root.findViewById(R.id.identifier_text_view);
        clubNumberTextView = root.findViewById(R.id.club_number_text_view);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        avatarImageView = root.findViewById(R.id.profile_photo_image_view);
        changeProfilePhotoImageView = root.findViewById(R.id.change_profile_photo_image_view);
        editBtn = root.findViewById(R.id.edit_btn);
        logoutBtn = root.findViewById(R.id.logout_btn);
        parentLayout = root.findViewById(R.id.parent_layout);

        bubbleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bubble);
        progressBar = root.findViewById(R.id.progress_bar);

        bottomSheet = requireActivity().findViewById(R.id.bottom_sheet_profile);
        bottomSheetPersonalData = requireActivity().findViewById(R.id.personal_data_option);
        bottomSheetNotificationsSettings = requireActivity().findViewById(R.id.notifications_option);
        bottomSheetChangePassword = requireActivity().findViewById(R.id.change_password_option);
        bottomSheetChangePin = requireActivity().findViewById(R.id.change_pin_option);
        bottomSheetDeleteAvatar = requireActivity().findViewById(R.id.delete_avatar_option);

        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutBtn.setOnClickListener(v -> { SharedPrefs.getInstance(requireContext()).logout(requireContext()); });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        editBtn.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        Log.i(TAG, "onStateChanged: STATE_COLLAPSED");
                        break;
                    }
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        Log.i(TAG, "onStateChanged: STATE_DRAGGING");
                        break;
                    }
                    case BottomSheetBehavior.STATE_SETTLING: {
                        if (bottomSheetHidden) {
                            editBtn.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    case BottomSheetBehavior.STATE_HALF_EXPANDED: {
                        Log.i(TAG, "onStateChanged: STATE_HALF_EXPANDED");
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        changeProfilePhotoImageView.setOnClickListener(v -> {
            final Intent pickImageIntent = new Intent();
            pickImageIntent.setAction(Intent.ACTION_GET_CONTENT);
            pickImageIntent.setType("image/*");
            startActivityForResult(Intent.createChooser(pickImageIntent, getString(R.string.pick_image)), Constants.REQUEST_CODE_PICK_IMAGE);
        });

        bottomSheetPersonalData.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_personalDataFragment);
            editBtn.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            imageSelected = false;
            bottomSheetHidden = true;
        });

        bottomSheetNotificationsSettings.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_notificationSettingsFragment);
            editBtn.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            imageSelected = false;
            bottomSheetHidden = true;
        });

        bottomSheetChangePassword.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_changePasswordFragment);
            editBtn.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            imageSelected = false;
            bottomSheetHidden = true;
        });

        bottomSheetChangePin.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_changePinFragment);
            editBtn.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            imageSelected = false;
            bottomSheetHidden = true;
        });

        bottomSheetDeleteAvatar.setOnClickListener(v -> {
            userViewModel.deleteAvatar();
            editBtn.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            imageSelected = false;
            bottomSheetHidden = true;
        });

        parentLayout.setOnClickListener(v -> {
            Log.i(TAG, "parentLayoutClick: " + imageSelected + " bottomSheet: " + bottomSheetHidden);
            if (imageSelected) {
                imageSelected = false;
                bottomSheetHidden = true;
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        editBtn.setOnClickListener(v -> {
            editBtn.startAnimation(bubbleAnimation);
            editBtn.postOnAnimationDelayed(() -> {
                Log.i(TAG, "editBtnClick: " + imageSelected + " bottomSheet: " + bottomSheetHidden);
                if (!imageSelected) {
                    imageSelected = true;
                    bottomSheetHidden = false;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return;
                }
                imageSelected = false;
                bottomSheetHidden = true;
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }, 100);
        });

        backArrowImageView.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            userViewModel.fetchUserData();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel.getUserData().observe(getViewLifecycleOwner(), user -> {
            fullNameTextView.setText(user == null ? getString(R.string.not_assigned) : getString(R.string.profile_name, user.getFirstName(), user.getLastName()));
            idTextView.setText(user == null || user.getId() <= 0 ? getString(R.string.identifier, getString(R.string.not_assigned)) : getString(R.string.identifier, String.valueOf(user.getId())
            ));
            clubNumberTextView.setText(user == null || user.getClubNumber() <= 0 ? getString(R.string.club_number, getString(R.string.not_assigned)) : getString(R.string.club_number, String.valueOf(user.getClubNumber())));

            if (user == null) {
                return;
            }

            SharedPrefs.getInstance(requireContext()).saveUserData(user);

            if (user.getPhotoUrl() != null) {
                avatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Picasso.get()
                        .load(getString(R.string.dev_server_url) + user.getPhotoUrl())
                        .noPlaceholder()
                        .fit()
                        .centerCrop()
                        .into(avatarImageView);
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        userViewModel.getDeleteAvatarResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                progressBar.setVisibility(View.GONE);
                editBtn.setEnabled(true);
                backArrowImageView.setEnabled(true);
                changeProfilePhotoImageView.setEnabled(true);

                avatarImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                avatarImageView.setImageBitmap(null);
                avatarImageView.setImageResource(R.drawable.ic_crown_big);

                Toast.makeText(requireContext(), "Фото успешно удалено", Toast.LENGTH_SHORT).show();

                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                progressBar.setVisibility(View.GONE);
                editBtn.setEnabled(true);
                backArrowImageView.setEnabled(true);
                changeProfilePhotoImageView.setEnabled(true);

                Toast.makeText(requireContext(), "Произошла ошибка...", Toast.LENGTH_SHORT).show();

                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            editBtn.setEnabled(false);
            backArrowImageView.setEnabled(false);
            changeProfilePhotoImageView.setEnabled(false);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            Log.e(TAG, "onActivityResult(): CANCELLED");
            return;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE) {
                if (data == null) {
                    Log.e(TAG, "onActivityResult(): data is NULL");
                    return;
                }
                final Uri selectedImageURI = data.getData();
                final Bitmap rotatedBitmap =
                        ImageProcessor.rotate(
                                ImageProcessor.scaleDown(
                                ImageProcessor.getBitmapFromUri(
                                        requireContext(),
                                        selectedImageURI)),
                                requireContext(), selectedImageURI);
                avatarImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                avatarImageView.setImageBitmap(rotatedBitmap);

                userViewModel.uploadAvatarAsync(FileManager.getInstance(requireContext()).storeBitmap(rotatedBitmap));
            }
        }
    }

    private static final String TAG = ProfileFragment.class.toString();
}