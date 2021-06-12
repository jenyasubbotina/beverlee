package uz.alex.its.beverlee.model.balance;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Balance {
    @Expose
    @SerializedName("balance")
    private final double balance;

    public Balance(final double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    @NonNull
    @Override
    public String toString() {
        return "Balance{" +
                "balance=" + balance +
                '}';
    }
}
