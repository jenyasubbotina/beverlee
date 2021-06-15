package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePinParams {
    @Expose
    @SerializedName("pin")
    private final String oldPin;

    @Expose
    @SerializedName("new_pin")
    private final String newPin;

    @Expose
    @SerializedName("new_pin_confirmation")
    private final String newPinConfirmation;

    public ChangePinParams(final String oldPin, final String newPin, final String newPinConfirmation) {
        this.oldPin = oldPin;
        this.newPin = newPin;
        this.newPinConfirmation = newPinConfirmation;
    }

    public String getOldPin() {
        return oldPin;
    }

    public String getNewPin() {
        return newPin;
    }

    public String getNewPinConfirmation() {
        return newPinConfirmation;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChangePinParams{" +
                "oldPin='" + oldPin + '\'' +
                ", newPin='" + newPin + '\'' +
                ", newPinConfirmation='" + newPinConfirmation + '\'' +
                '}';
    }
}
