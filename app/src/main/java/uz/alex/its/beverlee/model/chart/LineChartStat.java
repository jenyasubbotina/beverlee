package uz.alex.its.beverlee.model.chart;

import androidx.annotation.NonNull;

public class LineChartStat {
    private final float bonusAmount;
    private final float purchaseAmount;
    private final float receiptAmount;
    private final float transferAmount;
    private final float replenishAmount;
    private final float withdrawalAmount;

    public LineChartStat(final float bonusAmount,
                         final float purchaseAmount,
                         final float receiptAmount,
                         final float transferAmount,
                         final float replenishAmount,
                         final float withdrawalAmount) {
        this.bonusAmount = bonusAmount;
        this.purchaseAmount = purchaseAmount;
        this.receiptAmount = receiptAmount;
        this.transferAmount = transferAmount;
        this.replenishAmount = replenishAmount;
        this.withdrawalAmount = withdrawalAmount;
    }

    public float getBonusAmount() {
        return bonusAmount;
    }

    public float getPurchaseAmount() {
        return purchaseAmount;
    }

    public float getReceiptAmount() {
        return receiptAmount;
    }

    public float getTransferAmount() {
        return transferAmount;
    }

    public float getReplenishAmount() {
        return replenishAmount;
    }

    public float getWithdrawalAmount() {
        return withdrawalAmount;
    }

    @NonNull
    @Override
    public String toString() {
        return "LineChartStat{" +
                "bonusAmount=" + bonusAmount +
                ", purchaseAmount=" + purchaseAmount +
                ", receiptAmount=" + receiptAmount +
                ", transferAmount=" + transferAmount +
                ", replenishAmount=" + replenishAmount +
                ", withdrawalAmount=" + withdrawalAmount +
                '}';
    }
}
