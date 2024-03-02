package com.miniwhatsapp.Models;

public class UserModel {

    String userid, timestamp, firstname, lastname, email, phoneNumber, imageurl, otp;

    public UserModel(String userId, String timestamp, String namefirst, String namelast, String emails, String mobile, String imageurl, String otp) {
        this.userid = userId;
        this.timestamp = timestamp;
        this.firstname = namefirst;
        this.lastname = namelast;
        this.email = emails;
        this.phoneNumber = mobile;
        this.imageurl = imageurl;
        this.otp = otp;
    }

    public UserModel(String namefirst, String namelast, String imageurl, String timestamp,String userid) {
        this.firstname = namefirst;
        this.lastname = namelast;
        this.imageurl = imageurl;
        this.timestamp = timestamp;
        this.userid = userid;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
