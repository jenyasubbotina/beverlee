package uz.alex.its.beverlee.model.requestParams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePasswordParams {
    @Expose
    @SerializedName("password")
    private final String password;

    @Expose
    @SerializedName("new_password")
    private final String newPassword;

    @Expose
    @SerializedName("new_password_confirmation")
    private final String newPasswordConfirm;

    public ChangePasswordParams(final String password, final String newPassword, final String newPasswordConfirm) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }
}
