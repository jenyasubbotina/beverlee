package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel.WithdrawalType;
import uz.alex.its.beverlee.view.interfaces.WithdrawalTypeCallback;

public class WithdrawalTypeAdapter extends RecyclerView.Adapter<WithdrawalTypeAdapter.WithdrawalTypeViewHolder> {
    private final Context context;
    private final WithdrawalTypeCallback callback;
    private List<WithdrawalType> withdrawalTypeList;

    public WithdrawalTypeAdapter(final Context context, final WithdrawalTypeCallback callback) {
        this(context, callback, null);
    }

    public WithdrawalTypeAdapter(final Context context, final WithdrawalTypeCallback callback, final List<WithdrawalType> withdrawalTypeList) {
        this.context = context;
        this.callback = callback;
        this.withdrawalTypeList = withdrawalTypeList;
    }

    public void setWithdrawalTypeList(final List<WithdrawalType> withdrawalTypeList) {
        this.withdrawalTypeList = withdrawalTypeList;
    }

    public List<WithdrawalType> getWithdrawalTypeList() {
        return withdrawalTypeList;
    }

    @NonNull
    @Override
    public WithdrawalTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WithdrawalTypeViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_withdrawal_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawalTypeViewHolder holder, int position) {
        if (withdrawalTypeList == null) {
            return;
        }
        if (position >= getItemCount()) {
            return;
        }

        holder.commissionRateTextView.setText(context.getString(R.string.commission_rate, withdrawalTypeList.get(position).getCommission()));
        holder.bindItem(callback, withdrawalTypeList.get(position));

        Picasso.get()
                .load(withdrawalTypeList.get(position).getIconUrl())
                .centerCrop()
                .fit()
                .into(holder.withdrawalTypeImageView);
    }

    @Override
    public int getItemCount() {
        return withdrawalTypeList != null ? withdrawalTypeList.size() : 0;
    }

    static class WithdrawalTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView withdrawalTypeImageView;
        TextView commissionRateTextView;

        public WithdrawalTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            withdrawalTypeImageView = itemView.findViewById(R.id.withdrawal_type_image_view);
            commissionRateTextView = itemView.findViewById(R.id.commission_rate_text_view);
        }

        public void bindItem(final WithdrawalTypeCallback callback, final WithdrawalType withdrawalType) {
            itemView.setOnClickListener(v -> callback.onWithdrawalTypeSelected(withdrawalType));
        }
    }
}
