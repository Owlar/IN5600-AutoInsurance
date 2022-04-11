package no.uio.ifi.oscarlr.in5600_autoinsurance.model;

import android.graphics.Bitmap;

public class Claim {
    public String id;
    public String numberOfClaims;
    public String claimId;
    public String claimDes;
    public String claimPhotoBase64;
    public Bitmap claimPhoto;
    public String claimLocation;
    public String claimStatus;

    public Claim() {

    }

    public Claim(String id, String numberOfClaims, String claimId, String claimDes, String claimPhotoBase64, Bitmap claimPhoto, String claimLocation, String claimStatus) {
        this.id = id;
        this.numberOfClaims = numberOfClaims;
        this.claimId = claimId;
        this.claimDes = claimDes;
        this.claimPhoto = claimPhoto;
        this.claimLocation = claimLocation;
        this.claimStatus = claimStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumberOfClaims() {
        return numberOfClaims;
    }

    public void setNumberOfClaims(String numberOfClaims) {
        this.numberOfClaims = numberOfClaims;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getClaimDes() {
        return claimDes;
    }

    public void setClaimDes(String claimDes) {
        this.claimDes = claimDes;
    }

    public void setClaimPhotoBase64(String claimPhotoBase64) {
        this.claimPhotoBase64 = claimPhotoBase64;
    }

    public String getClaimPhotoBase64() {
        return claimPhotoBase64;
    }

    public Bitmap getClaimPhoto() {
        return claimPhoto;
    }

    public void setClaimPhoto(Bitmap claimPhoto) {
        this.claimPhoto = claimPhoto;
    }

    public String getClaimLocation() {
        return claimLocation;
    }

    public void setClaimLocation(String claimLocation) {
        this.claimLocation = claimLocation;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    @Override
    public String toString() {
        return claimId + ": " + claimDes;
    }
}
