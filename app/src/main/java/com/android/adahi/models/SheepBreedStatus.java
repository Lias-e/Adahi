package com.android.adahi.models;

/**
 * Represents the availability of one sheep breed inside a wilaya.
 */
public class SheepBreedStatus {
    private String breedName;
    private boolean available;
    private String statusText;
    private String details;

    public SheepBreedStatus() {
    }

    public SheepBreedStatus(String breedName, boolean available, String statusText, String details) {
        this.breedName = breedName;
        this.available = available;
        this.statusText = statusText;
        this.details = details;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}