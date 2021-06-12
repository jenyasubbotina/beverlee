package uz.alex.its.beverlee.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link {
    @Expose
    @SerializedName("prev")
    private final String prev;

    @Expose
    @SerializedName("next")
    private final String next;

    public Link(final String prev, final String next) {
        this.prev = prev;
        this.next = next;
    }

    public String getPrev() {
        return prev;
    }

    public String getNext() {
        return next;
    }

    @NonNull
    @Override
    public String toString() {
        return "Link{" +
                "prev='" + prev + '\'' +
                ", next='" + next + '\'' +
                '}';
    }
}
