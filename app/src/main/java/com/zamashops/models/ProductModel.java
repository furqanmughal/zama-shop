package com.zamashops.models;

public class ProductModel {

    String product_id;
    String name;
    String price;
    String descripition;
    String[] product_images;
    String upload_date;
    String expiry_date;
    String product_status;
    String user_id;
    String sub_category_id;
    String product_city;
    String views;
    String product_type;
    boolean favorite;

    public ProductModel(){

    }

    public ProductModel(String product_id, String name, String price, String descripition,
                        String[] product_images, String upload_date, String expiry_date, String product_status,
                        String user_id, String sub_category_id, String product_city,String views) {
        this.product_id = product_id;
        this.name = name;
        this.price = price;
        this.descripition = descripition;
        this.product_images = product_images;
        this.upload_date = upload_date;
        this.expiry_date = expiry_date;
        this.product_status = product_status;
        this.user_id = user_id;
        this.sub_category_id = sub_category_id;
        this.product_city = product_city;
        this.views = views;
        this.favorite = false;
    }


    public ProductModel(String product_id, String name, String price, String descripition,
                        String[] product_images, String upload_date, String expiry_date, String product_status,
                        String user_id, String sub_category_id, String product_city,String views,String product_type) {
        this.product_id = product_id;
        this.name = name;
        this.price = price;
        this.descripition = descripition;
        this.product_images = product_images;
        this.upload_date = upload_date;
        this.expiry_date = expiry_date;
        this.product_status = product_status;
        this.user_id = user_id;
        this.sub_category_id = sub_category_id;
        this.product_city = product_city;
        this.views = views;
        this.favorite = false;
        this.product_type = product_type;
    }

    public String getProduct_type() {
        return product_type;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescripition() {
        return descripition;
    }

    public String[] getProduct_images() {
        return product_images;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public String getProduct_status() {
        return product_status;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public String getProduct_city() {
        return product_city;
    }

    public String getViews() {
        return views;
    }

    public void setProduct_status(String product_status) {
        this.product_status = product_status;
    }
}
