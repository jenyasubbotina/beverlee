package uz.alex.its.beverlee.model.requestParams;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WithdrawalParams {
    @Expose
    @SerializedName("type")
    private final String type;

    @Expose
    @SerializedName("method")
    private final String method;

    @Expose
    @SerializedName("amount")
    private final double amount;

    @Expose
    @SerializedName("card")
    private final String cardNumber;

    @Expose
    @SerializedName("fio")
    private final String fullName;

    @Expose
    @SerializedName("phone")
    private final String phone;

    @Expose
    @SerializedName("country")
    private final String countryTitle;

    @Expose
    @SerializedName("city")
    private final String city;

    public WithdrawalParams(final String type,
                            final String method,
                            final double amount,
                            final String cardNumber,
                            final String fullName,
                            final String phone,
                            final String countryTitle,
                            final String city) {
        this.type = type;
        this.method = method;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.fullName = fullName;
        this.phone = phone;
        this.countryTitle = countryTitle;
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public String getMethod() {
        return method;
    }

    public double getAmount() {
        return amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountryTitle() {
        return countryTitle;
    }

    public String getCity() {
        return city;
    }

    @NonNull
    @Override
    public String toString() {
        return "WithdrawalParams{" +
                "type='" + type + '\'' +
                ", method='" + method + '\'' +
                ", amount='" + amount + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", countryTitle='" + countryTitle + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
