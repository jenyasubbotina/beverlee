package uz.alex.its.beverlee.model.requestParams;

import android.os.Build;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uz.alex.its.beverlee.utils.Cryptographic;

public class AuthParams {
    @Expose
    @SerializedName("phone_number")
    private final String phone;

    @Expose
    @SerializedName("password")
    private final String password;

    @Expose
    @SerializedName("device_token")
    private final String googleToken;

    public AuthParams(final String phone, final String password, final String googleToken) {
        this.phone = phone;
        this.password = password;
        this.googleToken = googleToken;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    @NonNull
    @Override
    public String toString() {
        return "AuthParams{" +
                "phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", googleToken='" + googleToken + '\'' +
                '}';
    }

    private static final String TAG = AuthParams.class.toString();
}
