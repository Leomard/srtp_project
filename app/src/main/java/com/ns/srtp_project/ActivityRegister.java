package com.ns.srtp_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/3/10.
 */

public class ActivityRegister extends Activity {
    private Button button;
    private EditText mUsername,mPassword,mRepeatPassword,mTel,mAddress,mNickname;
    private RadioGroup mSex;
    private RadioButton male,female;
    private User user;
    private Handler handler1,handler2;
    private Boolean checkUsername;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button=findViewById(R.id.button);
        mUsername=findViewById(R.id.username);
        mPassword=findViewById(R.id.password);
        mRepeatPassword=findViewById(R.id.repeat_password);
        mTel=findViewById(R.id.tel);
        mAddress=findViewById(R.id.address);
        mSex=findViewById(R.id.sex_rg);
        mNickname=findViewById(R.id.nickname);
        male=findViewById(R.id.male_rb);
        female=findViewById(R.id.female_rb);
        checkUsername=false;
        id=-1;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        handler1=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Boolean informationState=checkInformation();
                if(informationState){
                    new RegisterThread().start();
                }else{
                    Toast.makeText(ActivityRegister.this, "请输入正确的信息", Toast.LENGTH_LONG).show();
                }
            }
        };
        handler2=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(ActivityRegister.this, "注册成功\n返回登录!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(ActivityRegister.this, LoginActivity.class);
                startActivity(intent);
            }
        };
    }
    public void register(){
        if(mUsername.getText().length()>=8) {
            new CheckUserThread().start();
        }else{
            Toast.makeText(ActivityRegister.this, "用户名需要多于七个字符", Toast.LENGTH_LONG).show();
        }
    }
    public Boolean checkInformation(){
        if(mPassword.getText().length()<8){
            Toast.makeText(ActivityRegister.this, "密码需要多于七个字符", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!mPassword.getText().toString().equals(mRepeatPassword.getText().toString())){
            Toast.makeText(ActivityRegister.this, "重复密码不相同", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    private class CheckUserThread extends Thread{
        public void run() {
            String url="http://112.74.40.226/test/public/index.php/index/Index/checkUserName?username="+mUsername.getText();
            HttpHelper httpHelper=new HttpHelper(url,"GET");
            Boolean connectState=httpHelper.connect();
            Looper.prepare();
            if(connectState){
                id=httpHelper.returnINT();
                if(id==-1){
                    Toast.makeText(ActivityRegister.this, "获取失败!", Toast.LENGTH_LONG).show();
                }else if(id==0){
                    Message msg=handler1.obtainMessage();
                    handler1.sendMessage(msg);
                }else{
                    Toast.makeText(ActivityRegister.this, "用户名已存在!", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(ActivityRegister.this, "连接失败!", Toast.LENGTH_LONG).show();
            }
            Looper.loop();
            httpHelper.exit();
        }
    }
    private class RegisterThread extends Thread{
        public void run(){
            String url="http://112.74.40.226/test/public/index.php/index/Index/addUser?username="+mUsername.getText();
            url=url+"&password="+mPassword.getText()+"&address="+mAddress.getText()+"&tel="+mTel.getText()+"&nickname="+mNickname.getText();
            if(mSex.getCheckedRadioButtonId()==male.getId()){
                url=url+"&sex=0";
            }else if(mSex.getCheckedRadioButtonId()==female.getId()){
                url=url+"&sex=1";
            }
            Looper.prepare();
            id=-1;
            HttpHelper httpHelper=new HttpHelper(url,"GET");
            Boolean connectState=httpHelper.connect();
            if(connectState){
                id=httpHelper.returnINT();
                if(id==-1){
                    Toast.makeText(ActivityRegister.this, "获取失败!", Toast.LENGTH_LONG).show();
                }else if(id==0){
                    Toast.makeText(ActivityRegister.this, "注册失败!", Toast.LENGTH_LONG).show();
                }else if(id==1){
                    Message msg=handler2.obtainMessage();
                    handler2.sendMessage(msg);
                }
            }else{
                Toast.makeText(ActivityRegister.this, "连接失败!", Toast.LENGTH_LONG).show();
            }
            Looper.loop();
            httpHelper.exit();
        }
    }
}
