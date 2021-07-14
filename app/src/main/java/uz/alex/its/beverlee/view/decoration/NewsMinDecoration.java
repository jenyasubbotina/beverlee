package uz.alex.its.beverlee.view.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class NewsMinDecoration extends RecyclerView.ItemDecoration {
    private final int biggerMargin;
    private final int margin;

    public NewsMinDecoration(final int biggerMargin, final int margin) {
        this.biggerMargin = biggerMargin;
        this.margin = margin;
    }

    @Override
    public void getItemOffsets(@NonNull @NotNull Rect outRect,
                               @NonNull @NotNull View view,
                               @NonNull @NotNull RecyclerView parent,
                               @NonNull @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int left = 0, right = 0, top = 0, bottom = 0;

        if (parent.getAdapter() != null) {
            top =  margin;
            bottom = margin;
            right = margin;

            if (parent.getChildAdapterPosition(view) == 0) {
                left = biggerMargin;
            }
            else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                right = biggerMargin;
            }
            outRect.set(left, top, right, bottom);
        }
    }
}
