package no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ClaimDetailsViewModel extends ViewModel {

    private MutableLiveData<Object> obj = new MutableLiveData<>();

    public LiveData<Object> getObject() {
        return obj;
    }

    public void setObject(Object o) {
        obj.setValue(o);
    }
}
