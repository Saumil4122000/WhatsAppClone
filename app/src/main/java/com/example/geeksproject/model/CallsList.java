package com.example.geeksproject.model;


public class CallsList {
    private String userId;
    private String userName;
    private String urlProfile;
    private String calltype;

    public CallsList(String userId, String userName, String urlProfile, String calltype, String date) {
        this.userId = userId;
        this.userName = userName;
        this.urlProfile = urlProfile;
        this.calltype = calltype;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private  String date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public CallsList(String userId, String userName, String urlProfile, String calltype) {
        this.userId = userId;
        this.userName = userName;
        this.urlProfile = urlProfile;
        this.calltype = calltype;
    }
}
