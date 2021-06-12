package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.Country;
import uz.alex.its.beverlee.view.CircleTransformation;

public class CountryAdapter extends ArrayAdapter<Country> {
    private final Context context;
    private List<Country> countryList;

    public CountryAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        this.context = context;
    }

    public CountryAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

        this.context = context;
    }

    public CountryAdapter(@NonNull Context context, int resource, @NonNull Country[] objects) {
        super(context, resource, objects);

        this.context = context;
    }

    public CountryAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Country[] objects) {
        super(context, resource, textViewResourceId, objects);

        this.context = context;
    }

    public CountryAdapter(@NonNull Context context, int resource, @NonNull List<Country> objects) {
        super(context, resource, objects);

        this.context = context;
        this.countryList = objects;
    }

    public CountryAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Country> objects) {
        super(context, resource, textViewResourceId, objects);

        this.context = context;
        this.countryList = objects;
    }

    public void setCountryList(final List<Country> countryList) {
        this.countryList = countryList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View root = convertView;

        if (convertView == null) {
            root = LayoutInflater.from(context).inflate(R.layout.view_holder_country, parent, false);
        }
        final TextView nameTextView = (TextView) root.findViewById(R.id.country_name_text_view);
        final ImageView flagImageView = (ImageView) root.findViewById(R.id.country_flag_image_view);

        nameTextView.setText(getItem(position).getTitle());
        Picasso.get()
                .load(getItem(position).getFlag())
                .centerCrop()
                .fit()
                .transform(new CircleTransformation(50, 0))
                .into(flagImageView);
        return root;
    }

    @Nullable
    @Override
    public Country getItem(int position) {
        return countryList.get(position);
    }

    @Override
    public int getCount() {
        return countryList == null ? 0 : countryList.size();
    }
}