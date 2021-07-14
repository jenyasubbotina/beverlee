package uz.alex.its.beverlee.view.fragments;

import android.app.Activity;
import android.content.Intent;
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
import androidx.work.WorkInfo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.requestParams.VerifyTransferParams;
import uz.alex.its.beverlee.model.transaction.PurchaseModel;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.PurchaseAdapter;
import uz.alex.its.beverlee.view.dialog.PinDialog;
import uz.alex.its.beverlee.view.interfaces.PurchaseCallback;
import uz.alex.its.beverlee.viewmodel.PurchaseViewModel;
import uz.alex.its.beverlee.viewmodel.factory.PurchaseViewModelFactory;

public class PurchaseFragment extends Fragment implements PurchaseCallback {
    private ImageView backArrowImageView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PurchaseAdapter purchaseAdapter;
    private PurchaseViewModel purchaseViewModel;

    private static volatile int purchasePosition;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        purchasePosition = 0;

        final PurchaseViewModelFactory purchaseFactory = new PurchaseViewModelFactory(requireContext());
        purchaseViewModel = new ViewModelProvider(getViewModelStore(), purchaseFactory).get(PurchaseViewModel.class);

        purchaseViewModel.fetchPurchaseList();

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(PurchaseFragment.this).popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_purchase, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        final RecyclerView purchaseRecyclerView = root.findViewById(R.id.purchase_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        purchaseRecyclerView.setLayoutManager(layoutManager);
        purchaseRecyclerView.setHasFixedSize(true);
        purchaseAdapter = new PurchaseAdapter(requireContext(), this);
        purchaseRecyclerView.setAdapter(purchaseAdapter);

        UiUtils.hideBottomNav(requireActivity());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backArrowImageView.setOnClickListener(v -> {
            NavHostFragment.findNavController(PurchaseFragment.this).popBackStack();
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            purchaseViewModel.fetchPurchaseList();
        });
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        purchaseViewModel.getPurchaseList().observe(getViewLifecycleOwner(), purchaseList -> {
            swipeRefreshLayout.setRefreshing(false);
            purchaseAdapter.setPurchaseList(purchaseList);
        });

        purchaseViewModel.getMakePurchaseResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                purchaseAdapter.getPurchaseList().remove(purchasePosition);
                purchaseAdapter.notifyDataSetChanged();
                purchasePosition = 0;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Покупка успешно куплена", Toast.LENGTH_SHORT).show();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                return;
            }
            swipeRefreshLayout.setRefreshing(true);
        });

        purchaseViewModel.getDeletePurchaseResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                purchaseAdapter.getPurchaseList().remove(purchasePosition);
                purchaseAdapter.notifyDataSetChanged();
                purchasePosition = 0;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), "Покупка успешно удалена из корзины", Toast.LENGTH_SHORT).show();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), workInfo.getOutputData().getString(Constants.REQUEST_ERROR), Toast.LENGTH_SHORT).show();
                return;
            }
            swipeRefreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_VERIFY_PURCHASE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                purchaseViewModel.makePurchase(
                        data.getLongExtra(Constants.REQUEST_ID, 0L),
                        data.getStringExtra(Constants.PINCODE));
            }
        }
    }

    @Override
    public void onDeleteFromBasketClicked(final PurchaseModel.Purchase item, final int position) {
        purchasePosition = position;
        purchaseViewModel.deletePurchase(item.getId());
    }

    @Override
    public void onPurchaseBtnClicked(final PurchaseModel.Purchase item, final int position) {
        purchasePosition = position;
        final PinDialog dialog = PinDialog.newInstance(item.getId());
        dialog.setTargetFragment(this, Constants.REQUEST_CODE_VERIFY_PURCHASE);
        dialog.show(getParentFragmentManager().beginTransaction(), TAG);
    }

    private static final String TAG = PurchaseFragment.class.toString();
}