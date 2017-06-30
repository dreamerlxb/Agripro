package com.idejie.android.aoc.callback;

import android.support.v4.util.ArrayMap;

/**
 * Created by sxb on 2017/1/20.
 */

public abstract class CustomCallback {

    public Object tag;

    public CustomCallback(Object tag) {
        this.tag = tag;
    }

    public void onSuccess(Object count){

    }

    public void onError(Throwable t) {

    }
}
