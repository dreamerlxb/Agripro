package com.idejie.android.aoc.tools;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by slf on 16/9/2.
 */

public class LineTableDate {
    private String lineData;

    public LineTableDate(String x) {
        this.lineData = x;
    }

    @JavascriptInterface
    public String getLineData() {
        return lineData;
    }
}
