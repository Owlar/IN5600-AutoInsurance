package no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public void setEmail(String e) {
        email.setValue(e);
    }

    public void setPassword(String p) {
        password.setValue(p);
    }

}
