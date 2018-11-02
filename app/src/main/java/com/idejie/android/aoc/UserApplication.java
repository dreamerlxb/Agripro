package com.idejie.android.aoc;

import android.app.Application;

import com.idejie.android.aoc.model.UserModel;

/**
 * Created by slf on 16/9/21.
 */

public class UserApplication extends Application{

    private String accessToken;
    private UserModel user;

    public void onCreate(){
        super.onCreate();
        accessToken = "";
        user = null;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

}
