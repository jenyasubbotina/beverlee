package uz.alex.its.beverlee.viewmodel_factory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import uz.alex.its.beverlee.viewmodel.UserViewModel;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public UserViewModelFactory(final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
