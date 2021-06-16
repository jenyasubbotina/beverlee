package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.transaction.PurchaseDiffUtil;
import uz.alex.its.beverlee.model.transaction.PurchaseModel;
import uz.alex.its.beverlee.view.interfaces.PurchaseCallback;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {
    private final Context context;
    private final PurchaseCallback callback;
    private List<PurchaseModel.Purchase> itemList;

    public PurchaseAdapter(final Context context, final PurchaseCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void setPurchaseList(final List<PurchaseModel.Purchase> purchaseList) {
        final PurchaseDiffUtil diffUtil = new PurchaseDiffUtil(itemList, purchaseList);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(diffUtil);
        this.itemList = purchaseList;
        result.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new PurchaseViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_purchase, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PurchaseViewHolder holder, int position) {
        holder.titleTextView.setText(itemList.get(position).getDescription());
        holder.priceTextView.setText(context.getString(R.string.purchase_price, String.valueOf(itemList.get(position).getAmount())));
        holder.bindDeleteFromBasketTextView(callback, itemList.get(position), position);
        holder.bindPurchaseBtn(callback, itemList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public List<PurchaseModel.Purchase> getPurchaseList() {
        return itemList;
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView priceTextView;
        TextView deleteFromBasketTextView;
        Button purchaseBtn;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.purchase_title_text_view);
            priceTextView = itemView.findViewById(R.id.purchase_price_text_view);
            deleteFromBasketTextView = itemView.findViewById(R.id.delete_from_basket_text_view);
            purchaseBtn = itemView.findViewById(R.id.purchase_btn);
        }

        void bindDeleteFromBasketTextView(final PurchaseCallback callback, final PurchaseModel.Purchase item, final int position) {
            deleteFromBasketTextView.setOnClickListener(v -> {
                callback.onDeleteFromBasketClicked(item, position);
            });
        }

        public void bindPurchaseBtn(final PurchaseCallback callback, final PurchaseModel.Purchase item, final int position) {
            purchaseBtn.setOnClickListener(v -> {
                callback.onPurchaseBtnClicked(item, position);
            });
        }
    }
}
