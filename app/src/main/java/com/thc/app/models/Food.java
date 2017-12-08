package com.thc.app.models;

import android.content.Context;

import com.thc.app.database.DBHelper;


public class Food {

    String name;
    int calorie;

    public Food(String name, int calorie) {
        this.name = name;
        this.calorie = calorie;
    }

    public void save(Context context)
    {
        DBHelper dbHelper=new DBHelper(context,"THC",null,1);
        dbHelper.insertFood(this);
    }

    public String getName() {
        return name;
    }

    public int getCalorie() {
        return calorie;
    }
}
