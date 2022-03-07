package no.uio.ifi.oscarlr.in5600_autoinsurance.model;

import java.util.ArrayList;

public class Claim {
    public String id;
    public String numberOfClaims;
    public String claimId;
    public String claimDes;
    public String claimPhoto;
    public String claimLocation;
    public String claimStatus;

    public Claim() {

    }

    public Claim(String id, String numberOfClaims, String claimId, String claimDes, String claimPhoto, String claimLocation, String claimStatus) {
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

    public String getClaimPhoto() {
        return claimPhoto;
    }

    public void setClaimPhoto(String claimPhoto) {
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
}
