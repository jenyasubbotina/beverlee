package uz.alex.its.beverlee.model.transaction;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PurchaseModel {
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
    private final List<Purchase> purchaseList;

    public PurchaseModel(final long draw, final int recordsTotal, final int recordsFiltered, final List<Purchase> purchaseList) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.purchaseList = purchaseList;
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

    public List<Purchase> getPurchaseList() {
        return purchaseList;
    }

    @NonNull
    @Override
    public String toString() {
        return "NewsResponse{" +
                "draw=" + draw +
                ", recordsTotal=" + recordsTotal +
                ", recordsFiltered=" + recordsFiltered +
                ", newsList=" + purchaseList +
                '}';
    }

    public static class PurchaseResponse {
        @Expose
        @SerializedName("url")
        private final String url;

        @Expose
        @SerializedName("balance")
        private final double balance;

        public PurchaseResponse(final String url, final double balance) {
            this.url = url;
            this.balance = balance;
        }

        public String getUrl() {
            return url;
        }

        public double getBalance() {
            return balance;
        }

        @NonNull
        @Override
        public String toString() {
            return "PurchaseResult{" +
                    "url='" + url + '\'' +
                    ", balance=" + balance +
                    '}';
        }
    }

    public static class PurchaseTransaction {
        @Expose
        @SerializedName("product")
        private final Product product;

        @Expose
        @SerializedName("amount")
        @ColumnInfo(name = "purchase_amount")
        private final double amount;

        public PurchaseTransaction(final Product product, final double amount) {
            this.product = product;
            this.amount = amount;
        }

        public Product getProduct() {
            return product;
        }

        public double getAmount() {
            return amount;
        }

        @NonNull
        @Override
        public String toString() {
            return "PurchaseTransaction{" +
                    "product=" + product +
                    ", amount=" + amount +
                    '}';
        }
    }

    public static class Product {
        @Expose
        @SerializedName("id")
        @ColumnInfo(name = "product_id")
        private final long id;

        @Expose
        @SerializedName("order_id")
        @ColumnInfo(name = "order_id")
        private final long orderId;

        @Expose
        @SerializedName("title")
        @ColumnInfo(name = "title")
        private final String title;

        public Product(final long id, final long orderId, final String title) {
            this.id = id;
            this.orderId = orderId;
            this.title = title;
        }

        public long getId() {
            return id;
        }

        public long getOrderId() {
            return orderId;
        }

        public String getTitle() {
            return title;
        }

        @NonNull
        @Override
        public String toString() {
            return "Product{" +
                    "id=" + id +
                    ", orderId=" + orderId +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public static class Purchase {
        @Expose
        @SerializedName("id")
        @ColumnInfo(name = "purchase_id")
        private final long id;

        @Expose
        @SerializedName("amount")
        @ColumnInfo(name = "purchase_amount")
        private final double amount;

        @Expose
        @SerializedName("description")
        @ColumnInfo(name = "purchase_description")
        private final String description;

        @Expose
        @SerializedName("created_at")
        @ColumnInfo(name = "purchase_created_at")
        private final long createdAt;

        public Purchase(final long id, final double amount, final String description, final long createdAt) {
            this.id = id;
            this.amount = amount;
            this.description = description;
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public double getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        @NonNull
        @Override
        public String toString() {
            return "Purchase{" +
                    "id=" + id +
                    ", amount=" + amount +
                    ", description='" + description + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}
