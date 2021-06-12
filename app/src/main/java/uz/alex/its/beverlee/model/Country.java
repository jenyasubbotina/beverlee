package uz.alex.its.beverlee.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "country", indices = {@Index(value = "title")})
public class Country {
    @Expose
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "id")
    private final long id;

    @Expose
    @SerializedName("title")
    @ColumnInfo(name = "title")
    private final String title;

    @Expose
    @SerializedName("code")
    @ColumnInfo(name = "code")
    private final String code;

    @Expose
    @SerializedName("flag")
    @ColumnInfo(name = "flag")
    private final String flag;

    public Country(final long id, final String title, final String code, final String flag) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.flag = flag;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public String getFlag() {
        return flag;
    }

    @NonNull
    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }
}
