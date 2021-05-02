package com.zamashops.models;

public class CategoryModel {
    String cat_id;
    String cat_image;
    String cat_name;

    public CategoryModel(String cat_id, String cat_name, String cat_image) {
        this.cat_id = cat_id;
        this.cat_image = cat_image;
        this.cat_name = cat_name;
    }



    public CategoryModel() {
        this.cat_id = "-1";
        this.cat_image = "Home";
        this.cat_name = "Home and autos";
    }

    public String getCat_id() {
        return cat_id;
    }

    public String getCat_image() {
        return cat_image;
    }

    public String getCat_name() {
        return cat_name;
    }
}
