package no.uio.ifi.oscarlr.in5600_autoinsurance.new_claim;

import com.google.android.gms.maps.model.LatLng;

public class NewClaimSingleton {
    private static NewClaimSingleton instance;
    private String claimDes = "";
    private String claimPhoto = "";
    private String claimPosition;
    private String numberOfClaims = "0";

    private NewClaimSingleton () {

    }

    public static synchronized NewClaimSingleton getInstance() {
        if (instance == null) {
            instance = new NewClaimSingleton();
        }
        return instance;
    }

    public void setClaimDes(String des) {
        claimDes = des;
    }

    public String getClaimDes() {
        return claimDes;
    }

    public void setClaimPosition(String loc) {
        this.claimPosition = loc;
    }

    public String getClaimPosition() {
        return claimPosition;
    }

    public String getNumberOfClaims() {
        return numberOfClaims;
    }

    public void setNumberOfClaims(int numberOfClaims) {
        this.numberOfClaims = String.valueOf(numberOfClaims);
    }

    public void setClaimPhoto(String claimPhoto) {
        this.claimPhoto = claimPhoto;
    }

    public String getClaimPhoto() {
        return claimPhoto;
    }
}
