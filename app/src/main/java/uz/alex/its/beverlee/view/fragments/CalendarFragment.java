package uz.alex.its.beverlee.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.view.UiUtils;

public class CalendarFragment extends Fragment {
    private static final String TAG = CalendarFragment.class.toString();

    private DatePicker calendar;
    private ImageView backArrowImageView;
    private Button applyBtn;

    private Animation bubbleAnimation;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_calendar, container, false);

        /* Bottom navigation */
        UiUtils.hideBottomNav(requireActivity());

        calendar = root.findViewById(R.id.calendar);
        backArrowImageView = root.findViewById(R.id.back_arrow_image_view);
        applyBtn = root.findViewById(R.id.apply_btn);

        bubbleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.bubble);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backArrowImageView.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        applyBtn.setOnClickListener(v -> {
            applyBtn.startAnimation(bubbleAnimation);
            applyBtn.postOnAnimationDelayed(() -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }, 100);
        });
    }
}