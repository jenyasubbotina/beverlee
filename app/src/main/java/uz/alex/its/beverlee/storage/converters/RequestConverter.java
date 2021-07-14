package uz.alex.its.beverlee.storage.converters;
import androidx.room.TypeConverter;
import com.google.gson.Gson;
import uz.alex.its.beverlee.model.transaction.Withdrawal;

public class RequestConverter {
    @TypeConverter
    public static Withdrawal.Request stringToRequest(final String jsonValue) {
        return new Gson().fromJson(jsonValue, Withdrawal.Request.class);
    }

    @TypeConverter
    public static String requestToString(final Withdrawal.Request request) {
        return new Gson().toJson(request);
    }
}
