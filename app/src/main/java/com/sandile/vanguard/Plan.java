package com.sandile.vanguard;

public class Plan {
    private String planName, planDetails, planDate;

    public Plan(){
    }

    public Plan(String planName, String planDetails, String planDate) {
        this.planName = planName;
        this.planDetails = planDetails;
        this.planDate = planDate;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDetails() {
        return planDetails;
    }

    public void setPlanDetails(String planDetails) {
        this.planDetails = planDetails;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }
}
