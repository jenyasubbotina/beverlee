package uz.alex.its.beverlee.view.adapters;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.transaction.TransactionModel.Transaction;
import uz.alex.its.beverlee.model.transaction.TransactionDiffUtil;
import uz.alex.its.beverlee.utils.DateFormatter;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<Transaction> transactionList;

    public TransactionAdapter(final Context context) {
        this(context, null);
    }

    public TransactionAdapter(final Context context, final List<Transaction> transactionList) {
        this.context = context;
        setTransactionList(transactionList);
    }

    public void setTransactionList(final List<Transaction> newTransactionList) {
        final TransactionDiffUtil diffUtil = new TransactionDiffUtil(transactionList, newTransactionList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtil);
        this.transactionList = newTransactionList;
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemViewType(int position) {
        if (transactionList == null) {
            return 0;
        }
        if (position == 0) {
            return TYPE_DATE;
        }
        if ((transactionList.get(position - 1).getCreatedAt()/86400) != (transactionList.get(position).getCreatedAt()/86400)) {
            return TYPE_DATE;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return transactionList == null ? 0 : transactionList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_DATE) {
            return new TransactionDateViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_date_stroke, parent, false));
        }
        else {
            return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.view_holder_transaction, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == 0) {
            throw new IllegalArgumentException("unknown viewHolder viewType");
        }
        TransactionViewHolder holder = (TransactionViewHolder) viewHolder;

        holder.transactionTypeTextView.setText(transactionList.get(position).getTypeTitle());

        switch (transactionList.get(position).getTypeId()) {
            case 1: {
                holder.transactionImageView.setImageResource(R.drawable.ic_bonus);
                break;
            }
            case 2: {
                holder.transactionImageView.setImageResource(R.drawable.ic_purchase);
                break;
            }
            case 3: {
                holder.transactionImageView.setImageResource(R.drawable.ic_transfer_from);
                break;
            }
            case 4: {
                holder.transactionImageView.setImageResource(R.drawable.ic_transfer_to);
                break;
            }
            case 5:
            case 6: {
                holder.transactionImageView.setImageResource(R.drawable.ic_replenish_withdrawal);
                break;
            }
        }
        if (transactionList.get(position).getTypeId() == 1) {
            holder.transactionFromTextView.setText(null);
        }
        else if (transactionList.get(position).getTypeId() == 3) {
            holder.transactionFromTextView.setText(context.getString(R.string.transaction_from, transactionList.get(position).getTransfer().getSender().getFirstName(), transactionList.get(position).getTransfer().getSender().getLastName()));
        }
        else if (transactionList.get(position).getTypeId() == 4) {
            holder.transactionFromTextView.setText(context.getString(R.string.transaction_from, transactionList.get(position).getTransfer().getRecipient().getFirstName(), transactionList.get(position).getTransfer().getRecipient().getLastName()));
        }
        if (transactionList.get(position).isBalanceIncrease()) {
            holder.transactionAmountTextView.setText(context.getString(R.string.transaction_amount, "+", transactionList.get(position).getAmount()));
            holder.transactionAmountTextView.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorGreenBright, null));
        }
        else  {
            holder.transactionAmountTextView.setText(context.getString(R.string.transaction_amount, "-", transactionList.get(position).getAmount()));
            holder.transactionAmountTextView.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.colorBlack, null));
        }
        if (getItemViewType(position) == TYPE_DATE) {
            TransactionDateViewHolder dateHolder = (TransactionDateViewHolder) viewHolder;
            dateHolder.transactionDateTextView.setText(context.getString(R.string.transaction_date,
                    DateFormatter.timestampToDayMonthDate(transactionList.get(position).getCreatedAt())));
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView transactionTypeTextView;
        TextView transactionFromTextView;
        TextView transactionAmountTextView;
        ImageView transactionImageView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            transactionTypeTextView = itemView.findViewById(R.id.transaction_type_text_view);
            transactionFromTextView = itemView.findViewById(R.id.transaction_from_text_view);
            transactionAmountTextView = itemView.findViewById(R.id.transaction_amount_text_view);
            transactionImageView = itemView.findViewById(R.id.transaction_image_view);
        }
    }

    static class TransactionDateViewHolder extends TransactionViewHolder {
        public TextView transactionDateTextView;

        public TransactionDateViewHolder(@NonNull View itemView) {
            super(itemView);

            transactionDateTextView = itemView.findViewById(R.id.transaction_date_text_view);
        }
    }

    public static final int TYPE_DATE = 1;
    public static final int TYPE_ITEM = 2;
    private static final String TAG = TransactionAdapter.class.toString();
}
