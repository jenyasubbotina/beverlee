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

    public static final String PHONE_NOT_VERIFIED = "\\u041f\\u043e\\u0434\\u0442\\u0432\\u0435\\u0440\\u0434\\u0438\\u0442\\u0435 \\u043d\\u043e\\u043c\\u0435\\u0440 \\u0442\\u0435\\u043b\\u0435\\u0444\\u043e\\u043d\\u0430";

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
