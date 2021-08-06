package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.notification.Push;
import uz.alex.its.beverlee.model.notification.PushDiffUtil;
import uz.alex.its.beverlee.utils.DateFormatter;
import uz.alex.its.beverlee.view.interfaces.NotificationCallback;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<Push> notificationList;
    private final NotificationCallback callback;

    public NotificationAdapter(final Context context, final NotificationCallback callback) {
        this(context, callback, null);
    }

    public NotificationAdapter(final Context context, final NotificationCallback callback, final List<Push> notificationList) {
        this.context = context;
        this.callback = callback;
        setNotificationList(notificationList);
    }

    public void setNotificationList(final List<Push> newNotificationList) {
        final PushDiffUtil diffUtil = new PushDiffUtil(notificationList, newNotificationList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        this.notificationList = newNotificationList;
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (notificationList == null) {
            return 0;
        }
        if (position == 0) {
            return TYPE_DATE;
        }
        if ((notificationList.get(position - 1).getTimestamp()/86400) != (notificationList.get(position).getTimestamp()/86400)) {
            return TYPE_DATE;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return notificationList == null ? 0 : notificationList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DATE) {
            return new NotificationDateViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_notification_date, parent, false));
        }
        else {
            return new NotificationViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_notification, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == 0) {
            throw new IllegalArgumentException("unknown viewHolder viewType");
        }
        NotificationViewHolder holder = (NotificationViewHolder) viewHolder;
        holder.titleTextView.setText(notificationList.get(position).getTitle());
        holder.bodyTextView.setText(notificationList.get(position).getBody());

        holder.timeTextView.setText(DateFormatter.timestampToStringTime(notificationList.get(position).getTimestamp()));

        if (getItemViewType(position) == TYPE_DATE) {
            NotificationDateViewHolder dateHolder = (NotificationDateViewHolder) viewHolder;
            dateHolder.dateTextView.setText(DateFormatter.timestampToDayMonthDate(notificationList.get(position).getTimestamp()));
        }
        if (notificationList.get(position).getStatus() > 0) {
            holder.circleImageView.setVisibility(View.GONE);
            holder.layout.setBackgroundColor(context.getColor(R.color.colorWhite));
        }
        else {
            holder.circleImageView.setVisibility(View.VISIBLE);
            holder.layout.setBackgroundColor(context.getColor(R.color.colorBgGrey));
        }
        holder.bindItem(callback, notificationList.get(position), position);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        View layout;
        TextView titleTextView;
        TextView bodyTextView;
        ImageView circleImageView;
        TextView timeTextView;

        public NotificationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            this.layout = itemView.findViewById(R.id.push_bg);
            this.titleTextView = itemView.findViewById(R.id.title_text_view);
            this.bodyTextView = itemView.findViewById(R.id.body_text_view);
            this.timeTextView = itemView.findViewById(R.id.time_text_view);
            this.circleImageView = itemView.findViewById(R.id.circle_purple_image_view);
        }

        void bindItem(final NotificationCallback callback, final Push item, final int position) {
            itemView.setOnClickListener(v -> {
                callback.onNotificationTapped(item, position);
            });
        }
    }

    static class NotificationDateViewHolder extends NotificationViewHolder {
        TextView dateTextView;

        public NotificationDateViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            this.dateTextView = itemView.findViewById(R.id.date_text_view);
        }
    }

    private static final int TYPE_DATE = 0x01;
    private static final int TYPE_ITEM = 0x02;
    private static final String TAG = NotificationAdapter.class.toString();
}
