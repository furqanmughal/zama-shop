package com.zamashops.models;

import android.graphics.Bitmap;

public class UploadImageModel {
    String stringImage;
    String tag;
    Bitmap image;
    boolean check;

    public UploadImageModel(String stringImage, String tag , Bitmap image) {
        this.stringImage = stringImage;
        this.tag = tag;
        this.image = image;
        check = true;
    }

    public UploadImageModel(String stringImage) {
        this.stringImage = stringImage;
        check = false;
    }


    public String getStringImage() {
        return stringImage;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isCheck() {
        return check;
    }

    public String getTag() {
        return tag;
    }
}

