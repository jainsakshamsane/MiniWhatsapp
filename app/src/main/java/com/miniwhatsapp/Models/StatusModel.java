package com.miniwhatsapp.Models;

public class StatusModel {

    String userid, imageurl, text, statusid, timestamp, firstname, lastname, userimageurl;

    public StatusModel(String userId, String uploadedImageUrl, String text, String timestamp, String firstname, String lastname, String userimageurl) {
        this.userid = userId;
        this.imageurl = uploadedImageUrl;
        this.text = text;
        this.timestamp = timestamp;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userimageurl = userimageurl;
    }

    public StatusModel(String userId, String uploadedImageUrl, String text, String statusidd, String timestamp, String firstname, String lastname, String userimageurl) {
        this.userid = userId;
        this.imageurl = uploadedImageUrl;
        this.text = text;
        this.statusid = statusidd;
        this.timestamp = timestamp;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userimageurl = userimageurl;
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

    public String getUserimageurl() {
        return userimageurl;
    }

    public void setUserimageurl(String userimageurl) {
        this.userimageurl = userimageurl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatusid() {
        return statusid;
    }

    public void setStatusid(String statusid) {
        this.statusid = statusid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
