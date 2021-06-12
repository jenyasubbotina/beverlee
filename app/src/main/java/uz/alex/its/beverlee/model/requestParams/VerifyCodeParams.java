package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyCodeParams {
    @Expose
    @SerializedName("code")
    private final String code;

    public VerifyCodeParams(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @NonNull
    @Override
    public String toString() {
        return "VerifyCodeParams{" +
                "code='" + code + '\'' +
                '}';
    }
}
