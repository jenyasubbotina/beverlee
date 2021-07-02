package uz.alex.its.beverlee.model.response.error;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransferErrorModel {
    @Expose
    @SerializedName("errors")
    private final Object transferError;

    public TransferErrorModel(final Object transferError) {
        this.transferError = transferError;
    }

    public Object getTransferError() {
        return transferError;
    }

    @NonNull
    @Override
    public String toString() {
        return "TransferErrorModel{" +
                "transferError=" + transferError +
                '}';
    }
}
