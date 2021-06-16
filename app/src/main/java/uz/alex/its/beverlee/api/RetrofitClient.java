package uz.alex.its.beverlee.api;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import uz.alex.its.beverlee.R;
import uz.alex.its.beverlee.model.requestParams.AvatarParams;
import uz.alex.its.beverlee.model.requestParams.ChangePasswordParams;
import uz.alex.its.beverlee.model.requestParams.ChangePinParams;
import uz.alex.its.beverlee.model.requestParams.MakePurchaseParams;
import uz.alex.its.beverlee.model.requestParams.NotificationSettingsParams;
import uz.alex.its.beverlee.model.requestParams.UserDataParams;
import uz.alex.its.beverlee.model.actor.ContactModel;
import uz.alex.its.beverlee.model.CountryModel;
import uz.alex.its.beverlee.model.LoginModel;
import uz.alex.its.beverlee.model.requestParams.AuthParams;
import uz.alex.its.beverlee.model.requestParams.PinParams;
import uz.alex.its.beverlee.model.requestParams.RegisterParams;
import uz.alex.its.beverlee.model.requestParams.TransferFundsParams;
import uz.alex.its.beverlee.model.requestParams.VerifyCodeParams;
import uz.alex.its.beverlee.model.requestParams.VerifyTransferParams;
import uz.alex.its.beverlee.model.requestParams.WithdrawalParams;
import uz.alex.its.beverlee.model.news.NewsDataModel;
import uz.alex.its.beverlee.model.news.NewsModel;
import uz.alex.its.beverlee.model.notification.NotificationSettingsModel;
import uz.alex.its.beverlee.model.transaction.PurchaseModel;
import uz.alex.its.beverlee.model.transaction.TransactionModel;
import uz.alex.its.beverlee.model.actor.UserModel;
import uz.alex.its.beverlee.model.transaction.WithdrawalTypeModel;
import uz.alex.its.beverlee.model.balance.Balance;
import uz.alex.its.beverlee.model.balance.DaysBalance;
import uz.alex.its.beverlee.model.balance.MonthBalance;
import uz.alex.its.beverlee.storage.SharedPrefs;
import uz.alex.its.beverlee.utils.Constants;

public class RetrofitClient {
    private final Retrofit.Builder retrofitBuilder;
    private final OkHttpClient.Builder okHttpBuilder;
    private ApiService apiService;

    private static RetrofitClient INSTANCE;

    private RetrofitClient(@NonNull final Context context) {
        this.okHttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(60L, TimeUnit.SECONDS)
                .writeTimeout(60L, TimeUnit.SECONDS)
                .callTimeout(60L, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        this.retrofitBuilder = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.dev_server_url))
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                                .setLenient()
                                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context1) -> new Date(json.getAsJsonPrimitive().getAsLong()))
                                .create()));
    }

    public void setAuthorizationHeader(final Context context) {
        final String bearerToken = SharedPrefs.getInstance(context).getString(Constants.BEARER_TOKEN);
        if (!TextUtils.isEmpty(bearerToken)) {
            final AuthInterceptor interceptor = new AuthInterceptor(bearerToken);
            okHttpBuilder.addInterceptor(interceptor);
        }
        okHttpBuilder.addInterceptor(new ContentTypeInterceptor());
        okHttpBuilder.addInterceptor(new AcceptInterceptor());
        apiService = retrofitBuilder.client(okHttpBuilder.build()).build().create(ApiService.class);
    }

    public static RetrofitClient getInstance(@NonNull final Context context) {
        if (INSTANCE == null) {
            synchronized (RetrofitClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitClient(context);
                }
            }
        }
        return INSTANCE;
    }

    /* Authentication */
    public Response<LoginModel> register(final RegisterParams registerParams) throws IOException {
        return apiService.register(registerParams).execute();
    }

    public Response<LoginModel> login(final AuthParams authParams) throws IOException {
        return apiService.login(authParams).execute();
    }

    public Response<Void> verifySms() throws IOException {
        return apiService.verifySms().execute();
    }

    public Response<Void> verifyCall() throws IOException {
        return apiService.verifyCall().execute();
    }

    public Response<Void> submitVerification(final VerifyCodeParams verifyCodeParams) throws IOException {
        return apiService.submitVerification(verifyCodeParams).execute();
    }

    public Response<Void> checkVerified() throws IOException {
        return apiService.checkVerified().execute();
    }

    /* Pin-code */
    public Response<Void> checkPinAssigned() throws IOException {
        return apiService.checkPinAssigned().execute();
    }

    public Response<Void> assignPin(final PinParams pinParams) throws IOException {
        return apiService.assignPin(pinParams).execute();
    }

    public Response<Void> verifyPin(final String pinCode) throws IOException {
        return apiService.verifyPin(pinCode).execute();
    }

    public Response<Void> requestPinBySms() throws IOException {
        return apiService.requestPinBySms().execute();
    }

    public Response<Void> requestPinByCall() throws IOException {
        return apiService.requestPinByPhone().execute();
    }

    public Response<Void> changePin(final ChangePinParams params) throws IOException {
        return apiService.changePin(params).execute();
    }

    /* User */
    public void getUserData(final Callback<UserModel> callback) {
        apiService.getUserData().enqueue(callback);
    }

    public Response<UserModel> saveUserData(final UserDataParams params) throws IOException {
        return apiService.saveUserData(params).execute();
    }

    public Response<Void> uploadAvatar(final AvatarParams avatarParams) throws IOException {
        return apiService.uploadAvatar(avatarParams.getAvatar()).execute();
    }

    public void uploadAvatarAsync(final AvatarParams params, final Callback<Void> callback) {
        apiService.uploadAvatar(params.getAvatar()).enqueue(callback);
    }

    public Response<Void> deleteAvatar() throws IOException {
        return apiService.deleteAvatar().execute();
    }

    public Response<Void> changePassword(ChangePasswordParams params) throws IOException {
        return apiService.changePassword(params).execute();
    }

    /* Countries */
    public void getCountryList(final Callback<CountryModel> callback) {
        apiService.getCountryList().enqueue(callback);
    }

    /* Transactions */
    /**
     * Registers the text to display in a tool tip.   The text
     * displays when the cursor lingers over the component.
     *
     * @param page          Номер страницы
     * @param perPage      Кол-во записей
     * @param typeId       Тип. 1 - Начисление бонуса,
     *                      2 - Покупка в магазине,
     *                      3 - Перевод - получение,
     *                      4 - Перевод - отправка,
     *                      5 - Платежные системы - пополнение,
     *                      6 - Платежные системы - вывод
     * @param dateStart    Дата от, формат 2020-01-01
     * @param dateFinished Дата до, формат 2020-01-31
     * @param contactId    Идентификатор получателя или отправителя перевода
     */
    public void getTransactionHistory(final Integer page,
                                      final Integer perPage,
                                      final Integer typeId,
                                      final String dateStart,
                                      final String dateFinished,
                                      final Long contactId,
                                      final Callback<TransactionModel> callback) {
        apiService.getTransactionHistory(page, perPage, typeId, dateStart, dateFinished, contactId).enqueue(callback);
    }

    /* Текущий баланс в уе */
    public void getCurrentBalance(final Callback<Balance> callback) {
        apiService.getCurrentBalance().enqueue(callback);
    }

    /* Поступления/списания за месяц */
    public void getMonthlyBalanceHistory(final int month, final Callback<MonthBalance> callback) {
        apiService.getMonthlyBalanceHistory(month).enqueue(callback);
    }

    /* Поступления/списания за месяц по дням */
    public void getMonthlyBalanceHistoryByDays(final int month, final Callback<DaysBalance> callback) {
        apiService.getMonthlyBalanceHistoryByDays(month).enqueue(callback);
    }

    public void getWithdrawalTypes(final Callback<WithdrawalTypeModel> callback) {
        apiService.getWithdrawalTypes().enqueue(callback);
    }

    public Response<Void> withdrawFunds(final WithdrawalParams withdrawalParams) throws IOException {
        return apiService.withdrawFunds(withdrawalParams).execute();
    }

    public Response<Balance> transferFunds(final TransferFundsParams transferFundsParams) throws IOException {
        return apiService.transferFunds(transferFundsParams).execute();
    }

    public Response<Void> verifyTransfer(final VerifyTransferParams verifyTransferParams) throws IOException {
        return apiService.verifyTransfer(verifyTransferParams).execute();
    }

    /* News */
    public void getNews(final Integer page, final Integer perPage, final Callback<NewsModel> callback) {
        apiService.getNews(page, perPage).enqueue(callback);
    }

    public void getNewsData(final long newsId, final Callback<NewsDataModel> callback) {
        apiService.getNewsData(newsId).enqueue(callback);
    }

    /* Notifications */
    public void getNotificationSettings(final Callback<NotificationSettingsModel> callback) {
        apiService.getNotificationSettings().enqueue(callback);
    }

    public Response<Void> saveNotificationSettings(final NotificationSettingsParams params) throws IOException {
        return apiService.saveNotificationSettings(params).execute();
    }

    /* Contacts */
    public void getContactList(final String searchQuery, final Integer page, final Integer perPage, final Callback<ContactModel> callback) {
        apiService.getContactList(searchQuery, page, perPage).enqueue(callback);
    }

    public void getContactData(final long contactId, final Callback<ContactModel> callback) {
        apiService.getContactData(contactId).enqueue(callback);
    }

    public Response<Void> deleteContact(final long contactId) throws IOException {
        return apiService.deleteContact(contactId).execute();
    }

    /* Purchase */
    public void getUserPurchases(final Callback<PurchaseModel> callback) {
        apiService.getUserPurchases().enqueue(callback);
    }

    public Response<PurchaseModel.PurchaseResponse> makePurchase(final long requestId, final MakePurchaseParams makePurchaseParams) throws IOException {
        return apiService.makePurchase(requestId, makePurchaseParams).execute();
    }

    public Response<Void> deletePurchase(final long requestId) throws IOException {
        return apiService.deletePurchase(requestId).execute();
    }

    private static final String TAG = RetrofitClient.class.toString();
}