package com.idejie.android.aoc.tools;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by slf on 16/9/2.
 */

public class MapTableDate {
    private String mapData;

    public MapTableDate(String x) {
        this.mapData = x;
    }
    @JavascriptInterface
    public String getMapData() {
        return mapData;
    }
}
