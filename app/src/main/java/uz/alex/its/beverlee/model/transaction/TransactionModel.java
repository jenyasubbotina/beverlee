package uz.alex.its.beverlee.model.transaction;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import uz.alex.its.beverlee.model.transaction.PurchaseModel.Purchase;
import uz.alex.its.beverlee.model.transaction.TransferModel.Transfer;

public class TransactionModel {
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
    private final List<Transaction> transactionList;

    public TransactionModel(final long draw, final int recordsTotal, final int recordsFiltered, final List<Transaction> transactionList) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.transactionList = transactionList;
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

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewsResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", transactionList=" + transactionList +
                '}';
    }

    public static class Transaction {
        @Expose
        @SerializedName("id")
        private final long id;

        /* 1 -> Начисление бонуса; 2 -> Покупка в магазине; 3 -> Перевод(Получение); 4 -> Перевод(Отправка); 5 -> Пополнение(Платежка); 6 -> Вывод(Платежка); */
        @Expose
        @SerializedName("type_id")
        private final int typeId;

        @Expose
        @SerializedName("type_title")
        private final String typeTitle;

        @Expose
        @SerializedName("user_id")
        private final long userId;

        @Expose
        @SerializedName("user_fio")
        private final String userFullName;

        @Expose
        @SerializedName("amount")
        private final double amount;

        @Expose
        @SerializedName("is_balance_increase")
        private final boolean isBalanceIncrease;

        @Expose
        @SerializedName("created_at")
        private final long createdAt;

        @Expose
        @SerializedName("buy")
        private final Purchase purchase;

        @Expose
        @SerializedName("transfer")
        private final Transfer transfer;

//    @Expose
//    @SerializedName("withdrawal")
//    private Withdrawal withdrawal;

        public Transaction(final long id,
                           final int typeId,
                           final String typeTitle,
                           final long userId,
                           final String userFullName,
                           final double amount,
                           final boolean isBalanceIncrease,
                           final Purchase purchase,
                           final Transfer transfer,
                           final long createdAt) {
            this.id = id;
            this.typeId = typeId;
            this.typeTitle = typeTitle;
            this.userId = userId;
            this.userFullName = userFullName;
            this.amount = amount;
            this.isBalanceIncrease = isBalanceIncrease;
            this.purchase = purchase;
            this.transfer = transfer;
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public int getTypeId() {
            return typeId;
        }

        public String getTypeTitle() {
            return typeTitle;
        }

        public long getUserId() {
            return userId;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public double getAmount() {
            return amount;
        }

        public boolean isBalanceIncrease() {
            return isBalanceIncrease;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public Purchase getPurchase() {
            return purchase;
        }

        public Transfer getTransfer() {
            return transfer;
        }

        @NonNull
        @Override
        public String toString() {
            return "Transaction{" +
                    "id=" + id +
                    ", typeId=" + typeId +
                    ", typeTitle='" + typeTitle + '\'' +
                    ", userId=" + userId +
                    ", userFullName='" + userFullName + '\'' +
                    ", amount=" + amount +
                    ", isBalanceIncrease=" + isBalanceIncrease +
                    ", createdAt=" + createdAt +
                    ", purchase=" + purchase +
                    ", transfer=" + transfer +
                    '}';
        }
    }
}
