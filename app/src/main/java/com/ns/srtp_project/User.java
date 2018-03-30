package com.ns.srtp_project;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/11.
 */

public class User {
    private int id;
    private String username;
    private String password;
    private String address;
    private String tel;
    private short sex;
    private String nickname;

    public User() {
        this.id=0;
        this.username=new String();
        this.password=new String();
        this.tel=new String();
        this.sex=-1;
        this.address=new String();
        this.nickname=new String();
    }

    public User(int id, String username, String password, String address, String tel, short sex,String nickname) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.tel = tel;
        this.sex = sex;
        this.nickname=nickname;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public short getSex() {
        return sex;
    }

    public void setSex(short sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void JSONtoObject(JSONObject jsonObject){
        try {
            id = jsonObject.getInt("id");
            username=jsonObject.getString("username");
            password=jsonObject.getString("password");
            tel=jsonObject.getString("tel");
            address=jsonObject.getString("address");
            sex=(short)jsonObject.getInt("sex");
            nickname=jsonObject.getString("nickname");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
