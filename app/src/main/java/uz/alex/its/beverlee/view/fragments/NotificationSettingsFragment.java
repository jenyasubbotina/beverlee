package uz.alex.its.beverlee.view.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.push.NotifyManager;
import uz.alex.its.beverlee.utils.AppExecutors;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.NetworkConnectivity;
import uz.alex.its.beverlee.viewmodel.UserViewModel;
import uz.alex.its.beverlee.viewmodel_factory.UserViewModelFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class NotificationSettingsFragment extends Fragment {
    /* pull to refresh */
    private SwipeRefreshLayout swipeRefreshLayout;

    /* header */
    private ImageView backArrowImageView;

    /* views */
    private CheckBox notifyNewsCheckBox;
    private CheckBox notifyBonusesCheckBox;
    private CheckBox notifyIncomeCheckBox;
    private CheckBox notifyPurchaseCheckBox;
    private CheckBox notifyReplenishCheckBox;
    private CheckBox notifyWithdrawalCheckBox;

    private TextView notifyNewsTextView;
    private TextView notifyBonusesTextView;
    private TextView notifyIncomeTextView;
    private TextView notifyPurchaseTextView;
    private TextView notifyReplenishTextView;
    private TextView notifyWithdrawalTextView;

    private TextView errorTextView;
    private ProgressBar progressBar;
    private Button saveBtn;

    private Animation bubbleAnimation;

    private NotifyManager notifyManager;

    private UserViewModel userViewModel;

    private NetworkConnectivity networkConnectivity;

    public NotificationSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        notifyManager = new NotifyManager(requireContext());

        networkConnectivity = new NetworkConnectivity(requireContext(), AppExecutors.getInstance());

        final UserViewModelFactory userFactory = new UserViewModelFactory(requireContext());

        userViewModel = new ViewModelProvider(getViewModelStore(), userFactory).get(UserViewModel.class);
        userViewModel.fetchNotificationSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_notification_settings, container, false);

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        errorTextView = root.findViewById(R.id.notification_settings_error_text_view);
        progressBar = root.findViewById(R.id.progress_bar);
        saveBtn = root.findViewById(R.id.save_btn);

        notifyNewsCheckBox = root.findViewById(R.id.notify_news_check_box);
        notifyBonusesCheckBox = root.findViewById(R.id.notify_bonuses_check_box);
        notifyIncomeCheckBox = root.findViewById(R.id.notify_income_check_box);
        notifyPurchaseCheckBox = root.findViewById(R.id.notify_purchase_check_box);
        notifyReplenishCheckBox = root.findViewById(R.id.notify_replenish_check_box);
        notifyWithdrawalCheckBox = root.findViewById(R.id.notify_withdrawal_check_box);

        notifyNewsTextView = root.findViewById(R.id.notify_news_text_view);
        notifyNewsTextView.setOnClickListener(v -> notifyNewsCheckBox.performClick());

        notifyBonusesTextView = root.findViewById(R.id.notify_bonuses_text_view);
        notifyBonusesTextView.setOnClickListener(v -> notifyBonusesCheckBox.performClick());

        notifyIncomeTextView = root.findViewById(R.id.notify_income_text_view);
        notifyIncomeTextView.setOnClickListener(v -> notifyIncomeCheckBox.performClick());

        notifyPurchaseTextView = root.findViewById(R.id.notify_purchase_text_view);
        notifyPurchaseTextView.setOnClickListener(v -> notifyPurchaseCheckBox.performClick());

        notifyReplenishTextView = root.findViewById(R.id.notify_replenish_text_view);
        notifyReplenishTextView.setOnClickListener(v -> notifyReplenishCheckBox.performClick());

        notifyWithdrawalTextView = root.findViewById(R.id.notify_withdrawal_text_view);
        notifyWithdrawalTextView.setOnClickListener(v -> notifyWithdrawalCheckBox.performClick());

                bubbleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bubble);

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

        saveBtn.setOnClickListener(v -> {
            saveBtn.startAnimation(bubbleAnimation);

            networkConnectivity.checkInternetConnection(isConnected -> {
                if (!isConnected) {
                    NavHostFragment.findNavController(this).navigate(
                            NotificationSettingsFragmentDirections.actionNotificationSettingsFragmentToTransactionResultFragment()
                                    .setResult(false)
                                    .setType(Constants.RESULT_TYPE_PROFILE)
                                    .setErrorMessage(Constants.NO_INTERNET));
                    return;
                }
                userViewModel.saveNotificationSettings(
                        notifyNewsCheckBox.isChecked(),
                        notifyBonusesCheckBox.isChecked(),
                        notifyIncomeCheckBox.isChecked(),
                        notifyPurchaseCheckBox.isChecked(),
                        notifyReplenishCheckBox.isChecked(),
                        notifyWithdrawalCheckBox.isChecked());
            });
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            userViewModel.fetchNotificationSettings();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel.getNotificationSettings().observe(getViewLifecycleOwner(), notificationSettings -> {
            if (notificationSettings == null) {
                errorTextView.setVisibility(View.VISIBLE);
                saveBtn.setVisibility(View.GONE);
                notifyNewsCheckBox.setVisibility(View.GONE);
                notifyBonusesCheckBox.setVisibility(View.GONE);
                notifyIncomeCheckBox.setVisibility(View.GONE);
                notifyPurchaseCheckBox.setVisibility(View.GONE);
                notifyReplenishCheckBox.setVisibility(View.GONE);
                notifyWithdrawalCheckBox.setVisibility(View.GONE);
                return;
            }
            saveBtn.setVisibility(View.VISIBLE);
            notifyNewsCheckBox.setVisibility(View.VISIBLE);
            notifyBonusesCheckBox.setVisibility(View.VISIBLE);
            notifyIncomeCheckBox.setVisibility(View.VISIBLE);
            notifyPurchaseCheckBox.setVisibility(View.VISIBLE);
            notifyReplenishCheckBox.setVisibility(View.VISIBLE);
            notifyWithdrawalCheckBox.setVisibility(View.VISIBLE);

            notifyNewsCheckBox.setChecked(notificationSettings.getNews() >= 1);
            notifyBonusesCheckBox.setChecked(notificationSettings.getBonus() >= 1);
            notifyIncomeCheckBox.setChecked(notificationSettings.getIncome() >= 1);
            notifyPurchaseCheckBox.setChecked(notificationSettings.getPurchase() >= 1);
            notifyReplenishCheckBox.setChecked(notificationSettings.getReplenish() >= 1);
            notifyWithdrawalCheckBox.setChecked(notificationSettings.getWithdrawal() >= 1);

            swipeRefreshLayout.setRefreshing(false);
        });

        userViewModel.getSaveNotificationSettingsResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                progressBar.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.GONE);

                Toast.makeText(requireContext(), R.string.success_data_safe, Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(this).popBackStack();

                return;
            }
            if (workInfo.getState() == WorkInfo.State.CANCELLED || workInfo.getState() == WorkInfo.State.FAILED) {
                progressBar.setVisibility(View.GONE);
                saveBtn.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.VISIBLE);
                errorTextView.setText(workInfo.getOutputData().getString(Constants.REQUEST_ERROR));
                return;
            }
            errorTextView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.GONE);
        });
    }
}