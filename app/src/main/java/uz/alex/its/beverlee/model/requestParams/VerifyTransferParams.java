package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VerifyTransferParams implements Serializable {
    @Expose
    @SerializedName("recipient_id")
    private final long recipientId;

    @Expose
    @SerializedName("amount")
    private final double amount;

    @Expose
    @SerializedName("add_notebook")
    private final String note;

    public VerifyTransferParams(final long recipientId, final double amount, final String note) {
        this.recipientId = recipientId;
        this.amount = amount;
        this.note = note;
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

    @NonNull
    @Override
    public String toString() {
        return "VerifyTransferParams{" +
                "recipientId=" + recipientId +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                '}';
    }
}
