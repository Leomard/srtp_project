package com.ns.srtp_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.show.api.ShowApiRequest;

import java.util.Date;

/**
 * Created by 28537 on 2017/9/25.
 */

public class Fragment_Travel extends Fragment {

    private Button start_button,update;

    private EditText editText;
    private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12,tv13,tv14,tv15,tv16,test;
    private int id;
    private int i;
    private ACache mCache;
    private String appid="59888";//要替换成自己的
    private String secret="4e44d7b386a74065b72d0ab296764519";
    protected Handler mHandler;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCache = ACache.get(getContext());
        i=0;
        start_button=(Button) getActivity().findViewById(R.id.startcamera);
        update=getActivity().findViewById(R.id.update);
        editText=getActivity().findViewById(R.id.update_place);
        tv1=getActivity().findViewById(R.id.place);//place
        tv2=getActivity().findViewById(R.id.place_weather);//weather
        tv3=getActivity().findViewById(R.id.temperature);//temperature
        tv4=getActivity().findViewById(R.id.day_temperature);//day_temperature
        tv5=getActivity().findViewById(R.id.night_temperature);//night_temperature
        tv6=getActivity().findViewById(R.id.aqi);//api
        tv7=getActivity().findViewById(R.id.quality);//quality
        tv8=getActivity().findViewById(R.id.sd);//sd
        tv9=getActivity().findViewById(R.id.co);//co
        tv10=getActivity().findViewById(R.id.no2);//no2
        tv11=getActivity().findViewById(R.id.o3);//03
        tv12=getActivity().findViewById(R.id.pm10);//pm10
        tv13=getActivity().findViewById(R.id.pm2_5);//pm2.5
        tv14=getActivity().findViewById(R.id.so2);//s02
        tv15=getActivity().findViewById(R.id.wind_direction);//wind_direction
        tv16=getActivity().findViewById(R.id.wind_power);//wind_power
        test=getActivity().findViewById(R.id.test);
        Intent intent=getActivity().getIntent();
        id=intent.getIntExtra("id",1);
        mHandler =  new Handler();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    i=1;
                    new MyThread(editText.getText().toString()).start();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(),ActivityCamera.class);
                getActivity().startActivity(intent);
            }
        });
        mHandler=new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        String n=mCache.getAsString("place");
        try{
        if(n!=null)
        {
            new MyThread(n).start();
        }
        else
        {
            new MyThread("北京").start();
        }}catch (Exception e){
            e.printStackTrace();
        }
    }
    private class MyThread extends Thread{
        private String placeName="丽江";
        public MyThread(String name){
            super();
            placeName=new String(name);
        }
        public void run(){
            final String res=new ShowApiRequest( "http://route.showapi.com/9-2", appid, secret)
                    .addTextPara("areaid", "")
                    .addTextPara("area", placeName)
                    .addTextPara("needMoreDay", "0")
                    .addTextPara("needIndex", "0")
                    .addTextPara("needHourData", "0")
                    .addTextPara("need3HourForcast", "0")
                    .addTextPara("needAlarm", "0")
                    .post();
            System.out.println(res);
            com.alibaba.fastjson.JSONObject js=com.alibaba.fastjson.JSONObject.parseObject(res);
            if(!js.get("showapi_res_code").toString().equals("0"))
                mHandler.post(new Thread(){
                   public void run(){
                       Toast.makeText(getActivity(),"获取失败",Toast.LENGTH_LONG).show();
                   }
                });
            else if(js.getJSONObject("showapi_res_body").get("ret_code").toString().equals("0"))
            {
                if(i==1)
                    mCache.put("place",editText.getText().toString());
                mHandler.post(new Thread(){
                    public void run() {
                        com.alibaba.fastjson.JSONObject js=com.alibaba.fastjson.JSONObject.parseObject(res);
                        tv1.setText(js.getJSONObject("showapi_res_body").getJSONObject("cityInfo").get("c3").toString());
                        tv2.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").get("weather").toString());
                        tv3.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").get("temperature").toString());
                        tv4.setText(js.getJSONObject("showapi_res_body").getJSONObject("f1").get("day_air_temperature").toString());
                        tv5.setText(js.getJSONObject("showapi_res_body").getJSONObject("f1").get("night_air_temperature").toString());
                        tv6.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("aqi").toString());
                        tv7.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("quality").toString());
                        tv8.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").get("sd").toString());
                        tv9.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("co").toString());
                        tv10.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("no2").toString());
                        tv11.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("o3").toString());
                        tv12.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("pm10").toString());
                        tv13.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("pm2_5").toString());
                        tv14.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").getJSONObject("aqiDetail").get("so2").toString());
                        tv15.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").get("wind_direction").toString());
                        tv16.setText(js.getJSONObject("showapi_res_body").getJSONObject("now").get("wind_power").toString());
                    }
                });
            }
            else
            {
                mHandler.post(new Thread(){
                    public void run(){
                        Toast.makeText(getActivity(),"地名输入错误",Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
    }
}
