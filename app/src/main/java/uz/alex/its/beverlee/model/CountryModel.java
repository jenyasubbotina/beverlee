package uz.alex.its.beverlee.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import uz.alex.its.beverlee.model.Country;

public class CountryModel {
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
    private final List<Country> countryList;

    public CountryModel(final long draw, final long recordsTotal, final long recordsFiltered, final List<Country> countryList) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.countryList = countryList;
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

    public List<Country> getCountryList() {
        return countryList;
    }

    @NonNull
    @Override
    public String toString() {
        return "CountriesResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", countryList=" + countryList +
                '}';
    }
}
