package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.squareup.picasso.Picasso;

import java.util.List;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.news.NewsModel.News;
import uz.alex.its.beverlee.utils.DateFormatter;
import uz.alex.its.beverlee.view.interfaces.NewsCallback;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final Context context;
    private final NewsCallback callback;
    private int minNewsWidth;

    private List<News> newsList;

    private final int type;

    public NewsAdapter(final Context context, final NewsCallback callback, final int newsType) {
        this(context, callback, newsType, null);
    }

    public NewsAdapter(final Context context, final NewsCallback callback, final int newsType, final int minNewsWidth) {
        this(context, callback, newsType, null);
        this.minNewsWidth = minNewsWidth;
    }

    public NewsAdapter(final Context context, final NewsCallback callback, final int newsType, final List<News> newsList) {
        this.context = context;
        this.callback = callback;
        this.type = newsType;
        this.newsList = newsList;
    }

    public void setNewsList(final List<News> newsList) {
        this.newsList = newsList;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        /* news banner type view holder */
        holder.titleTextView.setText(Html.fromHtml(newsList.get(position).getTitle(), Html.FROM_HTML_MODE_COMPACT));

        Picasso.get()
                .load(newsList.get(position).getPhotoUrl())
                .centerCrop()
                .fit()
                .error(R.color.colorDarkGrey)
                .into(holder.coverImageView);
        holder.coverImageView.setShapeAppearanceModel(holder.coverImageView.getShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, context.getResources().getDimension(R.dimen.news_corner_margin))
                .build());

        if (type == TYPE_BANNER) {
            holder.dateTextView.setText(DateFormatter.timestampToStringDate(newsList.get(position).getCreatedAt()));
        }
        if (minNewsWidth > 0) {
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.cardView.getLayoutParams();
            params.width = minNewsWidth;
            holder.cardView.setLayoutParams(params);
        }
        holder.bindItem(callback, position, newsList.get(position));
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cardView;
        ShapeableImageView coverImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView dateTextView;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.news_min_card_view);
            coverImageView = itemView.findViewById(R.id.news_header);
            titleTextView = itemView.findViewById(R.id.news_title_text_view);
            descriptionTextView = itemView.findViewById(R.id.news_description_text_view);
            dateTextView = itemView.findViewById(R.id.news_date_text_view);
        }

        void bindItem(final NewsCallback callback, final int position, final News news) {
            itemView.setOnClickListener(v -> {
                callback.onNewsSelected(position, news);
            });
        }
    }

    public static final int TYPE_MIN = 1;
    public static final int TYPE_BANNER = 2;
    private static final String TAG = NewsAdapter.class.toString();
}
