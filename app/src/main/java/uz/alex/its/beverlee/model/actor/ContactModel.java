package uz.alex.its.beverlee.model.actor;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ContactModel {
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
    private final List<ContactData> contactData;

    public ContactModel(final long draw, final int recordsTotal, final int recordsFiltered, final List<ContactData> contactData) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.contactData = contactData;
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

    public List<ContactData> getContactData() {
        return contactData;
    }

    public static class ContactData {
        @Expose
        @SerializedName("id")
        private final long id;

        @Expose
        @SerializedName("user")
        private final Contact contact;

        public ContactData(final long id, final Contact contact) {
            this.id = id;
            this.contact = contact;
        }

        public long getId() {
            return id;
        }

        public Contact getContact() {
            return contact;
        }

        @NonNull
        @Override
        public String toString() {
            return "ContactData{" +
                    "id=" + id +
                    ", contact=" + contact +
                    '}';
        }
    }

    @Entity(tableName = "contact_list")
    public static class Contact implements Serializable {
        @Expose
        @SerializedName("id")
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "id")
        private final long id;

        @ColumnInfo(name = "contact_id", index = true)
        private final long contactId;

        @Expose
        @SerializedName("fio")
        @ColumnInfo(name = "full_name")
        private final String fio;

        @ColumnInfo(name = "is_fav", defaultValue = "false")
        private final boolean isFav;

        public Contact(final long id, final long contactId, final String fio, final boolean isFav) {
            this.id = id;
            this.contactId = contactId;
            this.fio = fio;
            this.isFav = isFav;
        }

        public long getId() {
            return id;
        }

        public String getFio() {
            return fio;
        }

        public boolean isFav() {
            return isFav;
        }

        public long getContactId() {
            return contactId;
        }

        @NonNull
        @Override
        public String toString() {
            return "Contact{" +
                    "id=" + id +
                    ", contactId='" + contactId +
                    ", fio='" + fio + '\'' +
                    ", isFav=" + isFav +
                    '}';
        }
    }
}
