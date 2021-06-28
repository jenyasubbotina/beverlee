package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.DayArrayAdapter;

public class TransactionSearchFragment extends Fragment {
    private ImageView backArrowImageView;

    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private Spinner firstDaySpinner;
    private Spinner lastDaySpinner;
    private Spinner transactionTypeSpinner;
    private Button searchBtn;

    private DayArrayAdapter firstDayAdapter;
    private DayArrayAdapter lastDayAdapter;

    private List<Integer> yearList;

    private List<Integer> thirtyOneDayList;
    private List<Integer> thirtyDayList;
    private List<Integer> februaryLeapDayList;
    private List<Integer> februaryNotLeapDayList;
    private List<Integer> dayList;

    public TransactionSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        yearList = new ArrayList<>();

        final int currentYear = YearMonth.now().getYear();
        for (int i = currentYear; i >= 2020; i--) {
            yearList.add(i);
        }
        thirtyOneDayList = Arrays.stream(getResources().getIntArray(R.array.day_array)).boxed().collect(Collectors.toList());
        thirtyDayList = thirtyOneDayList.subList(0, thirtyOneDayList.size() - 1);
        februaryLeapDayList = thirtyOneDayList.subList(0, thirtyOneDayList.size() - 2);
        februaryNotLeapDayList = thirtyOneDayList.subList(0, thirtyOneDayList.size() - 3);

        setDayList(YearMonth.now().getMonthValue() - 1, YearMonth.now().getYear());

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(TransactionSearchFragment.this).popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_transaction_search, container, false);

        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        yearSpinner = root.findViewById(R.id.year_spinner);
        monthSpinner = root.findViewById(R.id.month_spinner);
        firstDaySpinner = root.findViewById(R.id.first_day_spinner);
        lastDaySpinner = root.findViewById(R.id.last_day_spinner);
        transactionTypeSpinner = root.findViewById(R.id.transaction_type_spinner);
        searchBtn = root.findViewById(R.id.search_btn);

        final DayArrayAdapter yearAdapter = new DayArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearList);
        final ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.month_array));
        final ArrayAdapter<String> transactionTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.transaction_type_array));
        firstDayAdapter = new DayArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item);
        lastDayAdapter = new DayArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item);

        yearSpinner.setAdapter(yearAdapter);
        monthSpinner.setAdapter(monthAdapter);
        firstDaySpinner.setAdapter(firstDayAdapter);
        lastDaySpinner.setAdapter(lastDayAdapter);
        transactionTypeSpinner.setAdapter(transactionTypeAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        monthSpinner.setSelection(YearMonth.now().getMonthValue() - 1);

        UiUtils.hideBottomNav(requireActivity());

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setDayList(position, (int) yearSpinner.getSelectedItem());
                firstDayAdapter.setDayList(dayList);
                lastDayAdapter.setDayList(dayList);
                firstDayAdapter.notifyDataSetChanged();
                lastDayAdapter.notifyDataSetChanged();
                lastDaySpinner.setSelection(dayList.size() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setDayList(monthSpinner.getSelectedItemPosition(), (int) parent.getSelectedItem());
                firstDayAdapter.setDayList(dayList);
                lastDayAdapter.setDayList(dayList);
                firstDayAdapter.notifyDataSetChanged();
                lastDayAdapter.notifyDataSetChanged();
                lastDaySpinner.setSelection(dayList.size() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backArrowImageView.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        searchBtn.setOnClickListener(v -> {
            searchBtn.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bubble));

            final int year = (int) yearSpinner.getSelectedItem();
            final int monthNumber =  monthSpinner.getSelectedItemPosition() + 1;
            final int firstDay = (int) firstDaySpinner.getSelectedItem();
            final int lastDay = (int) lastDaySpinner.getSelectedItem();
            final LocalDateTime startDate = LocalDateTime.of(year, monthNumber, firstDay, 0, 0);
            final LocalDateTime now = LocalDateTime.now();

            if (firstDay > lastDay || startDate.isAfter(now)) {
                Toast.makeText(requireContext(), "Ошибка ввода", Toast.LENGTH_SHORT).show();
                return;
            }
            NavHostFragment.findNavController(this).navigate(
                    TransactionSearchFragmentDirections.actionTransactionSearchFragmentToMonitoringFragment()
                            .setYear(year)
                            .setMonth(monthNumber)
                            .setFirstDay(firstDay)
                            .setLastDay(lastDay)
                            .setTransactionTypeId(transactionTypeSpinner.getSelectedItemPosition()));
        });
    }

    private void setDayList(final int currentMonth, final int currentYear) {
        switch (currentMonth) {
            case 1: {
                if (Year.isLeap(currentYear)) {
                    dayList = februaryLeapDayList;
                }
                else {
                    dayList = februaryNotLeapDayList;
                }
                break;
            }
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11: {
                dayList = thirtyOneDayList;
                break;
            }
            case 3:
            case 5:
            case 8:
            case 10: {
                dayList = thirtyDayList;
                break;
            }
        }
    }

    private static final String TAG = TransactionSearchFragment.class.toString();
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
}