package uz.alex.its.beverlee.model.notification;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class PushDiffUtil extends DiffUtil.Callback {
    final List<Push> oldList;
    final List<Push> newList;

    public PushDiffUtil(final List<Push> oldList, final List<Push> newList) {
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
        return oldList.get(oldItemPosition).getNotificationId() == newList.get(newItemPosition).getNotificationId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        if (oldList.get(oldItemPosition).getNotificationId() != newList.get(newItemPosition).getNotificationId()) {
            return false;
        }
        if (oldList.get(oldItemPosition).getNewsId() != newList.get(newItemPosition).getNewsId()) {
            return false;
        }
        if (oldList.get(oldItemPosition).getTimestamp() != newList.get(newItemPosition).getTimestamp()) {
            return false;
        }
        if (oldList.get(oldItemPosition).getStatus() != newList.get(newItemPosition).getStatus()) {
            return false;
        }
        if (!oldList.get(oldItemPosition).getTitle().equals(newList.get(newItemPosition).getTitle())) {
            return false;
        }
        if (!oldList.get(oldItemPosition).getBody().equals(newList.get(newItemPosition).getBody())) {
            return false;
        }
        return oldList.get(oldItemPosition).getType().equals(newList.get(newItemPosition).getType());
    }
}
