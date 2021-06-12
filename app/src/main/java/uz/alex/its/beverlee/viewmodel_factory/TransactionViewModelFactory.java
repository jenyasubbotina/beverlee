package uz.alex.its.beverlee.viewmodel_factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uz.alex.its.beverlee.viewmodel.ContactsViewModel;
import uz.alex.its.beverlee.viewmodel.TransactionViewModel;

public class TransactionViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public TransactionViewModelFactory(final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionViewModel.class)) {
            return (T) new TransactionViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}