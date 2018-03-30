package com.ns.srtp_project;


import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/3/10.
 */

public class HttpHelper {
    private String urlString;
    private String method;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private StringBuffer buffer;
    private HttpURLConnection httpUrlConn;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    public HttpHelper(String URL,String method){
        this.method=method;
        this.urlString=URL;
        jsonObject = null;
        buffer = new StringBuffer();
    }
    public static void closeStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll().penaltyLog().build());
    }
    public boolean connect(){
        try {
            URL url = new URL(urlString);
            // http协议传输
            httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(true);
            httpUrlConn.setConnectTimeout(6*1000);
            httpUrlConn.setReadTimeout(6 * 1000);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod(method);
            httpUrlConn.connect();
            // 将返回的输入流转换成字符串

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public JSONObject returnJSON(){
        try{
            inputStream = httpUrlConn.getErrorStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            Log.i("get", "returnJSON: "+buffer.toString());
            jsonObject = new JSONObject(buffer.toString());
            return jsonObject;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray returnJSONArray(){
        try{
            inputStream = httpUrlConn.getErrorStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            Log.i("get", "returnJSONArray: "+buffer.toString());
            return jsonArray = new JSONArray(buffer.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public int returnINT(){
        int i=0;
        try{
            inputStream = httpUrlConn.getErrorStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            Log.i("get", "returnINT: "+buffer.toString());
            i=Integer.valueOf(buffer.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return i;
    }
    public void exit(){
        try {
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
