package uz.alex.its.beverlee.model.balance;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MonthBalance {
    @Expose
    @SerializedName("increase")
    private final double increase;

    @Expose
    @SerializedName("decrease")
    private final double decrease;

    public MonthBalance(final double increase, final double decrease) {
        this.increase = increase;
        this.decrease = decrease;
    }

    public double getDecrease() {
        return decrease;
    }

    public double getIncrease() {
        return increase;
    }

    @NonNull
    @Override
    public String toString() {
        return "MonthBalance{" +
                "increase=" + increase +
                ", decrease=" + decrease +
                '}';
    }
}
