package com.ece651group8.uwaterloo.ca.ece_651_group8.bean;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "token")
public class Token {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String value;

    Token(){
    }

    public Token(String tokenValue){
        id = "token";
        value = tokenValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
