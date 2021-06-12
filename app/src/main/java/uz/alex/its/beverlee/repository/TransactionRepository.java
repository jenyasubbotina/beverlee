package uz.alex.its.beverlee.repository;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.UUID;

import retrofit2.Callback;
import uz.alex.its.beverlee.api.RetrofitClient;
import uz.alex.its.beverlee.model.transaction.TransactionModel;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel;
import uz.alex.its.beverlee.model.balance.Balance;
import uz.alex.its.beverlee.model.balance.DaysBalance;
import uz.alex.its.beverlee.model.balance.MonthBalance;
import uz.alex.its.beverlee.utils.Constants;
import uz.alex.its.beverlee.worker.TransferFundsWorker;
import uz.alex.its.beverlee.worker.VerifyTransferWorker;
import uz.alex.its.beverlee.worker.WithdrawFundsWorker;

public class TransactionRepository {
    private final Context context;

    public TransactionRepository(final Context context) {
        this.context = context;
    }

    /* Текущий баланс в уе */
    public void getCurrentBalance(final Callback<Balance> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getCurrentBalance(callback);
    }

    /* Поступления/списания за месяц */
    public void getMonthlyBalance(final int month, final Callback<MonthBalance> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getMonthlyBalanceHistory(month, callback);
    }

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
        final OneTimeWorkRequest transferFundsRequest = new OneTimeWorkRequest.Builder(TransferFundsWorker.class)
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

    public void fetchTransactionList(final Integer page,
                                     final Integer perPage,
                                     final Integer typeId,
                                     final String dateStart,
                                     final String dateFinish,
                                     final Long contactId,
                                     final Callback<TransactionModel> callback) {
        RetrofitClient.getInstance(context).setAuthorizationHeader(context);
        RetrofitClient.getInstance(context).getTransactionHistory(page, perPage, typeId, dateStart, dateFinish, contactId, callback);
    }

    private static final String TAG = TransactionRepository.class.toString();
}
