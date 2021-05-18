package com.sandile.vanguard;

public class UserDetail {
    private String id, email, password, favouriteLandmark, preferredLandmarkType;
    private Boolean isMetric;

    private static UserDetail userDetail;

    public UserDetail userDetail(){
        if(userDetail == null){
            userDetail = new UserDetail();
        }
        return userDetail;
    }

    public UserDetail(){
    }

    public UserDetail(String id, String email, String password, String favouriteLandmark, String preferredLandmarkType, Boolean isMetric) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.favouriteLandmark = favouriteLandmark;
        this.preferredLandmarkType = preferredLandmarkType;
        this.isMetric = isMetric;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setIsMetric(Boolean metric) {
        isMetric = metric;
    }
}
