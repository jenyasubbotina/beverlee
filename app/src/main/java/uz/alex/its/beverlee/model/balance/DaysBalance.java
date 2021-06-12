package uz.alex.its.beverlee.model.balance;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class DaysBalance {
    @Expose
    @SerializedName("increase")
    private final HashMap<String, String> increase;

    @Expose
    @SerializedName("decrease")
    private final HashMap<String, String> decrease;

    public DaysBalance(final HashMap<String, String> increase, final HashMap<String, String> decrease) {
        this.increase = increase;
        this.decrease = decrease;
    }

    @NonNull
    @Override
    public String toString() {
        return "DaysBalance{" +
                "increase=" + increase +
                ", decrease=" + decrease +
                '}';
    }
}
