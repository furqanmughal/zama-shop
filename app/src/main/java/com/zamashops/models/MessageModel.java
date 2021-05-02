package com.zamashops.models;

public class MessageModel {


    String msg_id;
    String message;
    String time;
    String date;
    String sender_user_id;
    String user_name;
    String user_image;
    String type;
    String status;


    public MessageModel(){

    }

    public MessageModel(String msg_id, String message, String time, String date, String sender_user_id, String user_name,String user_image, String status) {
        this.msg_id = msg_id;
        this.message = message;
        this.time = time;
        this.date = date;
        this.sender_user_id = sender_user_id;
        this.user_name = user_name;
        this.user_image = user_image;
        this.status = status;
        this.type = "";
    }



    public String getType() {
        return type;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getStatus() {
        return status;
    }
}
