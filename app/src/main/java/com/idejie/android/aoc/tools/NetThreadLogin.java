package com.idejie.android.aoc.tools;

/**
 * Created by slf on 16/8/26.
 */

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by draft on 2015/7/23.
 */
public class NetThreadLogin extends Thread {
    private String params;
    private String url;
    private Handler han;
    String result = "";

    public NetThreadLogin(Handler han, String url, String params) {
        this.han = han;
        this.url = url;
        this.params = params;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        HttpURLConnection connection = null;
        try {
            URL realUrl = new URL(url);
            Log.d("test", "n1");
            //打开和URL之间的连接
            connection = (HttpURLConnection) realUrl.openConnection();
            //设置通用的请求属性

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(8000);
            // 设置 HttpURLConnection的字符编码
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("contentType", "utf-8");
            connection.setReadTimeout(8000);
            Log.d("test", "n2");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            Log.d("test", "n3");
            out.writeBytes(params);
            InputStream in = connection.getInputStream();
            //对获取的输入流进行读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            Log.d("test", "result" + "................." + result);
            Message mess = new Message();
            mess.what = 0;
            mess.obj = result;
            han.sendMessage(mess);

        } catch (FileNotFoundException e) {
            Message mess = new Message();
            mess.what = 1;
            han.sendMessage(mess);
        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}