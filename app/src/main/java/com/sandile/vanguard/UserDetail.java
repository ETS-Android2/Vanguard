package com.sandile.vanguard;

public class UserDetail {//Singleton used
    private String email, favouriteLandmark, preferredLandmarkType;
    private Boolean isMetric;

    public static String id;

    public UserDetail(String email, String favouriteLandmark, String preferredLandmarkType, Boolean isMetric) {
        this.email = email;
        this.favouriteLandmark = favouriteLandmark;
        this.preferredLandmarkType = preferredLandmarkType;
        this.isMetric = isMetric;
    }

    public UserDetail(){

    }

    ///////////getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFavouriteLandmark() {
        return favouriteLandmark;
    }

    public void setFavouriteLandmark(String favouriteLandmark) {
        this.favouriteLandmark = favouriteLandmark;
    }

    public String getPreferredLandmarkType() {
        return preferredLandmarkType;
    }

    public void setPreferredLandmarkType(String preferredLandmarkType) {
        this.preferredLandmarkType = preferredLandmarkType;
    }

    public Boolean getIsMetric() {
        return isMetric;
    }

    public void setIsMetric(Boolean isMetric) {
        this.isMetric = isMetric;
    }
}
