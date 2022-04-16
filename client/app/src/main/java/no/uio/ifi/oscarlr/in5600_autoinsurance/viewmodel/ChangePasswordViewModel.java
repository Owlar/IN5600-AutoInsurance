package no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangePasswordViewModel extends ViewModel {

    private MutableLiveData<String> newPassword = new MutableLiveData<>();
    private MutableLiveData<String> confirmNewPassword = new MutableLiveData<>();

    public LiveData<String> getNewPassword() {
        return newPassword;
    }

    public LiveData<String> getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setNewPassword(String s) {
        newPassword.setValue(s);
    }

    public void setConfirmNewPassword(String s) {
        confirmNewPassword.setValue(s);
    }

}
