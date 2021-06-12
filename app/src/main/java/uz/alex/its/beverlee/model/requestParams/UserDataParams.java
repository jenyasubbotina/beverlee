package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDataParams {
    @Expose
    @SerializedName("firstname")
    private final String firstName;

    @Expose
    @SerializedName("lastname")
    private final String lastName;

    @Expose
    @SerializedName("patronymic")
    private final String middleName;

    @Expose
    @SerializedName("email")
    private final String email;

    @Expose
    @SerializedName("city")
    private final String city;

    @Expose
    @SerializedName("address")
    private final String address;

    public UserDataParams(String firstName, String lastName, String middleName, String email, String city, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.city = city;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserDataParams{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
