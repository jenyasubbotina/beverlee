package uz.alex.its.beverlee.view.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Color;
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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.transaction.TransactionModel;
import uz.alex.its.beverlee.model.transaction.TransactionParams;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.utils.DateFormatter;
import uz.alex.its.beverlee.view.adapters.TransactionAdapter;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel.factory.TransactionViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitoringFragment extends Fragment {
    /* pull to refresh */
    private SwipeRefreshLayout swipeRefreshLayout;

    /* header */
    private EditText searchFieldEditText;
    private RadioGroup radioGroup;
    private RadioButton incomeRadioBtn;
    private RadioButton expenditureRadioBtn;

    /* monitoring chart */
    private View transactionsCard;

    private TextView incomeOrExpenditureTextView;
    private TextView currentMonthAndYearTextView;
    private TextView monthlyBalanceTextView;

    private PieChart pieChart;
    private ImageView prevChartImageView;
    private ImageView nextChartImageView;
    private TextView receiptOrtransferTextView;
    private TextView replenishedOrWithdrawalTextView;
    private TextView bonusOrPurchaseTextView;

    /* transactions */
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;

    private FloatingActionButton fab;
    private Animation shrinkAnim;

    private boolean chartHidden = false;

    private TransactionViewModel transactionViewModel;

    public MonitoringFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TransactionViewModelFactory transactionFactory = new TransactionViewModelFactory(requireContext());
        transactionViewModel = new ViewModelProvider(getViewModelStore(), transactionFactory).get(TransactionViewModel.class);

        TransactionParams params = null;

        if (getArguments() != null
                && MonitoringFragmentArgs.fromBundle(getArguments()).getYear() > 0
                && MonitoringFragmentArgs.fromBundle(getArguments()).getMonth() >= 1
                && MonitoringFragmentArgs.fromBundle(getArguments()).getMonth() <= 12
                && MonitoringFragmentArgs.fromBundle(getArguments()).getFirstDay() >= 1
                && MonitoringFragmentArgs.fromBundle(getArguments()).getLastDay() <= 31) {
            params = new TransactionParams(
                    true,
                    MonitoringFragmentArgs.fromBundle(getArguments()).getYear(),
                    MonitoringFragmentArgs.fromBundle(getArguments()).getMonth(),
                    MonitoringFragmentArgs.fromBundle(getArguments()).getFirstDay(),
                    MonitoringFragmentArgs.fromBundle(getArguments()).getLastDay(),
                    MonitoringFragmentArgs.fromBundle(getArguments()).getTransactionTypeId(),
                    null);
        }
        else {
            params = new TransactionParams(
                    true,
                    LocalDateTime.now().getYear(),
                    LocalDateTime.now().getMonthValue(),
                    1,
                    DateFormatter.getLastDayOfMonthAndYear(LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue()),
                    0,
                    null);
        }
        transactionViewModel.setTransactionParams(params);
        transactionViewModel.fetchTransactionList();
        transactionViewModel.fetchMonthlyBalance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_monitoring, container, false);

        /* pull to refresh */
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);

        /* header */
        searchFieldEditText = root.findViewById(R.id.monitoring_search_edit_text);
        radioGroup = root.findViewById(R.id.radio_group);
        incomeRadioBtn = root.findViewById(R.id.income_radio_btn);
        expenditureRadioBtn = root.findViewById(R.id.expenditure_radio_btn);

        /* bottom navigation */
        final BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav);
        fab = requireActivity().findViewById(R.id.floating_btn);
        bottomNavigationView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        /* monitoring chart */
        transactionsCard = root.findViewById(R.id.transactions_card);
        incomeOrExpenditureTextView = root.findViewById(R.id.income_or_expenditure_text_view);
        currentMonthAndYearTextView = root.findViewById(R.id.month_and_year_text_view);
        monthlyBalanceTextView = root.findViewById(R.id.monthly_balance_text_view);
        prevChartImageView = root.findViewById(R.id.prev_chart_image_view);
        nextChartImageView = root.findViewById(R.id.next_chart_image_view);
        receiptOrtransferTextView = root.findViewById(R.id.received_text_view);
        replenishedOrWithdrawalTextView = root.findViewById(R.id.debited_text_view);
        bonusOrPurchaseTextView = root.findViewById(R.id.bonus_text_view);

        shrinkAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shrink_vertically);

        fab = requireActivity().findViewById(R.id.floating_btn);

        /* transactions */
        transactionRecyclerView = root.findViewById(R.id.transaction_recycler_view);
        transactionAdapter = new TransactionAdapter(requireContext());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        transactionRecyclerView.setNestedScrollingEnabled(false);
        transactionRecyclerView.setAdapter(transactionAdapter);
        transactionRecyclerView.setLayoutManager(layoutManager);

        pieChart = root.findViewById(R.id.pie_chart);
        pieChart.setNoDataText(getString(R.string.monitoring_no_data));
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawMarkers(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawCenterText(false);

        pieChart.setTransparentCircleColor(Color.parseColor("#545351"));
        pieChart.setTransparentCircleRadius(100);
        pieChart.setTransparentCircleAlpha(0);

        pieChart.setTouchEnabled(false);
        pieChart.setHoleRadius(65);
        pieChart.setHoleColor(Color.parseColor("#545351"));

        incomeColorArr = new int[] {
                ResourcesCompat.getColor(getResources(), R.color.colorOrange, null),
                ResourcesCompat.getColor(getResources(), R.color.colorWhite, null),
                ResourcesCompat.getColor(getResources(), R.color.colorGreenBright, null) };

        expenditureColorArr = new int[] {
                ResourcesCompat.getColor(getResources(), R.color.colorPurple, null),
                ResourcesCompat.getColor(getResources(), R.color.colorGrey, null),
                ResourcesCompat.getColor(getResources(), R.color.colorDarkGrey, null) };

        monthlyBalanceTextView.setText(getString(R.string.monthly_summary, "+", 0.0));
        bonusOrPurchaseTextView.setText(getString(R.string.monthly_balance, "Бонусы", 0.0));
        receiptOrtransferTextView.setText(getString(R.string.monthly_balance, "Получено", 0.0));
        replenishedOrWithdrawalTextView.setText(getString(R.string.monthly_balance, "Пополнение", 0.0));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        incomeOrExpenditureTextView.setText(getString(R.string.income_or_expenditure, getString(R.string.income)));

        fab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_monitoringFragment_to_transferFragment);
        });

        /* header */
        searchFieldEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchFieldEditText.setHint("");
            }
            else {
                searchFieldEditText.setHint(R.string.search);
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
                if (s.length() < 3) {
                    return;
                }
                transactionViewModel.setSearchQuery(s.toString());
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            chartHidden = false;
            if (checkedId == incomeRadioBtn.getId()) {
                incomeOrExpenditureTextView.setText(getString(R.string.income_or_expenditure, getString(R.string.income)));
                radioGroup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_group_income, null));
                incomeRadioBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));
                expenditureRadioBtn.setTextColor(getResources().getColor(R.color.colorDarkGrey, null));

                bonusOrPurchaseTextView.setBackgroundResource(R.drawable.text_wrap_orange);
                receiptOrtransferTextView.setBackgroundResource(R.drawable.text_wrap_white);
                replenishedOrWithdrawalTextView.setBackgroundResource(R.drawable.text_wrap_green);

                receiptOrtransferTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorBlack, null));

                transactionViewModel.setIncrease(true);

                return;
            }
            if (checkedId == expenditureRadioBtn.getId()) {
                incomeOrExpenditureTextView.setText(getString(R.string.income_or_expenditure, getString(R.string.expenditure)));
                radioGroup.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_group_exp, null));
                incomeRadioBtn.setTextColor(getResources().getColor(R.color.colorDarkGrey, null));
                expenditureRadioBtn.setTextColor(getResources().getColor(R.color.colorWhite, null));

                bonusOrPurchaseTextView.setBackgroundResource(R.drawable.text_wrap_purple);
                receiptOrtransferTextView.setBackgroundResource(R.drawable.text_wrap_grey);
                replenishedOrWithdrawalTextView.setBackgroundResource(R.drawable.text_wrap_dark_grey);

                receiptOrtransferTextView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));

                transactionViewModel.setIncrease(false);
            }
        });

        nextChartImageView.setOnClickListener(v -> {
            transactionViewModel.nextMonth();
            transactionViewModel.fetchTransactionList();
            transactionViewModel.fetchMonthlyBalance();
        });

        prevChartImageView.setOnClickListener(v -> {
            transactionViewModel.prevMonth();
            transactionViewModel.fetchTransactionList();
            transactionViewModel.fetchMonthlyBalance();
        });

        pieChart.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.action_monitoringFragment_to_transactionSearchFragment));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            transactionViewModel.fetchMonthlyBalance();
            transactionViewModel.fetchTransactionList();
        });

        if (transactionViewModel.getParams().getTransactionTypeId() == 4
                || transactionViewModel.getParams().getTransactionTypeId() == 5
                || transactionViewModel.getParams().getTransactionTypeId() == 6) {
            expenditureRadioBtn.setChecked(true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        transactionViewModel.isLoading().observe(getViewLifecycleOwner(), isInLoading -> {
            if (isInLoading) {
                swipeRefreshLayout.setRefreshing(true);
                return;
            }
            swipeRefreshLayout.setRefreshing(false);
        });

        transactionViewModel.getTransactionParams().observe(getViewLifecycleOwner(), transactionParams -> {
            currentMonthAndYearTextView.setText(getString(R.string.month_year,
                    getString(transactionViewModel.getMonthName(transactionParams.getMonth())),
                    String.valueOf(transactionParams.getYear())));
            nextChartImageView.setEnabled(transactionParams.getYear() < LocalDateTime.now().getYear() || transactionParams.getMonth() < 12);
        });

        transactionViewModel.getTransactionList().observe(getViewLifecycleOwner(), transactionList -> {
            transactionAdapter.setTransactionList(transactionList);
            transactionAdapter.notifyDataSetChanged();
        });

        transactionViewModel.getMonthlyBalance().observe(getViewLifecycleOwner(), monthlyBalance -> {
            if (radioGroup.getCheckedRadioButtonId() == incomeRadioBtn.getId()) {
                monthlyBalanceTextView.setText(getString(R.string.monthly_summary, "+", monthlyBalance));
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == expenditureRadioBtn.getId()) {
                monthlyBalanceTextView.setText(getString(R.string.monthly_summary, "-", monthlyBalance));
            }
        });

        transactionViewModel.getMonthlyBonusOrPurchaseAmount().observe(getViewLifecycleOwner(), amount -> {
            if (radioGroup.getCheckedRadioButtonId() == incomeRadioBtn.getId()) {
                bonusOrPurchaseTextView.setText(getString(R.string.monthly_balance, "Бонусы", amount));
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == expenditureRadioBtn.getId()) {
                bonusOrPurchaseTextView.setText(getString(R.string.monthly_balance, "Покупки", amount));
            }
        });

        transactionViewModel.getMonthlyReceiptOrTransferAmount().observe(getViewLifecycleOwner(), amount -> {
            if (radioGroup.getCheckedRadioButtonId() == incomeRadioBtn.getId()) {
                receiptOrtransferTextView.setText(getString(R.string.monthly_balance, "Получено", amount));
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == expenditureRadioBtn.getId()) {
                receiptOrtransferTextView.setText(getString(R.string.monthly_balance, "Отправлено", amount));
            }
        });

        transactionViewModel.getMonthlyReplenishOrWithdrawalAmount().observe(getViewLifecycleOwner(), amount -> {
            if (radioGroup.getCheckedRadioButtonId() == incomeRadioBtn.getId()) {
                replenishedOrWithdrawalTextView.setText(getString(R.string.monthly_balance, "Пополнение", amount));
                return;
            }
            if (radioGroup.getCheckedRadioButtonId() == expenditureRadioBtn.getId()) {
                replenishedOrWithdrawalTextView.setText(getString(R.string.monthly_balance, "Вывод", amount));
            }
        });

        transactionViewModel.getPieChartData().observe(getViewLifecycleOwner(), statisticsData -> {
            if ((statisticsData.getBonusOrPurchase() <= 0 || Float.isNaN(statisticsData.getBonusOrPurchase()))
                    && (statisticsData.getReceiptOrTransfer() <= 0 || Float.isNaN(statisticsData.getReceiptOrTransfer()))
                    && (statisticsData.getReplenishOrWithdrawal() <= 0 || Float.isNaN(statisticsData.getReplenishOrWithdrawal()))) {
                pieChart.setData(null);
                pieChart.invalidate();
                return;
            }
            final List<PieEntry> pieEntryList = new ArrayList<>();
            final PieDataSet monitoringDataSet = new PieDataSet(pieEntryList, null);

            pieEntryList.add(new PieEntry(statisticsData.getBonusOrPurchase()));
            pieEntryList.add(new PieEntry(statisticsData.getReceiptOrTransfer()));
            pieEntryList.add(new PieEntry(statisticsData.getReplenishOrWithdrawal()));

            if (radioGroup.getCheckedRadioButtonId() == incomeRadioBtn.getId()) {
                monitoringDataSet.setColors(incomeColorArr);
            }
            else if (radioGroup.getCheckedRadioButtonId() == expenditureRadioBtn.getId()) {
                monitoringDataSet.setColors(expenditureColorArr);
            }
            monitoringDataSet.setSliceSpace(2.5f);
            monitoringDataSet.setDrawValues(false);
            pieChart.setData(new PieData(monitoringDataSet));
            pieChart.invalidate();
        });
    }

    private static int[] incomeColorArr;
    private static int[] expenditureColorArr;

    private static final String TAG = MonitoringFragment.class.toString();
}
