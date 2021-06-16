package uz.alex.its.beverlee.viewmodel.factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uz.alex.its.beverlee.viewmodel.NewsViewModel;
import uz.alex.its.beverlee.viewmodel.PurchaseViewModel;

public class PurchaseViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public PurchaseViewModelFactory(final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PurchaseViewModel.class)) {
            return (T) new PurchaseViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
