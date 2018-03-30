package com.ns.srtp_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by 28537 on 2017/9/25.
 */

public class Fragment_Temperature extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Button button,button1;
    private EditText city,county,street;
    private int id;
    private ACache mCache;
    private ArrayList<Comment> arrayList;
    private HttpHelper httpHelper;
    private Handler handler1,handler2;
    private String URL="http://112.74.40.226/test/public/index.php/index/Index/queryComment?";
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temperature,container,false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCache = ACache.get(getContext());
        id=Integer.valueOf(mCache.getAsString("id"));
        button1=getActivity().findViewById(R.id.add_comment);
        button=getActivity().findViewById(R.id.search);
        city=getActivity().findViewById(R.id.city);
        county=getActivity().findViewById(R.id.county);
        street=getActivity().findViewById(R.id.street);
        mLayoutManager=new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerView=getActivity().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        arrayList=new ArrayList<Comment>();
        mAdapter = new CommentAdapter(getContext(),arrayList);
        recyclerView.setAdapter(mAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resecrch();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("id",id);
                intent.setClass(getActivity(),AddComment.class);
                startActivity(intent);
            }
        });
        handler1=new Handler(){
            public void handleMessage(android.os.Message msg) {
                Boolean state=msg.getData().getBoolean("1");
                Log.i("LoginState",String.valueOf(state));
                if(state)
                {
                    new GetThread().start();
                }
                else
                {
                    Toast.makeText(getActivity(), "连接失败!", Toast.LENGTH_LONG).show();
                }
            };
        };
        handler2=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                recyclerView.setAdapter(mAdapter);
            }
        };
        resecrch();
    }
    public void resecrch(){
        String url=URL+"city="+city.getText()+"&county="+county.getText()+"&street="+street.getText();
        httpHelper=new HttpHelper(url,"GET");
        new MyThread().start();
    }
    private class MyThread extends Thread{
        public void run(){
            Boolean state=httpHelper.connect();
            Message msg = handler1.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putBoolean("1", state);
            msg.setData(bundle);
            handler1.sendMessage(msg);
        }
    }
    private class GetThread extends Thread{
        public void run(){
            JSONArray jsonArray=httpHelper.returnJSONArray();
            try {
                arrayList=new ArrayList<Comment>();
                Comment []comments=Comment.JSONtoObject(jsonArray);
                for(int i=comments.length;i>0;i--){
                    arrayList.add(comments[i-1]);
                }
                mAdapter = new CommentAdapter(getContext(),arrayList);
                Message msg = handler2.obtainMessage();
                handler2.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    }
}
