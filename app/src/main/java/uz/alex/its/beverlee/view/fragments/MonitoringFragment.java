package uz.alex.its.beverlee.view.fragments;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.utils.DateFormatter;
import uz.alex.its.beverlee.view.adapters.TransactionAdapter;
import uz.alex.its.beverlee.view.dialog.CalendarDialog;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;
import uz.alex.its.beverlee.viewmodel_factory.TransactionViewModelFactory;

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
    private TextView currentMonthTextView;
    private TextView monthlyBalanceTextView;

    private PieChart pieChart;
    private ImageView prevChartImageView;
    private ImageView nextChartImageView;

    private ImageView minimizeChartImageView;

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

        transactionViewModel.fetchMonthlyBalance(Calendar.getInstance().get(Calendar.MONTH) + 1);
        transactionViewModel.fetchMonthlyTransactionList(null, Calendar.getInstance().get(Calendar.MONTH), null);
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
        currentMonthTextView = root.findViewById(R.id.month_name_text_view);
        monthlyBalanceTextView = root.findViewById(R.id.monthly_balance_text_view);
        prevChartImageView = root.findViewById(R.id.prev_chart_image_view);
        nextChartImageView = root.findViewById(R.id.next_chart_image_view);
        minimizeChartImageView = root.findViewById(R.id.minimize_chart_image_view);
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
            transactionViewModel.incrementCurrentMonth();
        });

        prevChartImageView.setOnClickListener(v -> {
            transactionViewModel.decrementCurrentMonth();
        });

        pieChart.setOnClickListener(v -> {
            final CalendarDialog calendar = new CalendarDialog();
            calendar.addListener(selection -> {
                if (selection == null) {
                    return;
                }
                if (selection.first == null || selection.second == null) {
                    return;
                }
                if (selection.first > selection.second) {
                    return;
                }
                final String startDate = DateFormatter.dateToYearMonthDay(new Date(selection.first));
                final String finishDate = DateFormatter.dateToYearMonthDay(new Date(selection.second));

                transactionViewModel.fetchTransactionList(null, startDate, finishDate, null);
            });
            calendar.show(getParentFragmentManager());
        });

        final ObjectAnimator minimizeCardAnimator = ObjectAnimator.ofInt(transactionsCard, "top",1600, 850);
        final ObjectAnimator maximizeCardAnimator = ObjectAnimator.ofInt(transactionsCard, "top",870, 1670);
        final ObjectAnimator minimizeRecyclerViewAnimator = ObjectAnimator.ofInt(transactionRecyclerView, "top",1600, 850);
        final ObjectAnimator maximizeRecyclerAnimator = ObjectAnimator.ofInt(transactionRecyclerView, "top",870, 1670);

        minimizeCardAnimator.setDuration(200);
        minimizeCardAnimator.setInterpolator(new LinearInterpolator());
        maximizeCardAnimator.setDuration(200);
        maximizeCardAnimator.setInterpolator(new LinearInterpolator());
        minimizeRecyclerViewAnimator.setDuration(200);
        minimizeRecyclerViewAnimator.setInterpolator(new LinearInterpolator());
        maximizeRecyclerAnimator.setDuration(200);
        maximizeRecyclerAnimator.setInterpolator(new LinearInterpolator());

        minimizeChartImageView.setOnClickListener(v -> {
            if (chartHidden) {
                chartHidden = false;
                maximizeCardAnimator.start();
                maximizeRecyclerAnimator.start();
                pieChart.setVisibility(View.VISIBLE);
                prevChartImageView.setVisibility(View.VISIBLE);
                nextChartImageView.setVisibility(View.VISIBLE);

                bonusOrPurchaseTextView.setVisibility(View.VISIBLE);
                receiptOrtransferTextView.setVisibility(View.VISIBLE);
                replenishedOrWithdrawalTextView.setVisibility(View.VISIBLE);

                minimizeChartImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_minimize, null));
                return;
            }
            chartHidden = true;
            minimizeCardAnimator.start();
            minimizeRecyclerViewAnimator.start();
            minimizeChartImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_maximize, null));
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            transactionViewModel.fetchMonthlyBalance(Calendar.getInstance().get(Calendar.MONTH) + 1);
            transactionViewModel.fetchMonthlyTransactionList(null, Calendar.getInstance().get(Calendar.MONTH), null);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        transactionViewModel.getCurrentMonthNumber().observe(getViewLifecycleOwner(), monthNumber -> {
            if (monthNumber < 0 || monthNumber > 11) {
                return;
            }
            currentMonthTextView.setText(getString(R.string.month_name, getString(transactionViewModel.getMonthName(monthNumber + 1))));

            LocalDate currentdate = LocalDate.now();
            int currentMonth = currentdate.getMonth().getValue();
            if (monthNumber == currentMonth-1) {
               nextChartImageView.setVisibility(View.INVISIBLE);
            } else {
                nextChartImageView.setVisibility(View.VISIBLE);
            }
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

        transactionViewModel.getTransactionList().observe(getViewLifecycleOwner(), transactionList -> {
            transactionAdapter.setTransactionList(transactionList);
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
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private static final String TAG = MonitoringFragment.class.toString();

    private static int[] incomeColorArr;
    private static int[] expenditureColorArr;
}
