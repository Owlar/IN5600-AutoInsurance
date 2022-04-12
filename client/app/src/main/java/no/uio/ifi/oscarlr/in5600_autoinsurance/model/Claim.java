package no.uio.ifi.oscarlr.in5600_autoinsurance.model;

import android.graphics.Bitmap;

public class Claim {
    //TODO public or private?
    public String id;
    public String numberOfClaims;
    public String claimId;
    public String claimDes;
    public String claimPhotoBase64;
    public String claimPhotoFilepath;
    public String claimLocation;
    public String claimStatus;
    public transient Bitmap claimPhotoBitmap; // transient makes gson ignore this in DataProcessor

    public Claim() {

    }

    public Claim(String id, String numberOfClaims, String claimId, String claimDes, String claimPhotoBase64, String claimPhotoFilepath, String claimLocation, String claimStatus, Bitmap claimPhotoBitmap) {
        this.id = id;
        this.numberOfClaims = numberOfClaims;
        this.claimId = claimId;
        this.claimDes = claimDes;
        this.claimLocation = claimLocation;
        this.claimStatus = claimStatus;
        this.claimPhotoBase64 = claimPhotoBase64;
        this.claimPhotoFilepath = claimPhotoFilepath;
        this.claimPhotoBitmap = claimPhotoBitmap;
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

    public String getClaimPhotoBase64() {
        return claimPhotoBase64;
    }

    public void setClaimPhotoBase64(String claimPhotoBase64) {
        this.claimPhotoBase64 = claimPhotoBase64;
    }

    public String getClaimPhotoFilepath() {
        return claimPhotoFilepath;
    }

    public void setClaimPhotoFilepath(String claimPhotoFilepath) {
        this.claimPhotoFilepath = claimPhotoFilepath;
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

    public Bitmap getClaimPhotoBitmap() {
        return claimPhotoBitmap;
    }

    public void setClaimPhotoBitmap(Bitmap claimPhotoBitmap) {
        this.claimPhotoBitmap = claimPhotoBitmap;
    }

    @Override
    public String toString() {
        return claimId + ": " + claimDes;
    }
}
