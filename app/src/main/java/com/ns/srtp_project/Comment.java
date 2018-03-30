package com.ns.srtp_project;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/3/14.
 */

public class Comment {
    private int id;
    private int userId;
    private String text;
    private Date date;
    private String city;
    private String county;
    private String street;
    private int support;
    private String nickName;

    public Comment() {
        id=0;userId=0;text=null;date=null;city=null;county=null;street=null;support=-1;nickName=null;
    }

    public Comment(int id, int userId, String text, Date date, String city, String county, String street, int support,String nickName) {
        this.id = id;
        this.userId = userId;
        this.text = text;
        this.date = date;
        this.city = city;
        this.county = county;
        this.street = street;
        this.support = support;
        this.nickName=nickName;
    }

    public int getSupport() {
        return support;
    }

    public void setSupport(int support) {
        this.support = support;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
    public static Comment[] JSONtoObject(JSONArray jsonArray) throws JSONException, ParseException {
        Comment comment[]=new Comment[jsonArray.length()];
        DateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i;
        for(i=0;i<jsonArray.length();i++){
            comment[i]=new Comment(jsonArray.getJSONObject(i).getInt("id"),
                    jsonArray.getJSONObject(i).getInt("userid"),
                    jsonArray.getJSONObject(i).getString("text"),
                    format.parse(jsonArray.getJSONObject(i).getString("date")),
                    jsonArray.getJSONObject(i).getString("city"),
                    jsonArray.getJSONObject(i).getString("county"),
                    jsonArray.getJSONObject(i).getString("street"),
                    jsonArray.getJSONObject(i).getInt("support"),
                    jsonArray.getJSONObject(i).getString("nickname"));
        }
        return  comment;
    }
}
