package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.actor.ContactModel.Contact;
import uz.alex.its.beverlee.model.actor.ContactModel.ContactData;
import uz.alex.its.beverlee.model.actor.ContactDiffUtil;
import uz.alex.its.beverlee.view.interfaces.ContactCallback;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private final Context context;
    private final ContactCallback contactCallback;
    private List<ContactData> contactList;
    private final int type;

    public ContactAdapter(@NonNull final Context context, @NonNull final ContactCallback contactCallback, final int contactType) {
        this.context = context;
        this.contactCallback = contactCallback;
        this.type = contactType;
    }

    public void setContactList(@Nullable final List<ContactData> newContactList) {
        final ContactDiffUtil diffUtil = new ContactDiffUtil(contactList, newContactList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        this.contactList = newContactList;
        diffResult.dispatchUpdatesTo(this);
    }

    public List<ContactData> getContactList() {
        return contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_VERTICAL) {
            return new ContactVerticalViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_contact_vertical, parent, false));
        }
        if (viewType == TYPE_HORIZONTAL) {
            return new ContactHorizontalViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_contact_horizontal, parent, false));
        }
        if (viewType == TYPE_SPINNER) {
            return new ContactSpinnerViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_contact_spinner, parent, false));
        }
        throw new IllegalStateException("unknown view holder type");
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.contactNameTextView.setText(contactList.get(position).getContact().getFio());
        holder.bind(contactList.get(position), contactCallback);
        /* spinner view holder */
        if (getItemViewType(position) == TYPE_SPINNER) {
            final ContactSpinnerViewHolder spinnerViewHolder = (ContactSpinnerViewHolder) holder;
            spinnerViewHolder.contactIdTextView.setText(context.getString(R.string.contact_id, contactList.get(position).getId()));
            return;
        }
        /* horizontal view holder */
        if (getItemViewType(position) == TYPE_HORIZONTAL) {
            final ContactHorizontalViewHolder horizontalViewHolder = (ContactHorizontalViewHolder) holder;
            horizontalViewHolder.checkImageView.setVisibility(View.INVISIBLE);
            return;
        }
        /* vertical view holder */
        if (getItemViewType(position) == TYPE_VERTICAL) {
            final ContactVerticalViewHolder verticalViewHolder = (ContactVerticalViewHolder) holder;
            verticalViewHolder.checkImageView.setVisibility(View.INVISIBLE);

            if (contactList.get(position).getContact().isFav()) {
                verticalViewHolder.favIconImageView.setVisibility(View.VISIBLE);
                return;
            }
            verticalViewHolder.favIconImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return contactList == null ? 0 : contactList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type;
    }

    public static class ContactVerticalViewHolder extends ContactViewHolder {
        public ConstraintLayout viewHolderLayout;
        public ImageView checkImageView;
        public ImageView favIconImageView;

        public ContactVerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            viewHolderLayout = itemView.findViewById(R.id.view_holder_layout);
            favIconImageView = itemView.findViewById(R.id.fav_icon_image_view);
            checkImageView = itemView.findViewById(R.id.check_image_view);
        }

        public void bind(final ContactData contact, final ContactCallback contactCallback) {
            itemView.setOnClickListener(v -> contactCallback.onContactSelected(contact, this));
        }
    }

    public static class ContactHorizontalViewHolder extends ContactViewHolder {
        public ImageView checkImageView;

        public ContactHorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            checkImageView = itemView.findViewById(R.id.check_image_view);
        }

        public void bind(final ContactData contact, final ContactCallback contactCallback) {
            itemView.setOnClickListener(v -> contactCallback.onContactSelected(contact, this));
        }
    }

    public static class ContactSpinnerViewHolder extends ContactViewHolder {
        public TextView contactIdTextView;

        public ContactSpinnerViewHolder(@NonNull View itemView) {
            super(itemView);
            contactIdTextView = itemView.findViewById(R.id.id_text_view);
        }

        @Override
        public void bind(ContactData contactData, ContactCallback contactCallback) {
            itemView.setOnClickListener(v -> contactCallback.onContactSelected(contactData, this));
        }
    }

    public static abstract class ContactViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarImageView;
        public TextView contactNameTextView;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatar_image_view);
            contactNameTextView = itemView.findViewById(R.id.name_text_view);
        }

        public abstract void bind(final ContactData contactData, final ContactCallback contactCallback);
    }

    public static final int TYPE_HORIZONTAL = 0;
    public static final int TYPE_VERTICAL = 1;
    public static final int TYPE_SPINNER = 2;

    private static final String TAG = ContactAdapter.class.toString();
}
