package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.news.NewsModel.News;
import uz.alex.its.beverlee.utils.DateFormatter;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.viewmodel.NewsViewModel;
import uz.alex.its.beverlee.viewmodel_factory.NewsViewModelFactory;

public class NewsDataFragment extends Fragment {
    private ImageView backImageView;
    private EditText searchEditText;
    private ImageView bannerImageView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;

    private News currentNews;

    public NewsDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentNews = new News(
                    NewsDataFragmentArgs.fromBundle(getArguments()).getNewsId(),
                    NewsDataFragmentArgs.fromBundle(getArguments()).getTitle(),
                    NewsDataFragmentArgs.fromBundle(getArguments()).getDescription(),
                    NewsDataFragmentArgs.fromBundle(getArguments()).getPhotoUrl(),
                    NewsDataFragmentArgs.fromBundle(getArguments()).getCreatedAt());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_news_data, container, false);

        UiUtils.hideBottomNav(requireActivity());

        backImageView = root.findViewById(R.id.back_arrow_image_view);
        searchEditText = root.findViewById(R.id.news_search_edit_text);
        bannerImageView = root.findViewById(R.id.news_header);
        titleTextView = root.findViewById(R.id.news_title_text_view);
        dateTextView = root.findViewById(R.id.news_date_text_view);
        descriptionTextView = root.findViewById(R.id.news_description_text_view);
        titleTextView.setText(Html.fromHtml(currentNews.getTitle(), Html.FROM_HTML_MODE_COMPACT));
        descriptionTextView.setText(Html.fromHtml(currentNews.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        dateTextView.setText(DateFormatter.timestampToStringDate(currentNews.getCreatedAt()));

        Picasso.get()
                .load(currentNews.getPhotoUrl())
                .centerCrop()
                .fit()
                .error(R.color.colorDarkGrey)
                .into(bannerImageView);;

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backImageView.setOnClickListener(v -> {
            NavHostFragment.findNavController(NewsDataFragment.this).popBackStack();
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final NewsViewModelFactory factory = new NewsViewModelFactory(requireContext());
        final NewsViewModel newsViewModel = new ViewModelProvider(getViewModelStore(), factory).get(NewsViewModel.class);

        newsViewModel.fetchNewsData(currentNews.getId());
        newsViewModel.getNewsData().observe(getViewLifecycleOwner(), newsData -> {
            Log.i(TAG, "onActivityCreated(): newsData=" + newsData);
        });
    }

    private static final String TAG = NewsDataFragment.class.toString();
}