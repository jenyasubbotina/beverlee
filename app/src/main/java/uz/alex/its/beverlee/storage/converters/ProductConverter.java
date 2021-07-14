package uz.alex.its.beverlee.storage.converters;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import uz.alex.its.beverlee.model.transaction.PurchaseModel;

public class ProductConverter {
    @TypeConverter
    public static PurchaseModel.Product stringToProduct(final String jsonValue) {
        return new Gson().fromJson(jsonValue, PurchaseModel.Product.class);
    }

    @TypeConverter
    public static String productToString(final PurchaseModel.Product product) {
        return new Gson().toJson(product);
    }
}
