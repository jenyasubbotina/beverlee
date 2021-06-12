package uz.alex.its.beverlee.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @Expose
    @SerializedName("token")
    private final String token;

    public LoginModel(final String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginModel{" +
                "token='" + token + '\'' +
                '}';
    }
}
