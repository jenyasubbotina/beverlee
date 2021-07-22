package uz.alex.its.beverlee.model.notification;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.Date;

import uz.alex.its.beverlee.utils.Constants;

@Entity(tableName = "push")
public class Push {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id")
    private final long notificationId;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "body")
    private final String body;

    @ColumnInfo(name = "type")
    private final String type;

    @ColumnInfo(name = "timestamp")
    private final long timestamp;

    @ColumnInfo(name = "status")
    private final int status;

    @ColumnInfo(name = "news_id")
    private final long newsId;

    public Push(final long notificationId,
                final String title,
                final String body,
                final String type,
                final long timestamp,
                final int status,
                final long newsId) {
        this.notificationId = notificationId;
        this.title = title;
        this.body = body;
        this.type = type;
        this.timestamp = timestamp;
        this.status = status;
        this.newsId = newsId;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public long getNewsId() {
        return newsId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Push{" +
                "notificationId=" + notificationId +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", newsId=" + newsId +
                '}';
    }

    public static final String PUSH = "push";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String TYPE = "type";
    public static final String TIMESTAMP = "timestamp";
    public static final String NEWS_ID = "news_id";

    public static final String TYPE_BONUS = "bonus";
    public static final String TYPE_PURCHASE = "buy";
    public static final String TYPE_REPLENISH = "refill";
    public static final String TYPE_WITHDRAWAL = "withdrawal";
    public static final String TYPE_TRANSFER = "transfer";
    public static final String TYPE_NEWS = "news";
}
