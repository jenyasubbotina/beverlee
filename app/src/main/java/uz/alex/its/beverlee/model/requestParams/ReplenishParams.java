package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReplenishParams {
    @Expose
    @SerializedName("amount")
    private final String amount;

    public ReplenishParams(final String amount) {
        this.amount = amount;
    }

    @NonNull
    @Override
    public String toString() {
        return "ReplenishParams{" +
                "amount='" + amount + '\'' +
                '}';
    }
}
