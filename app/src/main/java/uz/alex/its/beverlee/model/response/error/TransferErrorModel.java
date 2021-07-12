package uz.alex.its.beverlee.model.response.error;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransferErrorModel {
    @Expose
    @SerializedName("errors")
    private final TransferError transferError;

    public TransferErrorModel(final TransferError transferError) {
        this.transferError = transferError;
    }

    public TransferError getTransferError() {
        return transferError;
    }

    @NonNull
    @Override
    public String toString() {
        return "TransferErrorModel{" +
                "transferError=" + transferError +
                '}';
    }

    public static class TransferError {
        @Expose
        @SerializedName("amount")
        private final List<String> amount;

        @Expose
        @SerializedName("pin")
        private final List<String> pin;

        public TransferError(final List<String> amount, final List<String> pin) {
            this.amount = amount;
            this.pin = pin;
        }

        public List<String> getAmount() {
            return amount;
        }

        public List<String> getPin() {
            return pin;
        }

        @NonNull
        @Override
        public String toString() {
            return "LoginError{" +
                    "amount='" + amount + '\'' +
                    ", pin='" + pin + '\'' +
                    '}';
        }
    }
}
