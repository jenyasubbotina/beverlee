package uz.alex.its.beverlee.view.dialog;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import uz.alex.its.beverlee.R;

public class CalendarDialog {
    private final MaterialDatePicker<Pair<Long, Long>> datePicker;

    public CalendarDialog() {
        final CalendarConstraints constraints = new CalendarConstraints.Builder().build();
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText(R.string.pick_period);
        materialDateBuilder.setCalendarConstraints(constraints);
        materialDateBuilder.setTheme(R.style.DatePickerTheme);
        datePicker = materialDateBuilder.build();
    }

    public void addListener(MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> listener) {
        datePicker.addOnPositiveButtonClickListener(listener);
    }

    public void removeListeners(MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> listener) {
        datePicker.removeOnPositiveButtonClickListener(listener);
    }

    public void show(final FragmentManager fragmentManager) {
        datePicker.show(fragmentManager, null);
    }

    public void dismiss() {
        datePicker.dismiss();
    }
}
