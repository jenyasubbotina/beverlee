package uz.alex.its.beverlee.model.notification;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.Date;

@Entity(tableName = "push", indices = {@Index(value = "id", unique = true)})
public class Push {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notification_id")
    private int notificationId;

    @ColumnInfo(name = "id")
    @NonNull
    private final String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "body")
    private String body;

    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "receipt_date")
    private Date receiptDate;

    @ColumnInfo(name = "channel_id")
    private int channelId;

    public Push(@NonNull final String id) {
        this.id = id;
    }

    public void setNotificationId(final int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Push{" +
                "notificationId=" + notificationId +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", status=" + status +
                ", receiptDate=" + receiptDate +
                ", channelId=" + channelId +
                '}';
    }
}
