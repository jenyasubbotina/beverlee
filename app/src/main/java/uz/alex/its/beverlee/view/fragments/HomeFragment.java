package uz.alex.its.beverlee.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.WorkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.chart.LineChartItem;
import uz.alex.its.beverlee.model.news.NewsModel.News;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.view.LineChart;
import uz.alex.its.beverlee.view.activities.ProfileActivity;
import uz.alex.its.beverlee.view.adapters.ContactAdapter;
import uz.alex.its.beverlee.view.adapters.NewsAdapter;
import uz.alex.its.beverlee.view.interfaces.ContactCallback;
import uz.alex.its.beverlee.view.interfaces.NewsCallback;
import uz.alex.its.beverlee.viewmodel.ContactsViewModel;
import uz.alex.its.beverlee.viewmodel.NewsViewModel;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel_factory.ContactsViewModelFactory;
import uz.alex.its.beverlee.viewmodel_factory.NewsViewModelFactory;
import uz.alex.its.beverlee.viewmodel_factory.TransactionViewModelFactory;

public class HomeFragment extends Fragment implements ContactCallback, NewsCallback {
    /* pull to refresh */
    private SwipeRefreshLayout swipeRefreshLayout;

    /* header */
    private ImageView crownImageView;
    private ImageView bellImageView;

    /* body */
    private TextView currentBalanceTextView;
    private Button debitBtn;
    private Button withdrawBtn;
    private ProgressBar progressBar;

    /* monitoring */
    private TextView incomeOrExpenditureTextView;
    private TextView monthNameTextView;
    private TextView monthlyBalanceTextView;
    private LineChart lineChart;

    private View cardProfit;

    private FloatingActionButton fab;

    /* bottomSheet for contactList */
    private BottomNavigationView bottomNavigationView;
    private LinearLayout bottomSheetContacts;
    private TextView bottomSheetContactsTransfer;
    private TextView bottomSheetAddToFavs;
    private TextView bottomSheetDelete;
    private BottomSheetBehavior contactsSheetBehavior;

    /* contact list */
    private RecyclerView contactListRecyclerView;
    private ContactAdapter contactsAdapter;
    private TextView contactListEmptyTextView;

    /* selected contacts */
    private ContactAdapter.ContactHorizontalViewHolder selectedHolder = null;
    private boolean contactSelected = false;

    /* news */
    private RecyclerView newsMinRecyclerView;
    private NewsAdapter newsAdapter;

    /* viewModels */
    private TransactionViewModel transactionViewModel;
    private ContactsViewModel contactsViewModel;
    private NewsViewModel newsViewModel;

    private static volatile ContactData selectedContact;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedContact = null;

        final TransactionViewModelFactory transactionFactory = new TransactionViewModelFactory(requireContext());
        final ContactsViewModelFactory contactsFactory = new ContactsViewModelFactory(requireContext());
        final NewsViewModelFactory newsFactory = new NewsViewModelFactory(requireContext());

        transactionViewModel = new ViewModelProvider(getViewModelStore(), transactionFactory).get(TransactionViewModel.class);
        contactsViewModel = new ViewModelProvider(getViewModelStore(), contactsFactory).get(ContactsViewModel.class);
        newsViewModel = new ViewModelProvider(getViewModelStore(), newsFactory).get(NewsViewModel.class);

        transactionViewModel.fetchCurrentBalance();

        transactionViewModel.fetchMonthlyTransactionList(null, Calendar.getInstance().get(Calendar.MONTH), null);

        transactionViewModel.fetchMonthlyBalance(Calendar.getInstance().get(Calendar.MONTH));

        contactsViewModel.fetchContactList(null, null);

        newsViewModel.fetchNews(null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        /* pull to refresh */
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);

        /* header */
        crownImageView = root.findViewById(R.id.crown_image_view);
        bellImageView = root.findViewById(R.id.bell_image_view);

        /* body */
        currentBalanceTextView = root.findViewById(R.id.current_balance_text_view);
        debitBtn = root.findViewById(R.id.replenish_btn);
        withdrawBtn = root.findViewById(R.id.withdraw_btn);
        progressBar = root.findViewById(R.id.progress_bar);

        /* monitoring */
        incomeOrExpenditureTextView = root.findViewById(R.id.income_or_expenditure_text_view);
        monthNameTextView = root.findViewById(R.id.month_name_text_view);
        monthlyBalanceTextView = root.findViewById(R.id.monthly_balance_text_view);
        lineChart = root.findViewById(R.id.line_chart);
        cardProfit = root.findViewById(R.id.card_profit);

        /* contacts */
        contactListRecyclerView = root.findViewById(R.id.contact_recycler_view);
        contactListEmptyTextView = root.findViewById(R.id.contact_list_empty_text_view);

        /* news */
        newsMinRecyclerView = root.findViewById(R.id.news_min_recycler_view);

        /* news */
        final LinearLayoutManager newsLayoutManager = new LinearLayoutManager(requireContext());
        newsLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        newsAdapter = new NewsAdapter(requireContext(), this, NewsAdapter.TYPE_MIN);
        newsMinRecyclerView.setLayoutManager(newsLayoutManager);
        newsMinRecyclerView.setAdapter(newsAdapter);
        newsMinRecyclerView.setNestedScrollingEnabled(false);

        /* contacts */
        final LinearLayoutManager contactsLayoutManager = new LinearLayoutManager(requireContext());
        contactsLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        contactsAdapter = new ContactAdapter(requireContext(), this, ContactAdapter.TYPE_HORIZONTAL);
        contactListRecyclerView.setLayoutManager(contactsLayoutManager);
        contactListRecyclerView.setAdapter(contactsAdapter);
        contactListRecyclerView.setNestedScrollingEnabled(false);

//        contactListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int visibleItemCount = contactsLayoutManager.getChildCount();
//                int totalItemCount = contactsLayoutManager.getItemCount();
//                int firstVisibleItemPosition = contactsLayoutManager.findFirstVisibleItemPosition();
//
//                if (!isLoading() && !isLastPage()) {
//                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                            && firstVisibleItemPosition >= 0) {
//                        loadMoreItems();
//                    }
//                }
//            }
//        });

        /* bottom navigation */
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        fab = requireActivity().findViewById(R.id.floating_btn);
        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        /* bottom sheet */
        bottomSheetContacts = requireActivity().findViewById(R.id.bottom_sheet_contacts);
        bottomSheetContactsTransfer = requireActivity().findViewById(R.id.contacts_transfer);
        bottomSheetAddToFavs = requireActivity().findViewById(R.id.add_to_favorites);
        bottomSheetDelete = requireActivity().findViewById(R.id.delete_contact);

        contactsSheetBehavior = BottomSheetBehavior.from(bottomSheetContacts);
        contactsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardProfit.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_monitoringFragment);
            bottomNavigationView.setSelectedItemId(R.id.navigation_monitoring);
        });

        fab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_transferFragment);
        });

        crownImageView.setOnClickListener(v -> {
            final Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(profileIntent);
        });

        bellImageView.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_notificationsFragment);
        });

        debitBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_debitFragment);
        });

        withdrawBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_withdrawalTypesFragment);
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

        bottomSheetContactsTransfer.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(
                    HomeFragmentDirections.actionHomeFragmentToTransferFragment()
                            .setContactId(selectedContact.getId()));
            if (contactSelected) {
                deselectContact();
            }
        });

        bottomSheetAddToFavs.setOnClickListener(v -> {
            contactsViewModel.addToFavs(selectedContact);
        });

        bottomSheetDelete.setOnClickListener(v -> {
            contactsViewModel.deleteContact(selectedContact.getId());

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            transactionViewModel.fetchCurrentBalance();
            transactionViewModel.fetchMonthlyTransactionList(null, Calendar.getInstance().get(Calendar.MONTH), null);
            transactionViewModel.fetchMonthlyBalance(Calendar.getInstance().get(Calendar.MONTH));
            contactsViewModel.fetchContactList(null, null);
            newsViewModel.fetchNews(null, null);
        });

        lineChart.getThumb().mutate().setAlpha(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        transactionViewModel.getBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                currentBalanceTextView.setText(getString(R.string.current_balance, String.valueOf(balance.getBalance())));
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        contactsViewModel.getContactList().observe(getViewLifecycleOwner(), contactList -> {
            if (contactList != null && !contactList.isEmpty()) {
                contactListEmptyTextView.setVisibility(View.GONE);
            }
            else {
                contactListEmptyTextView.setVisibility(View.VISIBLE);
            }
            contactsAdapter.setContactList(contactList);
        });

        newsViewModel.getNewsList().observe(getViewLifecycleOwner(), newsList -> {
            newsAdapter.setNewsList(newsList);
            newsAdapter.notifyDataSetChanged();
        });

        contactsViewModel.getAddToFavsResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                Toast.makeText(requireContext(), "Добавлено в Избранное", Toast.LENGTH_SHORT).show();
                bottomSheetAddToFavs.setEnabled(true);
                deselectContact();
                return;
            }
            if (workInfo.getState() == WorkInfo.State.FAILED || workInfo.getState() == WorkInfo.State.CANCELLED) {
                Toast.makeText(requireContext(), "Контакт уже в Избранных", Toast.LENGTH_SHORT).show();
                bottomSheetAddToFavs.setEnabled(true);
                deselectContact();
                return;
            }
            bottomSheetAddToFavs.setEnabled(false);
        });

        contactsViewModel.getDeleteContactResult(requireContext()).observe(getViewLifecycleOwner(), workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                contactsAdapter.getContactList().remove(selectedContact);
                contactsAdapter.notifyDataSetChanged();

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

        transactionViewModel.getMonthlyTurnover().observe(getViewLifecycleOwner(), monthlyBalance -> {
            if (monthlyBalance > 0) {
                monthlyBalanceTextView.setText(getString(R.string.monthly_balance, "+", monthlyBalance));
                incomeOrExpenditureTextView.setText(getString(R.string.income_or_expenditure, getString(R.string.income)));
                return;
            }
            monthlyBalanceTextView.setText(getString(R.string.monthly_balance, "", monthlyBalance));
            incomeOrExpenditureTextView.setText(getString(R.string.income_or_expenditure, getString(R.string.expenditure)));
        });

        transactionViewModel.getCurrentMonthNumber().observe(getViewLifecycleOwner(), currentMonthNumber -> {
            if (currentMonthNumber < 0 || currentMonthNumber > 11) {
                return;
            }
            monthNameTextView.setText(getString(R.string.month_name, getString(transactionViewModel.getMonthName(currentMonthNumber + 1))));
        });

        transactionViewModel.getLineChartData().observe(getViewLifecycleOwner(), statisticsData -> {
            if ((statisticsData.getBonusAmount() <= 0 || Float.isNaN(statisticsData.getBonusAmount()))
                    && (statisticsData.getPurchaseAmount() <= 0 || Float.isNaN(statisticsData.getPurchaseAmount()))
                    && (statisticsData.getReceiptAmount() <= 0 || Float.isNaN(statisticsData.getReceiptAmount()))
                    && (statisticsData.getTransferAmount() <= 0 || Float.isNaN(statisticsData.getTransferAmount()))
                    && (statisticsData.getReplenishAmount() <= 0 || Float.isNaN(statisticsData.getReplenishAmount()))
                    && (statisticsData.getWithdrawalAmount() <= 0 || Float.isNaN(statisticsData.getWithdrawalAmount()))) {
                lineChart.initData(null);
                lineChart.invalidate();
                return;
            }
            final List<LineChartItem> entryList = new ArrayList<>();

            if (statisticsData.getBonusAmount() > 0 && !Float.isNaN(statisticsData.getBonusAmount())) {
                entryList.add(new LineChartItem(ResourcesCompat.getColor(getResources(), R.color.colorOrange, null), statisticsData.getBonusAmount()*100));
            }
            if (statisticsData.getPurchaseAmount() > 0 && !Float.isNaN(statisticsData.getPurchaseAmount())) {
                entryList.add(new LineChartItem(ResourcesCompat.getColor(getResources(), R.color.colorPurple, null), statisticsData.getPurchaseAmount()*100));
            }
            if (statisticsData.getReceiptAmount() > 0 && !Float.isNaN(statisticsData.getReceiptAmount())) {
                entryList.add(new LineChartItem(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null), statisticsData.getReceiptAmount()*100));
            }
            if (statisticsData.getTransferAmount() > 0 && !Float.isNaN(statisticsData.getTransferAmount())) {
                entryList.add(new LineChartItem(ResourcesCompat.getColor(getResources(), R.color.colorGrey, null), statisticsData.getTransferAmount()*100));
            }
            if (statisticsData.getReplenishAmount() > 0 && !Float.isNaN(statisticsData.getReplenishAmount())) {
                entryList.add(new LineChartItem(ResourcesCompat.getColor(getResources(), R.color.colorGreenBright, null), statisticsData.getReplenishAmount()*100));
            }
            if (statisticsData.getWithdrawalAmount() > 0 && !Float.isNaN(statisticsData.getWithdrawalAmount())) {
                entryList.add(new LineChartItem(ResourcesCompat.getColor(getResources(), R.color.colorDarkGrey, null), statisticsData.getWithdrawalAmount()*100));
            }

            lineChart.initData(entryList);
            lineChart.invalidate();
        });
    }

    @Override
    public void expandNewsItem(int position) {

    }

    @Override
    public void onNewsSelected(int position, News news) {
        NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_newsFragment);
    }

    @Override
    public void onContactSelected(final ContactData contact, final ContactAdapter.ContactViewHolder holder) {
        if (!contactSelected) {
            contactSelected = true;
            selectedContact = contact;
            selectedHolder = (ContactAdapter.ContactHorizontalViewHolder) holder;
            selectedHolder.checkImageView.setVisibility(View.VISIBLE);
            contactsSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }
        deselectContact();
    }

    private void deselectContact() {
        if (contactSelected) {
            contactSelected = false;
            selectedContact = null;
            final ImageView selectedCheckImageView = selectedHolder.checkImageView;
            selectedCheckImageView.setVisibility(View.INVISIBLE);
            contactsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private static final String TAG = HomeFragment.class.toString();
}
