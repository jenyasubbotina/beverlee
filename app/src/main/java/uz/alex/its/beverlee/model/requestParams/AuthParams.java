package uz.alex.its.beverlee.model.requestParams;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.Cryptographic;

public class AuthParams {
    @Expose
    @SerializedName("phone_number")
    private final String phone;

    @Expose
    @SerializedName("password")
    private final String password;

    @Expose
    @SerializedName("device_name")
    private final String deviceName;

    public AuthParams(final String phone, final String password) {
        this.phone = phone;
        this.password = password;
        this.deviceName = Cryptographic.md5(phone + Build.MANUFACTURER + Build.MODEL);
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getDeviceName() {
        return deviceName;
    }

    @NonNull
    @Override
    public String toString() {
        return "AuthParams{" +
                "phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }

    private static final String TAG = AuthParams.class.toString();
}
