package com.zamashops.models;

public class UserModel {

    String user_id;
    String user_name;
    String user_email;
    String user_contact;
    String user_address;
    String user_password;
    String account_type;
    String user_image;
    boolean follow;
    String reviews;
    String token;

    public UserModel(String user_id, String user_name, String user_email,
                     String user_contact, String user_address, String user_password, String account_type, String user_image, boolean follow, String reviews) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_contact = user_contact;
        this.user_address = user_address;
        this.user_password = user_password;
        this.account_type = account_type;
        this.user_image = user_image;
        this.follow = follow;
        this.reviews = reviews;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_contact() {
        return user_contact;
    }

    public String getUser_address() {
        return user_address;
    }

    public String getUser_password() {
        return user_password;
    }

    public String getAccount_type() {
        return account_type;
    }

    public String getUser_image() {
        return user_image;
    }

    public boolean getFollow() {
        return follow;
    }

    public String getReviews() {
        return reviews;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_contact(String user_contact) {
        this.user_contact = user_contact;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }
}


