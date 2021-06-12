package uz.alex.its.beverlee.model.notification;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationSettingsModel {
    @Expose
    @SerializedName("draw")
    private final long draw;

    @Expose
    @SerializedName("recordsTotal")
    private final int recordsTotal;

    @Expose
    @SerializedName("recordsFiltered")
    private final int recordsFiltered;

    @Expose
    @SerializedName("data")
    private final NotificationSettings notificationData;

    public NotificationSettingsModel(final long draw, final int recordsTotal, final int recordsFiltered, final NotificationSettings notificationData) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.notificationData = notificationData;
    }

    public long getDraw() {
        return draw;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public NotificationSettings getNotificationData() {
        return notificationData;
    }

    @NonNull
    @Override
    public String toString() {
        return "NotificationDataResponse {" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", notificationData=" + notificationData +
                '}';
    }

    public static class NotificationSettings {
        @Expose
        @SerializedName("news")
        private final int news;

        @Expose
        @SerializedName("bonus")
        private final int bonus;

        @Expose
        @SerializedName("receipt")
        private final int income;

        @Expose
        @SerializedName("buy")
        private final int purchase;

        @Expose
        @SerializedName("refill")
        private final int replenish;

        @Expose
        @SerializedName("withdrawal")
        private final int withdrawal;

        public NotificationSettings(final int news, final int bonus, final int income, final int purchase, final int replenish, final int withdrawal) {
            this.news = news;
            this.bonus = bonus;
            this.income = income;
            this.purchase = purchase;
            this.replenish = replenish;
            this.withdrawal = withdrawal;
        }

        public int getNews() {
            return news;
        }

        public int getBonus() {
            return bonus;
        }

        public int getIncome() {
            return income;
        }

        public int getPurchase() {
            return purchase;
        }

        public int getReplenish() {
            return replenish;
        }

        public int getWithdrawal() {
            return withdrawal;
        }
    }
}
