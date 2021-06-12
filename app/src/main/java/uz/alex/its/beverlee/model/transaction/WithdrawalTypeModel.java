package uz.alex.its.beverlee.model.transaction;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WithdrawalTypeModel {
    @Expose
    @SerializedName("draw")
    private final long draw;

    @Expose
    @SerializedName("recordsTotal")
    private final long recordsTotal;

    @Expose
    @SerializedName("recordsFiltered")
    private final long recordsFiltered;

    @Expose
    @SerializedName("data")
    private final List<WithdrawalType> withdrawalTypeList;

    public WithdrawalTypeModel(final long draw, final long recordsTotal, final long recordsFiltered, final List<WithdrawalType> withdrawalTypeList) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.withdrawalTypeList = withdrawalTypeList;
    }

    public long getDraw() {
        return draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public List<WithdrawalType> getWithdrawalTypeList() {
        return withdrawalTypeList;
    }

    @NonNull
    @Override
    public String toString() {
        return "CountriesResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", withdrawalTypeList=" + withdrawalTypeList +
                '}';
    }

    public static class WithdrawalType {
        @Expose
        @SerializedName("type")
        private final String type;

        @Expose
        @SerializedName("method")
        private final String method;

        @Expose
        @SerializedName("commission")
        private final int commission;

        @Expose
        @SerializedName("icon")
        private final String iconUrl;

        public WithdrawalType(final String type, final String method, final int commission, final String iconUrl) {
            this.type = type;
            this.method = method;
            this.commission = commission;
            this.iconUrl = iconUrl;
        }

        public String getType() {
            return type;
        }

        public String getMethod() {
            return method;
        }

        public int getCommission() {
            return commission;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        @NonNull
        @Override
        public String toString() {
            return "WithdrawalType{" +
                    "type='" + type + '\'' +
                    ", method='" + method + '\'' +
                    ", commission=" + commission +
                    ", iconUrl='" + iconUrl + '\'' +
                    '}';
        }
    }
}
