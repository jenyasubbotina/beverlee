package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class DayArrayAdapter extends ArrayAdapter<Integer> {
    private List<Integer> dayList;

    public DayArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public DayArrayAdapter(@NonNull Context context, int resource, final List<Integer> items) {
        super(context, resource, items);
        this.dayList = items;
    }

    public void setDayList(final List<Integer> dayList) {
        this.dayList = dayList;
    }

    @Nullable
    @Override
    public Integer getItem(int position) {
        return dayList.get(position);
    }

    @Override
    public int getCount() {
        return dayList == null ? 0 : dayList.size();
    }
}
