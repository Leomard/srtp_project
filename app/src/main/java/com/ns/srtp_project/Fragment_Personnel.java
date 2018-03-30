package com.ns.srtp_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 28537 on 2017/9/25.
 */

public class Fragment_Personnel extends Fragment {

    private int id;
    private TextView tv;
    private Handler handler1,handler2;
    private HttpHelper httpHelper;
    private ACache mCache;
    private User user;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personnel,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCache = ACache.get(getContext());
        id=Integer.valueOf(mCache.getAsString("id"));
        tv=getActivity().findViewById(R.id.username);
        handler1=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                tv.setText(user.getNickname());
            }
        };
        handler2=new Handler(){
            @Override
            public void handleMessage(Message msg) {

            }
        };
        new GetThread().start();
    }
    private class GetThread extends Thread{
        public void run() {
            httpHelper=new HttpHelper("http://112.74.40.226/test/public/index.php/index/Index/getUser?id="+String.valueOf(id),"GET");
            Boolean state=httpHelper.connect();
            if(state){
                user=new User();
                user.JSONtoObject(httpHelper.returnJSON());
                Message msg=handler1.obtainMessage();
                handler1.sendMessage(msg);
            }else{
                Toast.makeText(getActivity(),"连接失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
