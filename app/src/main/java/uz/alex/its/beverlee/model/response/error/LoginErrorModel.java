package uz.alex.its.beverlee.model.response.error;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NonNls;

public class LoginErrorModel {
    @Expose
    @SerializedName("errors")
    private final LoginError loginError;

    public LoginErrorModel(final LoginError loginError) {
        this.loginError = loginError;
    }

    public LoginError getLoginError() {
        return loginError;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginErrorModel{" +
                "loginError=" + loginError +
                '}';
    }

    public static class LoginError {
        @Expose
        @SerializedName("phone_number")
        private final String phone;

        public LoginError(final String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        @NonNull
        @Override
        public String toString() {
            return "LoginError{" +
                    "phone='" + phone + '\'' +
                    '}';
        }
    }
}
