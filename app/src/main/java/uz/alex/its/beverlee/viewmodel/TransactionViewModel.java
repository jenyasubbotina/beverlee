package uz.alex.its.beverlee.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.chart.LineChartStat;
import uz.alex.its.beverlee.model.transaction.TransactionModel;
import uz.alex.its.beverlee.model.transaction.TransactionParams;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel;
import uz.alex.its.beverlee.model.balance.Balance;
import uz.alex.its.beverlee.model.chart.PieChartStat;
import uz.alex.its.beverlee.model.balance.MonthBalance;
import uz.alex.its.beverlee.model.transaction.TransactionModel.Transaction;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel.WithdrawalType;
import uz.alex.its.beverlee.repository.TransactionRepository;
import uz.alex.its.beverlee.utils.DateFormatter;

public class TransactionViewModel extends ViewModel {
    private final TransactionRepository repository;

    private final MutableLiveData<Balance> currentBalance;

    private final MutableLiveData<Double> monthlyBalanceIncrease;
    private final MutableLiveData<Double> monthlyBalanceDecrease;
    private final MutableLiveData<Double> monthlyTurnover;

    private final MutableLiveData<Double> monthlyBonusAmount;
    private final MutableLiveData<Double> monthlyPurchaseAmount;
    private final MutableLiveData<Double> monthlyReceiptAmount;
    private final MutableLiveData<Double> monthlyTransferAmount;
    private final MutableLiveData<Double> monthlyReplenishAmount;
    private final MutableLiveData<Double> monthlyWithdrawalAmount;

    private final MutableLiveData<PieChartStat> incomeStatistics;
    private final MutableLiveData<PieChartStat> expenditureStatistics;
    private final MutableLiveData<LineChartStat> lineChartData;

    private final MutableLiveData<UUID> replenishResult;

    private final MutableLiveData<List<WithdrawalType>> withdrawalTypeList;
    private final MutableLiveData<UUID> withdrawalResult;

    private final MutableLiveData<UUID> verifyTransferResult;

    private final MutableLiveData<UUID> transferResult;

    private final TransactionParams transactionParams;
    private final MutableLiveData<TransactionParams> params;

    private final MutableLiveData<Boolean> isLoading;

    private int page;
    private int perPage;

    public TransactionViewModel(final Context context) {
        this.repository = new TransactionRepository(context);

        this.currentBalance = new MutableLiveData<>();

        this.monthlyBalanceIncrease = new MutableLiveData<>();
        this.monthlyBalanceDecrease = new MutableLiveData<>();
        this.monthlyTurnover = new MutableLiveData<>();

        this.monthlyPurchaseAmount = new MutableLiveData<>();
        this.monthlyBonusAmount = new MutableLiveData<>();
        this.monthlyReceiptAmount = new MutableLiveData<>();
        this.monthlyTransferAmount = new MutableLiveData<>();
        this.monthlyReplenishAmount = new MutableLiveData<>();
        this.monthlyWithdrawalAmount = new MutableLiveData<>();

        this.incomeStatistics = new MutableLiveData<>();
        this.expenditureStatistics = new MutableLiveData<>();
        this.lineChartData = new MutableLiveData<>();

        this.replenishResult = new MutableLiveData<>();

        this.withdrawalTypeList = new MutableLiveData<>();
        this.withdrawalResult = new MutableLiveData<>();

        this.verifyTransferResult = new MutableLiveData<>();

        this.transferResult = new MutableLiveData<>();

        this.page = 0;
        this.perPage = 15;
        this.isLoading = new MutableLiveData<>();
        this.isLoading.setValue(false);

        this.transactionParams = new TransactionParams();
        this.params = new MutableLiveData<>();
    }

    /* get balance */
    public void fetchCurrentBalance() {
        isLoading.setValue(true);
        repository.getCurrentBalance(new Callback<Balance>() {
            @Override
            public void onResponse(@NonNull Call<Balance> call, @NonNull Response<Balance> response) {
                isLoading.setValue(false);
                if (response.code() == 200 && response.isSuccessful()) {
                    currentBalance.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Balance> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
                isLoading.setValue(false);
            }
        });
    }

    public LiveData<Balance> getBalance() {
        return currentBalance;
    }

    public LiveData<Double> getMonthlyBalance() {
        return Transformations.switchMap(params, input -> {
            if (input.isIncrease()) {
                return monthlyBalanceIncrease;
            }
            return monthlyBalanceDecrease;
        });
    }

    public LiveData<Double> getMonthlyTurnover() {
        return monthlyTurnover;
    }

    /* replenish */
    public void replenish(final String amount) {
        replenishResult.setValue(repository.replenish(amount));
    }

    public LiveData<WorkInfo> getReplenishResult(final Context context) {
        return Transformations.switchMap(replenishResult, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    /* withdrawal */
    public void fetchWithdrawalTypes() {
        isLoading.setValue(true);
        repository.fetchWithdrawalTypes(new Callback<WithdrawalTypeModel>() {
            @Override
            public void onResponse(@NonNull Call<WithdrawalTypeModel> call, @NonNull Response<WithdrawalTypeModel> response) {
                isLoading.setValue(false);
                if (response.code() == 200 && response.isSuccessful()) {
                    final WithdrawalTypeModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.e(TAG, "onResponse(): response is NULL");
                        return;
                    }
                    withdrawalTypeList.setValue(customizableObject.getWithdrawalTypeList());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WithdrawalTypeModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
                isLoading.setValue(false);
            }
        });
    }

    public void withdrawFinds(final String type,
                              final String method,
                              final double amount,
                              final String cardNumber,
                              final String recipientFullName,
                              final String phone,
                              final String countryTitle,
                              final String city) {
        withdrawalResult.setValue(repository.withdrawFunds(type, method, amount, cardNumber, recipientFullName, phone, countryTitle, city));
    }

    public LiveData<List<WithdrawalType>> getWithdrawalTypeList() {
        return withdrawalTypeList;
    }

    public LiveData<WorkInfo> getWithdrawalResult(final Context context) {
        return Transformations.switchMap(withdrawalResult, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    /* transfer */
    public void transferFunds(final long recipientId, final double amount, final String note, final String pin) {
        transferResult.setValue(repository.transferFunds(recipientId, amount, note, pin));
    }

    public void verifyTransfer(final long recipientId, final double amount, final String note) {
        verifyTransferResult.setValue(repository.verifyTransfer(recipientId, amount, note));
    }

    public LiveData<WorkInfo> getVerifyTransferResult(final Context context) {
        return Transformations.switchMap(verifyTransferResult, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    public LiveData<WorkInfo> getTransferResult(final Context context) {
        return Transformations.switchMap(transferResult, input -> WorkManager.getInstance(context).getWorkInfoByIdLiveData(input));
    }

    /* transactions */
    public void fetchTransactionList() {
        isLoading.setValue(true);
        repository.fetchTransactionList(
                null,
                null,
                transactionParams.getTransactionTypeId(),
                transactionParams.getYear(),
                transactionParams.getMonth(),
                transactionParams.getFirstDay(),
                transactionParams.getLastDay(),
                new Callback<TransactionModel>() {
            @Override
            public void onResponse(@NonNull Call<TransactionModel> call, @NonNull Response<TransactionModel> response) {
                isLoading.setValue(false);
                if (response.code() == 200 && response.isSuccessful()) {
                    final TransactionModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.e(TAG, "onResponse(): response is NULL");
                        return;
                    }
                    final List<Transaction> fetchedTransactionList = customizableObject.getTransactionList();
                    repository.insertTransactionList(fetchedTransactionList);

                    double bonusAmount = 0;
                    double purchaseAmount = 0;
                    double receiptAmount = 0;
                    double transferAmount = 0;
                    double replenishAmount = 0;
                    double withdrawalAmount = 0;

                    bonusAmount = fetchedTransactionList.stream()
                            .filter(i -> i.getTypeId() == 1)
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    purchaseAmount = fetchedTransactionList.stream()
                            .filter(i -> i.getTypeId() == 2)
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    receiptAmount = fetchedTransactionList.stream()
                            .filter(i -> i.getTypeId() == 3)
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    transferAmount = fetchedTransactionList.stream()
                            .filter(i -> i.getTypeId() == 4)
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    replenishAmount = fetchedTransactionList.stream()
                            .filter(i -> i.getTypeId() == 5)
                            .mapToDouble(Transaction::getAmount)
                            .sum();
                    withdrawalAmount = fetchedTransactionList.stream()
                            .filter(i -> i.getTypeId() == 6)
                            .mapToDouble(Transaction::getAmount)
                            .sum();

                    final double income = bonusAmount + receiptAmount + replenishAmount;
                    final double expenditure = purchaseAmount + transferAmount + withdrawalAmount;
                    final double turnover = income + expenditure;

                    incomeStatistics.setValue(new PieChartStat(
                            (float) (bonusAmount/income),
                            (float) (receiptAmount/income),
                            (float) (replenishAmount/income)));
                    expenditureStatistics.setValue(new PieChartStat(
                            (float) (purchaseAmount/expenditure),
                            (float) (transferAmount/expenditure),
                            (float) (withdrawalAmount/expenditure)));
                    lineChartData.setValue(new LineChartStat(
                            (float) (bonusAmount/turnover),
                            (float) (purchaseAmount/turnover),
                            (float) (receiptAmount/turnover),
                            (float) (transferAmount/turnover),
                            (float) (replenishAmount/turnover),
                            (float) (withdrawalAmount/turnover)));

                    monthlyBonusAmount.setValue(bonusAmount);
                    monthlyPurchaseAmount.setValue(purchaseAmount);
                    monthlyReceiptAmount.setValue(receiptAmount);
                    monthlyTransferAmount.setValue(transferAmount);
                    monthlyReplenishAmount.setValue(replenishAmount);
                    monthlyWithdrawalAmount.setValue(withdrawalAmount);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionModel> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
                isLoading.setValue(false);
            }
        });
    }

    /* typeId and contactId can be null */
    public LiveData<List<Transaction>> getTransactionList() {
        return Transformations.switchMap(params, input -> repository.getTransactionList(
                input.isIncrease(),
                input.getYear(),
                input.getMonth(),
                input.getFirstDay(),
                input.getLastDay(),
                input.getTransactionTypeId(),
                input.getSearchQuery()));
    }

    public void nextMonth() {
        int currentYear = transactionParams.getYear();
        int currentMonth = transactionParams.getMonth();

        if (currentMonth >= LocalDateTime.now().getMonthValue()) {
            if (currentYear >= LocalDateTime.now().getYear()) {
                return;
            }
        }
        if (currentMonth >= 12) {
            currentYear++;
            currentMonth = 1;
        }
        else {
            currentMonth++;
        }
        transactionParams.setYear(currentYear);
        transactionParams.setMonth(currentMonth);
        transactionParams.setFirstDay(1);
        transactionParams.setLastDay(DateFormatter.getLastDayOfMonthAndYear(currentYear, currentMonth));
        transactionParams.setTransactionTypeId(0);
        transactionParams.setSearchQuery(null);
        params.setValue(transactionParams);
    }

    public void prevMonth() {
        int currentYear = transactionParams.getYear();
        int currentMonth = transactionParams.getMonth();

        if (currentMonth <= 1) {
            currentYear--;
            currentMonth = 12;
        }
        else {
            currentMonth--;
        }
        transactionParams.setYear(currentYear);
        transactionParams.setMonth(currentMonth);
        transactionParams.setFirstDay(1);
        transactionParams.setLastDay(DateFormatter.getLastDayOfMonthAndYear(currentYear, currentMonth));
        transactionParams.setTransactionTypeId(0);
        transactionParams.setSearchQuery(null);
        params.setValue(transactionParams);
    }

    public void setTransactionParams(
            final boolean isIncrease,
            final int year,
            final int month,
            final int firstDay,
            final int lastDay,
            final int transactionType,
            final String searchQuery) {
        this.transactionParams.setIncrease(isIncrease);
        this.transactionParams.setYear(year);
        this.transactionParams.setMonth(month);
        this.transactionParams.setFirstDay(firstDay);
        this.transactionParams.setLastDay(lastDay);
        this.transactionParams.setTransactionTypeId(transactionType);
        this.transactionParams.setSearchQuery(searchQuery);
        this.params.setValue(transactionParams);
    }

    public void setTransactionParams(final TransactionParams params) {
        this.transactionParams.setIncrease(params.isIncrease());
        this.transactionParams.setYear(params.getYear());
        this.transactionParams.setMonth(params.getMonth());
        this.transactionParams.setFirstDay(params.getFirstDay());
        this.transactionParams.setLastDay(params.getLastDay());
        this.transactionParams.setTransactionTypeId(params.getTransactionTypeId());
        this.transactionParams.setSearchQuery(params.getSearchQuery());
        this.params.setValue(params);
    }

    public void setSearchQuery(final String query) {
        this.transactionParams.setTransactionTypeId(0);
        this.transactionParams.setFirstDay(1);
        this.transactionParams.setLastDay(DateFormatter.getLastDayOfMonthAndYear(transactionParams.getYear(), transactionParams.getMonth()));
        this.transactionParams.setSearchQuery(query);
        this.params.setValue(transactionParams);
    }

    public void setIncrease(final boolean increase) {
        this.transactionParams.setIncrease(increase);
        this.params.setValue(transactionParams);
    }

    public void fetchMonthlyBalance() {
        isLoading.setValue(true);
        repository.getMonthlyBalance(transactionParams.getMonth(), new Callback<MonthBalance>() {
            @Override
            public void onResponse(@NonNull Call<MonthBalance> call, @NonNull Response<MonthBalance> response) {
                isLoading.setValue(false);
                if (response.code() == 200 && response.isSuccessful()) {
                    final MonthBalance monthBalance = response.body();

                    if (monthBalance == null) {
                        Log.e(TAG, "onResponse(): monthlyBalance=null");
                        return;
                    }
                    monthlyBalanceIncrease.setValue(monthBalance.getIncrease());
                    monthlyBalanceDecrease.setValue(monthBalance.getDecrease());

                    if (monthBalance.getIncrease() >= monthBalance.getDecrease()) {
                        monthlyTurnover.setValue(monthBalance.getIncrease());
                        return;
                    }
                    monthlyTurnover.setValue(-monthBalance.getDecrease());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MonthBalance> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    /* visual graphs */
    public LiveData<PieChartStat> getPieChartData() {
        return Transformations.switchMap(params, input -> {
           if (input.isIncrease()) {
               return incomeStatistics;
           }
           return expenditureStatistics;
        });
    }

    public LiveData<LineChartStat> getLineChartData() {
        return lineChartData;
    }

    public LiveData<Double> getMonthlyBonusOrPurchaseAmount() {
        return Transformations.switchMap(params, input -> {
            if (input.isIncrease()) {
                return monthlyBonusAmount;
            }
            return monthlyPurchaseAmount;
        });
    }

    public LiveData<Double> getMonthlyReceiptOrTransferAmount() {
        return Transformations.switchMap(params, input -> {
            if (input.isIncrease()) {
                return monthlyReceiptAmount;
            }
            return monthlyTransferAmount;
        });
    }

    public LiveData<Double> getMonthlyReplenishOrWithdrawalAmount() {
        return Transformations.switchMap(params, input -> {
            if (input.isIncrease()) {
                return monthlyReplenishAmount;
            }
            return monthlyWithdrawalAmount;
        });
    }

    public int getMonthName(final int monthNumber) {
        int resource = -1;

        switch (monthNumber) {
            case 1:
                resource = R.string.january;
                break;
            case 2:
                resource = R.string.february;
                break;
            case 3:
                resource = R.string.march;
                break;
            case 4:
                resource = R.string.april;
                break;
            case 5:
                resource = R.string.may;
                break;
            case 6:
                resource = R.string.june;
                break;
            case 7:
                resource = R.string.july;
                break;
            case 8:
                resource = R.string.august;
                break;
            case 9:
                resource = R.string.september;
                break;
            case 10:
                resource = R.string.october;
                break;
            case 11:
                resource = R.string.november;
                break;
            case 12:
                resource = R.string.december;
                break;
        }
        return resource;
    }

    public LiveData<TransactionParams> getTransactionParams() {
        return params;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    private final String TAG = TransactionViewModel.class.toString();
}
