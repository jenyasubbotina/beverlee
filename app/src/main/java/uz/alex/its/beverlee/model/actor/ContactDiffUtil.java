package uz.alex.its.beverlee.model.actor;

import androidx.recyclerview.widget.DiffUtil;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import java.util.List;

public class ContactDiffUtil extends DiffUtil.Callback {
    final List<ContactModel.Contact> oldList;
    final List<ContactModel.Contact> newList;

    public ContactDiffUtil(final List<ContactModel.Contact> oldList, final List<ContactModel.Contact> newList) {
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
        if (!oldList.get(oldItemPosition).getFio().equals(newList.get(newItemPosition).getFio())) {
            return false;
        }
        return !oldList.get(oldItemPosition).isFav() == newList.get(newItemPosition).isFav();
    }
}