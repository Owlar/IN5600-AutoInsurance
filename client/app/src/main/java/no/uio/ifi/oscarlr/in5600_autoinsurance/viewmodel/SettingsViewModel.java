package no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<Boolean> checkedAppTheme = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkedOfflineMode = new MutableLiveData<>();

    public LiveData<Boolean> getCheckedAppTheme() {
        return checkedAppTheme;
    }

    public void setCheckedAppTheme(Boolean checked) {
        this.checkedAppTheme.setValue(checked);
    }

    public LiveData<Boolean> getCheckedOfflineMode() {
        return checkedOfflineMode;
    }

    public void setCheckedOfflineMode(Boolean checked) {
        this.checkedOfflineMode.setValue(checked);
    }
}
