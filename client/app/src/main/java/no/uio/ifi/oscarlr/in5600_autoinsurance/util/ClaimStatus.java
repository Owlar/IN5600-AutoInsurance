package no.uio.ifi.oscarlr.in5600_autoinsurance.util;

import androidx.annotation.NonNull;

// Simple utility class to get a claim's status
public enum ClaimStatus {
    OPENED("Opened"), REOPENED("Re-opened"), CLOSED("Closed");

    private final String text;

    ClaimStatus(final String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
