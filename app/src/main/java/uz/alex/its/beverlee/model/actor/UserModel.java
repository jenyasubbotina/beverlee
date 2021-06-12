package uz.alex.its.beverlee.model.actor;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uz.alex.its.beverlee.model.Country;

public class UserModel {
    @Expose
    @SerializedName("draw")
    private final long draw;

    @Expose
    @SerializedName("recordsTotal")
    private final int recordsTotal;

    @Expose
    @SerializedName("recordsFiltered")
    private final int recordsFiltered;

    @Expose
    @SerializedName("data")
    private final User user;

    public UserModel(long draw, int recordsTotal, int recordsFiltered, User user) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.user = user;
    }

    public long getDraw() {
        return draw;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public User getUser() {
        return user;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", user=" + user +
                '}';
    }

    public static class User {
        @Expose
        @SerializedName("id")
        private final long id;

        @Expose
        @SerializedName("club_number")
        private final long clubNumber;

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
        @SerializedName("phone_number")
        private final String phone;

        @Expose
        @SerializedName("email")
        private final String email;

        @Expose
        @SerializedName("country")
        private final Country country;

        @Expose
        @SerializedName("city")
        private final String city;

        @Expose
        @SerializedName("position")
        private final String position;

        @Expose
        @SerializedName("address")
        private final String address;

        @Expose
        @SerializedName("avatar_url")
        private final String photoUrl;

        public User(final long id,
                    final long clubNumber,
                    final String firstName,
                    final String lastName,
                    final String middleName,
                    final String phone,
                    final String email,
                    final Country country,
                    final String city,
                    final String position,
                    final String address,
                    final String photoUrl) {
            this.id = id;
            this.clubNumber = clubNumber;
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
            this.phone = phone;
            this.email = email;
            this.country = country;
            this.city = city;
            this.position = position;
            this.address = address;
            this.photoUrl = photoUrl;
        }

        public long getId() {
            return id;
        }

        public long getClubNumber() {
            return clubNumber;
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

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public Country getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }

        public String getPosition() {
            return position;
        }

        public String getAddress() {
            return address;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        @NonNull
        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", clubNumber=" + clubNumber +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", middleName='" + middleName + '\'' +
                    ", phone='" + phone + '\'' +
                    ", email='" + email + '\'' +
                    ", country=" + country +
                    ", city='" + city + '\'' +
                    ", position='" + position + '\'' +
                    ", address='" + address + '\'' +
                    ", photo='" + photoUrl + '\'' +
                    '}';
        }
    }
}
