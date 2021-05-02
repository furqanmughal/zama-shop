package com.zamashops.models;

public class ChatModel {

    String chat_id;
    String user_name;
    String user_id_1;
    String user_id_2;
    String user_image;
    String product_id;
    String first_message;
    String count;
    String date;
    String account_type;

    public ChatModel(String chat_id,String user_name,String user_image, String product_id, String first_message,String date,String count,String account_type) {
        this.chat_id = chat_id;
        this.user_name = user_name;
        this.user_image = user_image;
        this.product_id = product_id;
        this.first_message = first_message;
        this.count = count;
        this.date = date;
        this.account_type = account_type;
    }

    public String getUser_id_1() {
        return user_id_1;
    }

    public void setUser_id_1(String user_id_1) {
        this.user_id_1 = user_id_1;
    }

    public String getUser_id_2() {
        return user_id_2;
    }

    public void setUser_id_2(String user_id_2) {
        this.user_id_2 = user_id_2;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getFirst_message() {
        return first_message;
    }

    public String getCount() {
        return count;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getDate() {
        return date;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getAccount_type() {
        return account_type;
    }
}
