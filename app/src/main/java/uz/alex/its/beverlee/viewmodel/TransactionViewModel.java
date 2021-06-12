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

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.chart.LineChartStat;
import uz.alex.its.beverlee.model.transaction.TransactionModel;
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

    private final MutableLiveData<Integer> currentMonthNumber;

    private final MutableLiveData<Balance> currentBalance;

    private final MutableLiveData<Boolean> isIncrease;
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

    private final MutableLiveData<List<Transaction>> incomeTransactionList;
    private final MutableLiveData<List<Transaction>> expenditureTransactionList;

    private final MutableLiveData<List<WithdrawalType>> withdrawalTypeList;
    private final MutableLiveData<UUID> withdrawalResult;

    private final MutableLiveData<UUID> verifyTransferResult;

    private final MutableLiveData<UUID> transferResult;

    private int monthNumber;
    private int page;
    private int perPage;
    private boolean isLoading;

    public TransactionViewModel(final Context context) {
        this.repository = new TransactionRepository(context);

        this.currentBalance = new MutableLiveData<>();

        this.isIncrease = new MutableLiveData<>();
        this.isIncrease.setValue(true);

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

        this.currentMonthNumber = new MutableLiveData<>();
        this.monthNumber = Calendar.getInstance().get(Calendar.MONTH);
        this.currentMonthNumber.setValue(monthNumber);

        this.incomeTransactionList = new MutableLiveData<>();
        this.expenditureTransactionList = new MutableLiveData<>();

        this.withdrawalTypeList = new MutableLiveData<>();
        this.withdrawalResult = new MutableLiveData<>();

        this.verifyTransferResult = new MutableLiveData<>();

        this.transferResult = new MutableLiveData<>();

        this.page = 0;
        this.perPage = 15;
        this.isLoading = false;
    }

    /* get balance */
    public void fetchCurrentBalance() {
        repository.getCurrentBalance(new Callback<Balance>() {
            @Override
            public void onResponse(@NonNull Call<Balance> call, @NonNull Response<Balance> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    currentBalance.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Balance> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public LiveData<Balance> getBalance() {
        return currentBalance;
    }

    public LiveData<Double> getMonthlyBalance() {
        return Transformations.switchMap(isIncrease, input -> {
            if (input) {
                return monthlyBalanceIncrease;
            }
            return monthlyBalanceDecrease;
        });
    }

    public LiveData<Double> getMonthlyTurnover() {
        return monthlyTurnover;
    }


    /* withdrawal */
    public void fetchWithdrawalTypes() {
        repository.fetchWithdrawalTypes(new Callback<WithdrawalTypeModel>() {
            @Override
            public void onResponse(@NonNull Call<WithdrawalTypeModel> call, @NonNull Response<WithdrawalTypeModel> response) {
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
    public void fetchTransactionList(final Integer typeId,
                                     final String dateStart,
                                     final String dateFinish,
                                     final Long contactId) {
        repository.fetchTransactionList(null, null, typeId, dateStart, dateFinish, contactId, new Callback<TransactionModel>() {
            @Override
            public void onResponse(@NonNull Call<TransactionModel> call, @NonNull Response<TransactionModel> response) {
                if (response.code() == 200 && response.isSuccessful()) {
                    final TransactionModel customizableObject = response.body();

                    if (customizableObject == null) {
                        Log.e(TAG, "onResponse(): response is NULL");
                        return;
                    }
                    final List<Transaction> fetchedTransactionList = customizableObject.getTransactionList();
                    incomeTransactionList.setValue(fetchedTransactionList.stream().filter(Transaction::isBalanceIncrease).collect(Collectors.toList()));
                    expenditureTransactionList.setValue(fetchedTransactionList.stream().filter(i -> !i.isBalanceIncrease()).collect(Collectors.toList()));

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
            }
        });
    }

    public void fetchMonthlyTransactionList(final Integer typeId,
                                            final int month,
                                            final Long contactId) {
        fetchTransactionList(typeId,
                DateFormatter.dateToYearMonthDay(DateFormatter.getFirstDayOfMonth(month)),
                DateFormatter.dateToYearMonthDay(DateFormatter.getLastDayOfMonth(month)),
                contactId);
    }

    public LiveData<List<Transaction>> getTransactionList() {
        return Transformations.switchMap(isIncrease, input -> {
            if (input) {
                return incomeTransactionList;
            }
            return expenditureTransactionList;
        });
    }

    public void setIncrease(final boolean increase) {
        this.isIncrease.setValue(increase);
    }

    public void incrementCurrentMonth() {
        if (monthNumber >= 11) {
            return;
        }
        currentMonthNumber.setValue(++monthNumber);

        fetchMonthlyTransactionList( null, monthNumber, null);
        fetchMonthlyBalance(monthNumber + 1);
    }

    public void decrementCurrentMonth() {
        if (monthNumber <= 0) {
            return;
        }
        currentMonthNumber.setValue(--monthNumber);

        fetchMonthlyTransactionList(null, monthNumber, null);
        fetchMonthlyBalance(monthNumber + 1);
    }

    public void fetchMonthlyBalance(final int month) {
        repository.getMonthlyBalance(month, new Callback<MonthBalance>() {
            @Override
            public void onResponse(@NonNull Call<MonthBalance> call, @NonNull Response<MonthBalance> response) {
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
                Log.e(TAG, "onFailure(): ", t);
            }
        });
    }

    public LiveData<Integer> getCurrentMonthNumber() {
        return currentMonthNumber;
    }

    public LiveData<PieChartStat> getPieChartData() {
        return Transformations.switchMap(isIncrease, input -> {
           if (input) {
               return incomeStatistics;
           }
           return expenditureStatistics;
        });
    }

    public LiveData<LineChartStat> getLineChartData() {
        return lineChartData;
    }

    public LiveData<Double> getMonthlyBonusOrPurchaseAmount() {
        return Transformations.switchMap(isIncrease, input -> {
            if (input) {
                return monthlyBonusAmount;
            }
            return monthlyPurchaseAmount;
        });
    }

    public LiveData<Double> getMonthlyReceiptOrTransferAmount() {
        return Transformations.switchMap(isIncrease, input -> {
            if (input) {
                return monthlyReceiptAmount;
            }
            return monthlyTransferAmount;
        });
    }

    public LiveData<Double> getMonthlyReplenishOrWithdrawalAmount() {
        return Transformations.switchMap(isIncrease, input -> {
            if (input) {
                return monthlyReplenishAmount;
            }
            return monthlyWithdrawalAmount;
        });
    }

    private final String TAG = TransactionViewModel.class.toString();

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
}
