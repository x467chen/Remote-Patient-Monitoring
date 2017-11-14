package com.ece651group8.uwaterloo.ca.ece_651_group8.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by chenxuanqi on 16/11/17.
 */

@DatabaseTable(tableName = "pid")
public class Pid {
    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private int value;

    Pid(){
    }

    public Pid(int pidvalue){
        id = "pid";
        value = pidvalue;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
