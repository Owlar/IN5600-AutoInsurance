package no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private MutableLiveData<String> claimId = new MutableLiveData<>();

    public void setClaimId(String claimId) {
        this.claimId.setValue(claimId);
    }

    public LiveData<String> getClaimId() {
        return this.claimId;
    }
}
