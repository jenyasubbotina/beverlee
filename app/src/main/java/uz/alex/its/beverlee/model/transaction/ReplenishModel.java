package uz.alex.its.beverlee.model.transaction;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReplenishModel {
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
    private final ReplenishLink url;

    public ReplenishModel(final long draw, final int recordsTotal, final int recordsFiltered, final ReplenishLink url) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.url = url;
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

    public ReplenishLink getReplenishLink() {
        return url;
    }

    @NonNull
    @Override
    public String toString() {
        return "ReplenishModel{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", urlList=" + url +
                '}';
    }

    public static class ReplenishLink {
        private final String url;

        public ReplenishLink(final String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        @NonNull
        @Override
        public String toString() {
            return "ReplenishLink{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }
}
