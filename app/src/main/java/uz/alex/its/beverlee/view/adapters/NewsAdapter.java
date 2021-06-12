package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.news.NewsModel.News;
import uz.alex.its.beverlee.utils.DateFormatter;
import uz.alex.its.beverlee.view.interfaces.NewsCallback;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final Context context;
    private final NewsCallback callback;

    private List<News> newsList;

    private final int type;

    public NewsAdapter(final Context context, final NewsCallback callback, final int newsType) {
        this(context, callback, newsType, null);
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
        if (viewType == TYPE_BANNER) {
            return new NewsBannerViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_news_banner, parent, false));
        }
        if (viewType == TYPE_MIN) {
            return new NewsMinViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_news_min, parent, false));
        }
        throw new IllegalStateException("unknown view holder type");
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        /* news banner type view holder */
//        if (position == 0) {
//            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//            params.leftMargin = 80;
//            holder.itemView.setLayoutParams(params);
//        }
//        else if (position == newsList.size() - 1) {
//            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//            params.rightMargin = 80;
//            holder.itemView.setLayoutParams(params);
//        }
        holder.titleTextView.setText(Html.fromHtml(newsList.get(position).getTitle(), Html.FROM_HTML_MODE_COMPACT));

        Picasso.get()
                .load(newsList.get(position).getPhotoUrl())
                .centerCrop()
                .fit()
                .error(R.color.colorDarkGrey)
                .into(holder.coverImageView);

        holder.bindItem(callback, position, newsList.get(position));

        if (getItemViewType(position) == TYPE_BANNER) {
            final NewsBannerViewHolder viewHolder = (NewsBannerViewHolder) holder;
            //viewHolder.descriptionTextView.setText(Html.fromHtml(newsList.get(position).getDescription(), Html.FROM_HTML_MODE_COMPACT));
            viewHolder.dateTextView.setText(DateFormatter.timestampToStringDate(newsList.get(position).getCreatedAt()));
            viewHolder.bindMoreTextView(callback, position);
        }
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    static abstract class NewsViewHolder extends RecyclerView.ViewHolder {
        protected ImageView coverImageView;
        protected TextView titleTextView;

        protected NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            coverImageView = itemView.findViewById(R.id.news_header);
            titleTextView = itemView.findViewById(R.id.news_title_text_view);
        }

        protected abstract void bindItem(final NewsCallback callback, final int position, final News news);
    }

    static class NewsBannerViewHolder extends NewsViewHolder {
        TextView dateTextView;
        TextView descriptionTextView;
        TextView moreTextView;

        public NewsBannerViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.news_date_text_view);
            descriptionTextView = itemView.findViewById(R.id.news_description_text_view);
            moreTextView = itemView.findViewById(R.id.more_text_view);
        }

        void bindMoreTextView(final NewsCallback callback, final int position) {
            moreTextView.setOnClickListener(v -> {
                callback.expandNewsItem(position);
            });
        }

        public void bindItem(final NewsCallback callback, final int position, final News news) {
            itemView.setOnClickListener(v -> {
                callback.onNewsSelected(position, news);
            });
        }

    }

    static class NewsMinViewHolder extends NewsViewHolder {
        public NewsMinViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindItem(final NewsCallback callback, final int position, final News news) {
            itemView.setOnClickListener(v -> {
                callback.onNewsSelected(position, news);
            });
        }
    }

    public static final int TYPE_BANNER = 0;
    public static final int TYPE_MIN = 1;

    private static final String TAG = NewsAdapter.class.toString();
}
