package com.example.facedemo.Model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiRequest {
    @SerializedName("img")
    @Expose
    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @NonNull
    @Override
    public String toString() {
        return "{"+'\n'+"img :"+string+'\n'+"}";
    }
}
