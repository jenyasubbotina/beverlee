package uz.alex.its.beverlee.view.interfaces;

import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.view.adapters.ContactAdapter;

public interface ContactCallback {
    void onContactSelected(final ContactData contact, final ContactAdapter.ContactViewHolder holder);
}
