package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PinParams {
    @Expose
    @SerializedName("pin")
    private final String pin;

    public PinParams(final String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    @NonNull
    @Override
    public String toString() {
        return "PinParams{" +
                "pin='" + pin + '\'' +
                '}';
    }
}
