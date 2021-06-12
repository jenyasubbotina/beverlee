package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel.WithdrawalType;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.WithdrawalTypeAdapter;
import uz.alex.its.beverlee.view.interfaces.WithdrawalTypeCallback;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel_factory.TransactionViewModelFactory;

public class WithdrawalTypesFragment extends Fragment implements WithdrawalTypeCallback {
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView backArrowImageView;

    private WithdrawalTypeAdapter withdrawalTypeAdapter;

    private TransactionViewModel transactionViewModel;

    public WithdrawalTypesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TransactionViewModelFactory transactionFactory = new TransactionViewModelFactory(requireContext());
        transactionViewModel = new ViewModelProvider(getViewModelStore(), transactionFactory).get(TransactionViewModel.class);
        transactionViewModel.fetchWithdrawalTypes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_withdrawal_types, container, false);

        UiUtils.hideBottomNav(requireActivity());

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);

        final RecyclerView withdrawalTypeRecyclerView = root.findViewById(R.id.withdrawal_type_recycler_view);
        withdrawalTypeAdapter = new WithdrawalTypeAdapter(requireContext(), this);
        withdrawalTypeRecyclerView.setHasFixedSize(false);

        final GridLayoutManager withdrawalTypeLayoutManager = new GridLayoutManager(requireContext(), 3);
        withdrawalTypeLayoutManager.setOrientation(RecyclerView.VERTICAL);
        withdrawalTypeRecyclerView.setLayoutManager(withdrawalTypeLayoutManager);
        withdrawalTypeRecyclerView.setAdapter(withdrawalTypeAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backArrowImageView.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            transactionViewModel.fetchWithdrawalTypes();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        transactionViewModel.getWithdrawalTypeList().observe(getViewLifecycleOwner(), withdrawalTypeList -> {
            if (withdrawalTypeList != null) {
                withdrawalTypeAdapter.setWithdrawalTypeList(withdrawalTypeList);
                withdrawalTypeAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onWithdrawalTypeSelected(final WithdrawalType withdrawalType) {
        final WithdrawalTypesFragmentDirections.ActionWithdrawalTypesFragmentToWithdrawalFragment action = WithdrawalTypesFragmentDirections.actionWithdrawalTypesFragmentToWithdrawalFragment();
        action.setMethod(withdrawalType.getMethod());
        action.setType(withdrawalType.getType());
        action.setCommission(withdrawalType.getCommission());
        NavHostFragment.findNavController(this).navigate(action);
    }

    private static final String TAG = WithdrawalTypesFragment.class.toString();
}