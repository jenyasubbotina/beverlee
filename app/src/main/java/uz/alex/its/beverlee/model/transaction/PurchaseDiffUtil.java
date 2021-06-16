package uz.alex.its.beverlee.model.transaction;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class PurchaseDiffUtil extends DiffUtil.Callback {
    final List<PurchaseModel.Purchase> oldList;
    final List<PurchaseModel.Purchase> newList;

    public PurchaseDiffUtil(final List<PurchaseModel.Purchase> oldList, final List<PurchaseModel.Purchase> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList == null ? 0 : oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList == null ? 0 : newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldList.get(oldItemPosition).getId() != newList.get(newItemPosition).getId()) {
            return false;
        }
        if (!oldList.get(oldItemPosition).getDescription().equals(newList.get(newItemPosition).getDescription())) {
            return false;
        }
        if (oldList.get(oldItemPosition).getAmount() != newList.get(newItemPosition).getAmount()) {
            return false;
        }
        return oldList.get(oldItemPosition).getCreatedAt() == newList.get(newItemPosition).getCreatedAt();
    }
}