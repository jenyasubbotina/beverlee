package uz.alex.its.beverlee.model.transaction;

import androidx.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uz.alex.its.beverlee.model.actor.TransactionParticipant;
import uz.alex.its.beverlee.model.balance.Balance;

public class TransferModel {
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
    private final Balance balance;

    public TransferModel(final long draw, final int recordsTotal, final int recordsFiltered, final Balance balance) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.balance = balance;
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

    public Balance getBalance() {
        return balance;
    }

    @NonNull
    @Override
    public String toString() {
        return "TransferResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", balance=" + balance +
                '}';
    }

    public static class Transfer {
        @Expose
        @SerializedName("id")
        private final long id;

        @Expose
        @SerializedName("transaction_id")
        private final long transactionId;

        @Expose
        @SerializedName("sender_id")
        private final long senderId;

        @Expose
        @SerializedName("recipient_id")
        private final long recipientId;

        @Expose
        @SerializedName("type")
        private final String type;

        @Expose
        @SerializedName("amount")
        private final double amount;

        @Expose
        @SerializedName("sender")
        private final TransactionParticipant sender;

        @Expose
        @SerializedName("recipient")
        private final TransactionParticipant recipient;

        public Transfer(
                final long id,
                final long transactionId,
                final long senderId,
                final long recipientId,
                final String type,
                final double amount,
                final TransactionParticipant sender,
                final TransactionParticipant recipient) {
            this.id = id;
            this.transactionId = transactionId;
            this.senderId = senderId;
            this.recipientId = recipientId;
            this.type = type;
            this.amount = amount;
            this.sender = sender;
            this.recipient = recipient;
        }

        public long getId() {
            return id;
        }

        public long getTransactionId() {
            return transactionId;
        }

        public long getSenderId() {
            return senderId;
        }

        public long getRecipientId() {
            return recipientId;
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public TransactionParticipant getSender() {
            return sender;
        }

        public TransactionParticipant getRecipient() {
            return recipient;
        }

        @NonNull
        @Override
        public String toString() {
            return "Transfer{" +
                    "id=" + id +
                    ", transactionId=" + transactionId +
                    ", senderId=" + senderId +
                    ", recipientId=" + recipientId +
                    ", type='" + type + '\'' +
                    ", amount=" + amount +
                    ", sender=" + sender +
                    ", recipient=" + recipient +
                    '}';
        }
    }
}
