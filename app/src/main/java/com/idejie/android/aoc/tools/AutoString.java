package com.idejie.android.aoc.tools;

/**
 * Created by slf on 16/8/26.
 */

public class AutoString {

    private String result="";

    public AutoString(String name, String value) {
        result=result+name+"="+value;
    }

    public void addToResult(String name,String value){
        result=result+"&"+name+"="+value;
    }

    public String getResult() {
        return result;
    }
}
