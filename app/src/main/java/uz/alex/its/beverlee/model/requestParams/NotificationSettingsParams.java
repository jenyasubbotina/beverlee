package uz.alex.its.beverlee.model.requestParams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationSettingsParams {
    @Expose
    @SerializedName("news")
    private final boolean news;

    @Expose
    @SerializedName("bonus")
    private final boolean bonus;

    @Expose
    @SerializedName("receipt")
    private final boolean income;

    @Expose
    @SerializedName("buy")
    private final boolean purchase;

    @Expose
    @SerializedName("refill")
    private final boolean replenish;

    @Expose
    @SerializedName("withdrawal")
    private final boolean withdrawal;

    public NotificationSettingsParams(final boolean news, final boolean bonus, final boolean income, final boolean purchase, final boolean replenish, final boolean withdrawal) {
        this.news = news;
        this.bonus = bonus;
        this.income = income;
        this.purchase = purchase;
        this.replenish = replenish;
        this.withdrawal = withdrawal;
    }

    public boolean isNews() {
        return news;
    }

    public boolean isBonus() {
        return bonus;
    }

    public boolean isIncome() {
        return income;
    }

    public boolean isPurchase() {
        return purchase;
    }

    public boolean isReplenish() {
        return replenish;
    }

    public boolean isWithdrawal() {
        return withdrawal;
    }
}
