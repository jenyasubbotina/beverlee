package uz.alex.its.beverlee.model.transaction;

import androidx.annotation.NonNull;

public class TransactionParams {
    private boolean isIncrease;
    private int year;
    private int month;
    private int firstDay;
    private int lastDay;
    private int transactionTypeId;
    private String searchQuery;

    public TransactionParams() {
        this.isIncrease = true;
        this.year = 0;
        this.month = 0;
        this.firstDay = 0;
        this.lastDay = 0;
        this.transactionTypeId = 0;
        this.searchQuery = null;
    }

    public TransactionParams(final TransactionParams params) {
        this.isIncrease = params.isIncrease();
        this.year = params.getYear();
        this.month = params.getMonth();
        this.firstDay = params.getFirstDay();
        this.lastDay = params.getLastDay();
        this.transactionTypeId = params.getTransactionTypeId();
        this.searchQuery = params.getSearchQuery();
    }

    public TransactionParams(final boolean isIncrease,
                             final int year,
                             final int month,
                             final int firstDay,
                             final int lastDay,
                             final int transactionTypeId,
                             final String searchQuery) {
        this.isIncrease = isIncrease;
        this.year = year;
        this.month = month;
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.transactionTypeId = transactionTypeId;
        this.searchQuery = searchQuery;
    }

    public void incrementMonth() {
        this.month++;
    }

    public void decrementMonth() {
        this.month--;
    }

    public void incrementYear() {
        this.year++;
    }

    public void decrementYear() {
        this.year--;
    }

    public void setTransactionTypeId(int transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    public void setMonth(final int month) {
        this.month = month;
    }

    public void setIncrease(final boolean isIncrease) {
        this.isIncrease = isIncrease;
    }

    public void setFirstDay(final int firstDay) {
        this.firstDay = firstDay;
    }

    public void setLastDay(final int lastDay) {
        this.lastDay = lastDay;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getFirstDay() {
        return firstDay;
    }

    public int getLastDay() {
        return lastDay;
    }

    public int getTransactionTypeId() {
        return transactionTypeId;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public boolean isIncrease() {
        return isIncrease;
    }

    @NonNull
    @Override
    public String toString() {
        return "TransactionParams{" +
                "year=" + year +
                ", month=" + month +
                ", firstDay=" + firstDay +
                ", lastDay=" + lastDay +
                ", transactionTypeId=" + transactionTypeId +
                ", searchQuery='" + searchQuery + '\'' +
                '}';
    }
}
