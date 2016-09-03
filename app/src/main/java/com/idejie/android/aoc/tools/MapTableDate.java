package com.idejie.android.aoc.tools;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by slf on 16/9/2.
 */

public class MapTableDate {
    private String x;
    private String y;
    private Context mContext;

    public MapTableDate(Context context, String x, String y) {
        this.x = x;
        this.y = y;
        mContext=context;
    }
    @JavascriptInterface
    public String getX() {
        return x;
    }
    @JavascriptInterface
    public void setX(String x) {
        this.x = x;
    }
    @JavascriptInterface
    public String getY() {
        return y;
    }
    @JavascriptInterface
    public void setY(String y) {
        this.y = y;
    }
}
