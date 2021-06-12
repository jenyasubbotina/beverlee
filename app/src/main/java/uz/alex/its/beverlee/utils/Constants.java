package uz.alex.its.beverlee.utils;

public class Constants {
    /* application constants */
    public static final String MD5 = "MD5";
    public static final String BEARER_TOKEN = "bearer_token";
    public static final String FCM_TOKEN = "fcm_token";

    /* user constants */
    public static final String USER_ID = "user_id";
    public static final String CLUB_NUMBER = "club_number";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String MIDDLE_NAME = "middle_name";
    public static final String FULL_NAME = "full_name";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String CITY = "city";
    public static final String ADDRESS = "address";
    public static final String POSITION = "position";
    public static final String COUNTRY_ID = "country_id";
    public static final String COUNTRY_TITLE = "country_title";
    public static final String COUNTRY_CODE = "country_code";
    public static final String PHOTO_URL = "photo_url";
    public static final String CONTACT_ID = "contact_id";
    public static final String CONTACT_FULL_NAME = "contact_full_name";

    /* authorization constants */
    public static final String PASSWORD = "password";
    public static final String PASSWORD_CONFIRMATION = "password_confirmation";
    public static final String NEW_PASSWORD = "new_password";
    public static final String NEW_PASSWORD_CONFIRMATION = "new_password_confirmation";
    public static final String PINCODE = "pin";
    public static final String CODE = "code";

    /* transaction constants */
    public static final String CARD_NUMBER = "card_number";
    public static final String AMOUNT = "amount";
    public static final String WITHDRAW_METHOD = "withdraw_method";
    public static final String WITHDRAW_TYPE = "withdraw_type";
    public static final String RECIPIENT_ID = "recipient_id";
    public static final String NOTE = "note";
    public static final String TRANSACTION_PIN = "transaction_pin";

    /* auth & registration checkers */
    public static final String PHONE_VERIFIED = "phone_verified";
    public static final String PIN_ASSIGNED = "pin_assigned";
    public static final String FINGERPRINT_ON = "fingerprint_on";

    /* request codes */
    public static final int REQUEST_CODE_PICK_IMAGE = 0x101;
    public static final int REQUEST_CODE_READ_CONTACTS = 0x102;
    public static final int REQUEST_CODE_PROVIDE_AUTHENTICATOR = 0x103;
    public static final int REQUEST_CODE_VERIFY_TRANSFER = 104;

    /* local storage */
    public static final String DATABASE_NAME = "beverlee_local_database.db";
    public static final String SHARED_PREFS_NAME = "beverlee_shared_prefs";

    /* push */
    public static final String PACKAGE_NAME = "package_name";
    public static final String PUSH_INTENT = "push_intent";
    public static final String PUSH = "push";
    public static final String PUSH_ID = "id";
    public static final String PUSH_TITLE = "title";
    public static final String PUSH_BODY = "body";

    /* push status */
    public static final String PUSH_STATUS = "push_status";
    public static final int NOT_DELIVERED = 0;
    public static final int DELIVERED = 1;
    public static final int READ = 2;

    /* notifications */
    public static final String PUSH_CHANNEL_ID = "channel_id";
    public static final String DEFAULT_CHANNEL_ID = "101";
    public static final String NEWS_CHANNEL_ID = "102";
    public static final String BONUS_CHANNEL_ID = "103";
    public static final String INCOME_CHANNEL_ID = "104";
    public static final String PURCHASE_CHANNEL_ID = "105";
    public static final String REPLENISH_CHANNEL_ID = "106";
    public static final String WITHDRAWAL_CHANNEL_ID = "107";

    public static final String DEFAULT_CHANNEL_NAME = "default channel";
    public static final String NEWS_CHANNEL_NAME = "news_channel";
    public static final String BONUS_CHANNEL_NAME = "bonus_channel";
    public static final String INCOME_CHANNEL_NAME = "income_channel";
    public static final String PURCHASE_CHANNEL_NAME = "purchase_channel";
    public static final String REPLENISH_CHANNEL_NAME = "replenish_channel";
    public static final String WITHDRAWAL_CHANNEL_NAME = "withdrawal_channel";

    public static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String NOTIFY_NEWS = "notify_news";
    public static final String NOTIFY_BONUSES = "notify_bonuses";
    public static final String NOTIFY_INCOME = "notify_income";
    public static final String NOTIFY_PURCHASE = "notify_purchase";
    public static final String NOTIFY_REPLENISH = "notify_replenish";
    public static final String NOTIFY_WITHDRAWAL = "notify_withdrawal";

    /* errors */
    public static final String REQUEST_ERROR = "request_error";
    public static final String UNKNOWN_ERROR = "Unknown error";
    public static final String NO_PIN = "no_pin";
    public static final String CURRENT_BALANCE = "current_balance";
    public static final String VERIFY_TRANSFER_PARAMS = "verify_transfer_params";


    public static final String RESULT_TYPE_TRANSFER = "transfer";
    public static final String RESULT_TYPE_REPLENISH = "replenish";
    public static final String RESULT_TYPE_WITHDRAWAL = "withdrawal";
    public static final String RESULT_TYPE_PROFILE = "profile";
    public static final String RESULT_TYPE_CONTACTS = "contacts";
    public static final String NO_INTERNET = "no_internet";
    public static final String ID = "id";
}
