package uz.alex.its.beverlee.model.response.error;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NonNls;

import java.util.List;

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

        @Expose
        @SerializedName("email")
        private final List<String> email;

        public LoginError(final String phone, final List<String> email) {
            this.phone = phone;
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public List<String> getEmail() {
            return email;
        }

        @NonNull
        @Override
        public String toString() {
            return "LoginError{" +
                    "phone='" + phone + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}
