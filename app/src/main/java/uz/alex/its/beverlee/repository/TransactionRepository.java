package uz.alex.its.beverlee.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import retrofit2.Callback;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.transaction.TransactionModel;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel;
import uz.alex.its.beverlee.model.balance.Balance;
import uz.alex.its.beverlee.model.balance.DaysBalance;
import uz.alex.its.beverlee.model.balance.MonthBalance;
import uz.alex.its.beverlee.storage.LocalDatabase;
import uz.alex.its.beverlee.storage.dao.TransactionDao;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.utils.DateFormatter;
import uz.alex.its.beverlee.worker.transaction.ReplenishWorker;
import uz.alex.its.beverlee.worker.transaction.TransferWorker;
import uz.alex.its.beverlee.worker.transaction.VerifyTransferWorker;
import uz.alex.its.beverlee.worker.transaction.WithdrawFundsWorker;

public class TransactionRepository {
    private final Context context;
    private final TransactionDao transactionDao;

    public TransactionRepository(final Context context) {
        this.context = context;
        this.transactionDao = LocalDatabase.getInstance(context).transactionDao();
    }

    /* Текущий баланс в уе */
    public void getCurrentBalance(final Callback<Balance> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getCurrentBalance(callback);
    }

    /* Поступления/списания за месяц */
//    public void getMonthlyBalance(final int month, final Callback<MonthBalance> callback) {
//        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
//        RetrofitClient.getInstance(context).getMonthlyBalanceHistory(month, callback);
//    }

    /* Поступления/списания за месяц по дням */
    public void getMonthlyBalanceByDays(final int month, final Callback<DaysBalance> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getMonthlyBalanceHistoryByDays(month, callback);
    }

    public UUID verifyTransfer(final long recipientId, final double amount, final String note) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putLong(Constants.RECIPIENT_ID, recipientId)
                .putDouble(Constants.AMOUNT, amount)
                .putString(Constants.NOTE, note)
                .build();
        final OneTimeWorkRequest verifyTransferRequest = new OneTimeWorkRequest.Builder(VerifyTransferWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(verifyTransferRequest);
        return verifyTransferRequest.getId();
    }

    public UUID transferFunds(final long recipientId,
                              final double amount,
                              final String note,
                              final String pin) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putLong(Constants.RECIPIENT_ID, recipientId)
                .putDouble(Constants.AMOUNT, amount)
                .putString(Constants.NOTE, note)
                .putString(Constants.TRANSACTION_PIN, pin)
                .build();
        final OneTimeWorkRequest transferFundsRequest = new OneTimeWorkRequest.Builder(TransferWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(transferFundsRequest);
        return transferFundsRequest.getId();
    }

    public void fetchWithdrawalTypes(final Callback<WithdrawalTypeModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getWithdrawalTypes(callback);
    }

    public UUID withdrawFunds(final String type,
                              final String method,
                              final double amount,
                              final String cardNumber,
                              final String recipientFullName,
                              final String phone,
                              final String countryTitle,
                              final String city) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putString(Constants.WITHDRAW_TYPE, type)
                .putString(Constants.WITHDRAW_METHOD, method)
                .putDouble(Constants.AMOUNT, amount)
                .putString(Constants.CARD_NUMBER, cardNumber)
                .putString(Constants.FULL_NAME, recipientFullName)
                .putString(Constants.PHONE, phone)
                .putString(Constants.COUNTRY_TITLE, countryTitle)
                .putString(Constants.CITY, city)
                .build();
        final OneTimeWorkRequest withdrawFundsWorker = new OneTimeWorkRequest.Builder(WithdrawFundsWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(withdrawFundsWorker);
        return withdrawFundsWorker.getId();
    }

    public UUID replenish(final String amount) {
        final Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build();
        final Data inputData = new Data.Builder()
                .putString(Constants.AMOUNT, amount)
                .build();
        final OneTimeWorkRequest replenishRequest = new OneTimeWorkRequest.Builder(ReplenishWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(context).enqueue(replenishRequest);
        return replenishRequest.getId();
    }

    public void fetchTransactionList(final Integer page,
                                     final Integer perPage,
                                     final Integer typeId,
                                     final int year,
                                     final int month,
                                     final int firstDay,
                                     final int lastDay,
                                     final Callback<TransactionModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getTransactionHistory(page, perPage, typeId <= 0 ? null : typeId,
                DateFormatter.dayMonthYearToStringDate(year, month, firstDay),
                DateFormatter.dayMonthYearToStringDate(year, month, lastDay),
                null, callback);
    }

    /* get transaction list from cache */
    public LiveData<List<TransactionModel.Transaction>> getMonthTransactionList(
            final boolean isIncrease,
            final int year,
            final int month) {
        return getTransactionList(isIncrease,
                year,
                month,
                1,
                DateFormatter.getLastDayOfMonthAndYear(year, month),
                0,
                null);
    }

    public LiveData<List<TransactionModel.Transaction>> getTransactionList(
            final boolean isIncrease,
            final int year,
            final int month,
            final int firstDay,
            final int lastDay,
            final int transactionType,
            final String searchQuery) {
        final long startTimestamp = DateFormatter.dayMonthYearToTimestamp(year, month, firstDay)/1000;
        final long endTimestamp = DateFormatter.dayMonthYearToTimestamp(year, month, lastDay)/1000;

        if (searchQuery == null || TextUtils.isEmpty(searchQuery)) {
            if (transactionType > 0) {
                return transactionDao.getTransactionListBySearchQuery(isIncrease, transactionType, startTimestamp, endTimestamp);
            }
            return transactionDao.getTransactionListBySearchQuery(isIncrease, startTimestamp, endTimestamp);
        }
        else {
            if (transactionType > 0) {
                return transactionDao.getTransactionListBySearchQuery(isIncrease, transactionType, searchQuery, startTimestamp, endTimestamp);
            }
            return transactionDao.getTransactionListBySearchQuery(isIncrease, searchQuery, startTimestamp, endTimestamp);
        }
    }

    public void insertTransactionList(List<TransactionModel.Transaction> fetchedTransactionList) {
        new Thread(() -> transactionDao.insertTransactionList(fetchedTransactionList)).start();
    }

    private static final String TAG = TransactionRepository.class.toString();
}
