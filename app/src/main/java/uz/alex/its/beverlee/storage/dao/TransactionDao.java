package uz.alex.its.beverlee.storage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import uz.alex.its.beverlee.model.transaction.TransactionModel;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertTransactionList(final List<TransactionModel.Transaction> transactionList);

    @Query("SELECT * FROM transactions ORDER BY created_at DESC")
    LiveData<List<TransactionModel.Transaction>> getAllTransactionList();

    @Query("SELECT * FROM transactions WHERE created_at >= :from AND created_at <= :to AND " +
            "is_balance_increase == :isIncrease AND " +
            "type_id == :transactionType AND " +
            "sender LIKE '%' || :searchQuery || '%' OR " +
            "recipient LIKE '%' || :searchQuery || '%' OR " +
            "product LIKE '%' || :searchQuery || '%' OR " +
            "request LIKE '%' || :searchQuery || '%' OR " +
            "user_fio LIKE '%' || :searchQuery || '%'" +
            "ORDER BY created_at DESC")
    LiveData<List<TransactionModel.Transaction>> getTransactionListBySearchQuery(
            final boolean isIncrease,
            final int transactionType,
            final String searchQuery,
            final long from,
            final long to);

    @Query("SELECT * FROM transactions WHERE created_at >= :from AND created_at <= :to AND " +
            "is_balance_increase == :isIncrease AND " +
            "sender LIKE '%' || :searchQuery || '%' OR " +
            "recipient LIKE '%' || :searchQuery || '%' OR " +
            "product LIKE '%' || :searchQuery || '%' OR " +
            "request LIKE '%' || :searchQuery || '%' OR " +
            "user_fio LIKE '%' || :searchQuery || '%'" +
            "ORDER BY created_at DESC")
    LiveData<List<TransactionModel.Transaction>> getTransactionListBySearchQuery(
            final boolean isIncrease,
            final String searchQuery,
            final long from,
            final long to);

    @Query("SELECT * FROM transactions WHERE created_at >= :from AND created_at <= :to AND " +
            "is_balance_increase == :isIncrease AND " +
            "type_id == :transactionType ORDER BY created_at DESC")
    LiveData<List<TransactionModel.Transaction>> getTransactionListBySearchQuery(
            final boolean isIncrease,
            final int transactionType,
            final long from,
            final long to);

    @Query("SELECT * FROM transactions WHERE created_at >= :from AND created_at <= :to AND " +
            "is_balance_increase == :isIncrease ORDER BY created_at DESC")
    LiveData<List<TransactionModel.Transaction>> getTransactionListBySearchQuery(
            final boolean isIncrease,
            final long from,
            final long to);
}
