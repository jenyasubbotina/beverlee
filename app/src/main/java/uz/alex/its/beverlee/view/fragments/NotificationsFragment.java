package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.NotificationAdapter;
import uz.alex.its.beverlee.view.interfaces.NotificationCallback;
import uz.alex.its.beverlee.viewmodel.NotificationViewModel;
import uz.alex.its.beverlee.viewmodel.factory.NotificationViewModelFactory;

public class NotificationsFragment extends Fragment implements NotificationCallback {
    private ImageView backArrowImageView;
    private NotificationAdapter adapter;

    private NotificationViewModel notificationViewModel;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final NotificationViewModelFactory notificationFactory = new NotificationViewModelFactory(requireContext());
        notificationViewModel = new ViewModelProvider(getViewModelStore(), notificationFactory).get(NotificationViewModel.class);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(NotificationsFragment.this).popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        final RecyclerView recyclerView = root.findViewById(R.id.notification_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        adapter = new NotificationAdapter(requireContext(), this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        UiUtils.hideBottomNav(requireActivity());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backArrowImageView.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        notificationViewModel.selectAllPushList().observe(getViewLifecycleOwner(), notificationList -> {
            adapter.setNotificationList(notificationList);
        });
    }

    @Override
    public void onNotificationTapped(final Push item, final int position) {
        notificationViewModel.updateStatusRead(item.getNotificationId());
        adapter.notifyItemChanged(position);
    }

    private static final String TAG = NotificationsFragment.class.toString();
}