package uz.alex.its.beverlee.viewmodel_factory;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import uz.alex.its.beverlee.viewmodel.PinViewModel;

public class PinViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public PinViewModelFactory(final Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PinViewModel.class)) {
            return (T) new PinViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
