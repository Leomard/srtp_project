package com.ns.srtp_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by 28537 on 2017/9/23.
 */

public class LoginActivity extends Activity {

    private Button login_in,register;
    private ACache mCache;
    private EditText password,username;
    private boolean isLogin;
    private HttpHelper httpHelper;
    private int id;
    private Handler handler1,handler2;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mCache = ACache.get(LoginActivity.this);
        login_in=(Button) findViewById(R.id.email_sign_in_button);
        password=(EditText) findViewById(R.id.password);
        username=(EditText) findViewById(R.id.email);
        register=findViewById(R.id.email_register_button);
        intent = new Intent();
        intent.setClass(LoginActivity.this, MainIndex.class);
        login_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegister();
            }
        });
        isLogin=false;
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
                    Toast.makeText(LoginActivity.this, "连接失败!", Toast.LENGTH_LONG).show();
                }
            };
        };
        handler2=new Handler(){
            public void handleMessage(android.os.Message msg) {
                Log.i("Login",String.valueOf(id));
                if(id!=0){
                    isLogin=true;
                }
                else{
                    isLogin=false;
                    Toast.makeText(LoginActivity.this, "登录失败!", Toast.LENGTH_LONG).show();
                }
                if(isLogin) {
                    mCache.put("id",String.valueOf(id));
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
            };
        };
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
            id=httpHelper.returnINT();
            Message msg = handler2.obtainMessage();
            handler2.sendMessage(msg);
        }
    }
    protected void onLogin(){
        String URL=new String("");
        URL="http://112.74.40.226/test/public/index.php/index/Index/login?username="+username.getText()+"&password="+password.getText();
        httpHelper=new HttpHelper(URL,"GET");
        new MyThread().start();
    }
    protected void onRegister(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, ActivityRegister.class);
        startActivity(intent);
    }
}
