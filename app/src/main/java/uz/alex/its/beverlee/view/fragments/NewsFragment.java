package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.news.NewsModel.News;
import uz.alex.its.beverlee.view.UiUtils;
import uz.alex.its.beverlee.view.adapters.NewsAdapter;
import uz.alex.its.beverlee.view.interfaces.NewsCallback;
import uz.alex.its.beverlee.viewmodel.NewsViewModel;
import uz.alex.its.beverlee.viewmodel.factory.NewsViewModelFactory;

public class NewsFragment extends Fragment implements NewsCallback {
    private ImageView backImageView;
    private EditText searchEditText;

    private NewsAdapter newsAdapter;

    private NewsViewModel newsViewModel;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final NewsViewModelFactory newsFactory = new NewsViewModelFactory(requireContext());
        newsViewModel = new ViewModelProvider(getViewModelStore(), newsFactory).get(NewsViewModel.class);

        newsViewModel.fetchNews(0, 50);

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(NewsFragment.this).popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_news, container, false);

        backImageView = root.findViewById(R.id.back_arrow_image_view);
        searchEditText = root.findViewById(R.id.news_search_edit_text);
        final RecyclerView newsRecyclerView = root.findViewById(R.id.news_recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        newsAdapter = new NewsAdapter(requireContext(), this, NewsAdapter.TYPE_BANNER);
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setAdapter(newsAdapter);

        UiUtils.hideBottomNav(requireActivity());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backImageView.setOnClickListener(v -> {
            NavHostFragment.findNavController(NewsFragment.this).popBackStack();
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
                if (s.length() >= 3) {
                    newsViewModel.submitSearch(s.toString());
                    newsViewModel.setSearchFieldEmpty(false);
                    return;
                }
                newsViewModel.setSearchFieldEmpty(true);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        newsViewModel.getNewsList().observe(getViewLifecycleOwner(), newsList -> {
            newsAdapter.setNewsList(newsList);
            newsAdapter.notifyDataSetChanged();
        });
    }



    @Override
    public void expandNewsItem(final int position) {

    }

    @Override
    public void onNewsSelected(final int position, final News news) {
        final NewsFragmentDirections.ActionNewsFragmentToNewsDataFragment action
                = NewsFragmentDirections.actionNewsFragmentToNewsDataFragment();
        action.setNewsId(news.getId());
        action.setTitle(news.getTitle());
        action.setDescription(news.getDescription());
        action.setPhotoUrl(news.getPhotoUrl());
        action.setCreatedAt(news.getCreatedAt());
        NavHostFragment.findNavController(NewsFragment.this).navigate(action);
    }

    private static final String TAG = NewsFragment.class.toString();
}