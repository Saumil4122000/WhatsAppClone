package com.example.geeksproject.model.user;


import android.net.Uri;

import com.google.firebase.firestore.DocumentSnapshot;

public class Users {
    private String userID;
    private String userName;
    private String userPhone;
    private String imageProfile;
    private String imageCover;
    private String email;
    private String dateOfBirth;
    private String gender;
    private String status;
    private String bio;
    private String typingTo;
    private String onlineStatus;
    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    // private String about;
    public Users() {
    }


    public Users( String userID, String userName, String userPhone, String imageProfile, String imageCover, String email, String dateOfBirth, String gender, String status, String bio) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.status = status;
        this.bio = bio;

    }

    public Users( String userID, String userName, String userPhone, String imageProfile, String imageCover, String email, String dateOfBirth, String gender, String status, String bio,String typingTo,String onlineStatus) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.status = status;
        this.bio = bio;
        this.typingTo=typingTo;
        this.onlineStatus=onlineStatus;
    }

    public String getUserID() {
        return userID;
    }




    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageCover() {
        return imageCover;
    }

    public void setImageCover(String imageCover) {
        this.imageCover = imageCover;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
