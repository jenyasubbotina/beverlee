package uz.alex.its.beverlee.model.transaction;

import androidx.recyclerview.widget.DiffUtil;
import java.util.List;
import uz.alex.its.beverlee.model.transaction.TransactionModel.Transaction;
import uz.alex.its.beverlee.model.transaction.TransferModel.Transfer;

public class TransactionDiffUtil extends DiffUtil.Callback {
    final List<Transaction> oldList;
    final List<Transaction> newList;

    public TransactionDiffUtil(final List<Transaction> oldList, final List<Transaction> newList) {
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
        if (oldList.get(oldItemPosition).getTypeId() != newList.get(newItemPosition).getTypeId()) {
            return false;
        }
        if (!oldList.get(oldItemPosition).getTypeTitle().equals(newList.get(newItemPosition).getTypeTitle())) {
            return false;
        }
        if (oldList.get(oldItemPosition).getUserId() != newList.get(newItemPosition).getUserId()) {
            return false;
        }
        if (!oldList.get(oldItemPosition).getUserFullName().equals(newList.get(newItemPosition).getUserFullName())) {
            return false;
        }
        if (oldList.get(oldItemPosition).getAmount() != newList.get(newItemPosition).getAmount()) {
            return false;
        }
        if (oldList.get(oldItemPosition).getCreatedAt() != newList.get(newItemPosition).getCreatedAt()) {
            return false;
        }
        if (oldList.get(oldItemPosition).getTransfer() != null) {
            if (newList.get(newItemPosition).getTransfer() == null) {
                return false;
            }
            if (!oldList.get(oldItemPosition).getTransfer().equals(newList.get(newItemPosition).getTransfer())) {
                return false;
            }
        }
        if (oldList.get(oldItemPosition).getPurchase() != null) {
            if (newList.get(newItemPosition).getPurchase() == null) {
                return false;
            }
            return oldList.get(oldItemPosition).getPurchase().equals(newList.get(newItemPosition).getPurchase());
        }
        return false;
    }
}