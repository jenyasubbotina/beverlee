package uz.alex.its.beverlee.model.actor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionParticipant {
    @Expose
    @SerializedName("id")
    private final long id;

    @Expose
    @SerializedName("firstname")
    @ColumnInfo(name = "first_name")
    private final String firstName;

    @Expose
    @SerializedName("lastname")
    @ColumnInfo(name = "last_name")
    private final String lastName;

    public TransactionParticipant(final long id, final String firstName, final String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @NonNull
    @Override
    public String toString() {
        return "TransactionParticipant{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
