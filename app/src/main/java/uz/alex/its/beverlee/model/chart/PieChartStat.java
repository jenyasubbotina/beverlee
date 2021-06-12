package uz.alex.its.beverlee.model.chart;

import androidx.annotation.NonNull;

public class PieChartStat {
    private final float bonusOrPurchase;
    private final float receiptOrTransfer;
    private final float replenishOrWithdrawal;

    public PieChartStat(final float bonusOrPurchase,
                        final float receiptOrTransfer,
                        final float replenishOrWithdrawal) {
        this.bonusOrPurchase = bonusOrPurchase;
        this.receiptOrTransfer = receiptOrTransfer;
        this.replenishOrWithdrawal = replenishOrWithdrawal;
    }

    public float getBonusOrPurchase() {
        return bonusOrPurchase;
    }

    public float getReceiptOrTransfer() {
        return receiptOrTransfer;
    }

    public float getReplenishOrWithdrawal() {
        return replenishOrWithdrawal;
    }

    @NonNull
    @Override
    public String toString() {
        return "StatisticsData{" +
                "bonusOrPurchase=" + bonusOrPurchase +
                ", receiptOrTransfer=" + receiptOrTransfer +
                ", replenishOrWithdrawal=" + replenishOrWithdrawal +
                '}';
    }
}
