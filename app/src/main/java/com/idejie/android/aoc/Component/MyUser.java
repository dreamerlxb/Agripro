package com.idejie.android.aoc.Component;

import com.idejie.android.aoc.activity.MainActivity;
import com.idejie.android.aoc.activity.WelcomeActivity;

/**
 * Created by shandongdaxue on 16/8/6.
 */
public class MyUser {
    private static final long serialVersionUID = 1L;

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public static String getObjectByKey(WelcomeActivity welcomeActivity, String username) {
        return null;
    }

    public static MyUser getCurrentUser(MainActivity mainActivity, Class<MyUser> myUserClass) {
        return new MyUser();
    }
}
