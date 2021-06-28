package uz.alex.its.beverlee.storage.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import uz.alex.its.beverlee.model.actor.TransactionParticipant;

public class TransactionParticipantConverter {
    @TypeConverter
    public static TransactionParticipant stringToTransactionParticipant(final String jsonValue) {
        return new Gson().fromJson(jsonValue, TransactionParticipant.class);
    }

    @TypeConverter
    public static String transactionParticipantToString(final TransactionParticipant participant) {
        return new Gson().toJson(participant);
    }
}
