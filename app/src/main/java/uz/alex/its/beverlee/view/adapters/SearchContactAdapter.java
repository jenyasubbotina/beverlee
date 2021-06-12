//package uz.alex.its.beverlee.view.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import uz.alex.its.beverlee.R;
//import uz.alex.its.beverlee.model.actor.ContactData;
//import uz.alex.its.beverlee.model.actor.ContactDiffUtil;
//
//public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ContactViewHolder> implements Filterable {
//    private final Context context;
//    private List<ContactData> contactDataList;
//
//    public SearchContactAdapter(@NonNull Context context, int resource) {
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ContactViewHolder(LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
//
//    }
//
//    public void setContactDataList(List<ContactData> newContactList) {
//        final ContactDiffUtil diffUtil = new ContactDiffUtil(contactDataList, newContactList);
//        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
//        this.contactDataList = newContactList;
//        diffResult.dispatchUpdatesTo(this);
//    }
//
//    @Override
//    public int getItemCount() {
//        return contactDataList == null ? 0 : contactDataList.size();
//    }
//
//    @NonNull
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                if (constraint.length() > 3) {
//
//                }
//
//                FilterResults r = new FilterResults();
//                r.values = result;
//                r.count = result.size();
//                return r;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                if (results.count > 0) {
////                    notifyDataSetChanged();
//                }
//                else {
////                    notifyDataSetInvalidated();
//                }
//            }
//        };
//    }
//
//    static class ContactViewHolder extends RecyclerView.ViewHolder {
//        TextView nameTextView;
//
//        public ContactViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.name_text_view);
//        }
//    }
//}
