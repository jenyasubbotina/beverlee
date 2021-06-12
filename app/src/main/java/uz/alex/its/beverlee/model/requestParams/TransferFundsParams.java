package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransferFundsParams implements Serializable {
    @Expose
    @SerializedName("recipient_id")
    private final long recipientId;

    @Expose
    @SerializedName("amount")
    private final double amount;

    @Expose
    @SerializedName("add_notebook")
    private final String note;

    @Expose
    @SerializedName("pin")
    private final String pin;

    public TransferFundsParams(final long recipientId, final double amount, final String note, final String pin) {
        this.recipientId = recipientId;
        this.amount = amount;
        this.note = note;
        this.pin = pin;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public double getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public String getPin() {
        return pin;
    }

    @NonNull
    @Override
    public String toString() {
        return "MakeTransferParams{" +
                "recipientId=" + recipientId +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }
}
