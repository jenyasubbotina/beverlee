package uz.alex.its.beverlee.viewmodel_factory;

import android.content.Context;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import uz.alex.its.beverlee.viewmodel.ContactsViewModel;

public class ContactsViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public ContactsViewModelFactory(final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ContactsViewModel.class)) {
            return (T) new ContactsViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
