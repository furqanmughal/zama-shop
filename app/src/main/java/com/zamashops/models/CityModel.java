package com.zamashops.models;

public class CityModel {

    String id;
    String name;

    public CityModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
