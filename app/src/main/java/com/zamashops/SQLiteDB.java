package com.zamashops;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.zamashops.models.CategoryModel;
import com.zamashops.models.CityModel;

import java.util.ArrayList;

public class SQLiteDB extends SQLiteOpenHelper {

    public static String db_name = "zamashops_db";


    public SQLiteDB(@Nullable Context context) {
        super(context, db_name, null, 1);
    }


    public static String category_tbl = "category_tbl";
    public static String _id = "_id";
    public static String cat_id = "cat_id";
    public static String cat_name = "cat_name";

    public static String CRATE_CATEGORY_TBL = "CREATE TABLE IF NOT EXISTS "+category_tbl+"("+cat_id+" INTEGER PRIMARY KEY,"+cat_name+" TEXT)";


    public static String city_tbl = "city_tbl";
    public static String city_id = "city_id";
    public static String city_name = "city_name";

    public static String CRATE_CITY_TBL = "CREATE TABLE IF NOT EXISTS "+city_tbl+"("+city_id+" INTEGER PRIMARY KEY,"+city_name+" TEXT)";






    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CRATE_CATEGORY_TBL);
        sqLiteDatabase.execSQL(CRATE_CITY_TBL);
    }



    public boolean insertData(String table_name, ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(table_name,null,values)>0;
    }

    public boolean deleteCity( String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(city_tbl,city_id+"="+id,null)>0;
    }

    public boolean deleteCategory( String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(category_tbl,cat_id+"="+id,null)>0;
    }

    //Get Data
    public ArrayList<CategoryModel> allCategory(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ArrayList<CategoryModel> items = new ArrayList<>();
            Cursor result = db.rawQuery("SELECT * FROM "+category_tbl,null);
            result.moveToFirst();
            while (result.isAfterLast() == false){
                items.add(new CategoryModel(result.getString(0),result.getString(1),""));
                result.moveToNext();
            }
            return items;
        }catch (Exception ex){
            ex.printStackTrace();

            return null;
        }
    }

    //Get Data
    public ArrayList<CityModel> allCity(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ArrayList<CityModel> items = new ArrayList<>();
            Cursor result = db.rawQuery("SELECT * FROM "+city_tbl,null);
            result.moveToFirst();
            while (result.isAfterLast() == false){
                items.add(new CityModel(result.getString(0),result.getString(1)));
                result.moveToNext();
            }
            return items;
        }catch (Exception ex){
            ex.printStackTrace();

            return null;
        }
    }

    //Get Data
    public String allCityInString(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ArrayList<CityModel> items = new ArrayList<>();
            Cursor result = db.rawQuery("SELECT * FROM "+city_tbl,null);

            String cities = "";

            result.moveToFirst();
            while (result.isAfterLast() == false){
               cities += result.getString(0)+",";
                result.moveToNext();
            }
            if (cities != null && cities.length() > 0 && cities.charAt(cities.length() - 1) == ',') {
                cities = cities.substring(0, cities.length() - 1);
            }
            return cities;
        }catch (Exception ex){
            ex.printStackTrace();

            return null;
        }
    }

    //Get Data
    public String allCategoryInString(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ArrayList<CategoryModel> items = new ArrayList<>();
            Cursor result = db.rawQuery("SELECT * FROM "+category_tbl,null);
            String categories = "";
            result.moveToFirst();
            while (result.isAfterLast() == false){
               categories += result.getString(0)+",";
                result.moveToNext();
            }
            if (categories != null && categories.length() > 0 && categories.charAt(categories.length() - 1) == ',') {
                categories = categories.substring(0, categories.length() - 1);
            }
            return categories;
        }catch (Exception ex){
            ex.printStackTrace();

            return null;
        }
    }





    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
