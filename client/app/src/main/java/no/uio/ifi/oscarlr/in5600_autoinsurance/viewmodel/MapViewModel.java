package no.uio.ifi.oscarlr.in5600_autoinsurance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private MutableLiveData<String> claimId = new MutableLiveData<>();
    private boolean onlyFirstTime; // Don't allow camera zoom to marker after first time clicking on claim map button

    public void setClaimId(String claimId) {
        onlyFirstTime = true;
        this.claimId.setValue(claimId);
    }

    public LiveData<String> getClaimId() {
        if (onlyFirstTime) {
            onlyFirstTime = false;
        } else {
            this.claimId.setValue("-1");
        }
        return this.claimId;
    }
}
