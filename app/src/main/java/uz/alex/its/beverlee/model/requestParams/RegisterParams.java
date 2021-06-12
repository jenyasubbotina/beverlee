package uz.alex.its.beverlee.model.requestParams;

import android.os.Build;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uz.alex.its.beverlee.utils.Cryptographic;

public class RegisterParams {
    @Expose
    @SerializedName("firstname")
    private final String firstName;

    @Expose
    @SerializedName("lastname")
    private final String lastName;

    @Expose
    @SerializedName("phone_number")
    private final String phone;

    @Expose
    @SerializedName("email")
    private final String email;

    @Expose
    @SerializedName("country_id")
    private final long countryId;

    @Expose
    @SerializedName("city")
    private final String city;

    @Expose
    @SerializedName("password")
    private final String password;

    @Expose
    @SerializedName("password_confirmation")
    private final String passwordConfirmation;

    @Expose
    @SerializedName("device_name")
    private final String deviceName;

    public RegisterParams(final String firstName,
                          final String lastName,
                          final String phone,
                          final String email,
                          final long countryId,
                          final String city,
                          final String password,
                          final String passwordConfirmation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.countryId = countryId;
        this.city = city;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.deviceName = Cryptographic.md5(phone + Build.MANUFACTURER + Build.MODEL);;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public long getCountryId() {
        return countryId;
    }

    public String getCity() {
        return city;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public String getDeviceName() {
        return deviceName;
    }

    @NonNull
    @Override
    public String toString() {
        return "RegisterParams{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", countryId=" + countryId +
                ", city='" + city + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirmation='" + passwordConfirmation + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }
}
