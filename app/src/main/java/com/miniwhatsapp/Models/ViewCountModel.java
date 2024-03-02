package com.miniwhatsapp.Models;

public class ViewCountModel {

    String statusof_id, firstname, lastname, imageurl, userid, timestamp, activity;

    public ViewCountModel(String viewedids, String userids, String timestamps, String activitys) {
        this.statusof_id = viewedids;
        this.userid = userids;
        this.timestamp = timestamps;
        this.activity = activitys;
    }

    public ViewCountModel(String usersid, String userfirstName, String userLastName, String userimage, String timestamp) {
        this.userid = usersid;
        this.firstname = userfirstName;
        this.lastname = userLastName;
        this.imageurl = userimage;
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

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatusof_id() {
        return statusof_id;
    }

    public void setStatusof_id(String statusof_id) {
        this.statusof_id = statusof_id;
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
}
