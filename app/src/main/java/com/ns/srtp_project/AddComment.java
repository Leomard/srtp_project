package com.ns.srtp_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/3/23.
 */

public class AddComment extends Activity {

    private int id;
    private Button button;
    private EditText editText1,editText2,editText3,editText4;
    private HttpHelper httpHelper;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comment);
        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);
        button=findViewById(R.id.comment_add);
        editText1=findViewById(R.id.comment_add_text);
        editText2=findViewById(R.id.comment_add_city);
        editText3=findViewById(R.id.comment_add_county);
        editText4=findViewById(R.id.comment_add_street);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                Toast.makeText(AddComment.this,bundle.getString("i"),Toast.LENGTH_LONG).show();
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyThread().start();
            }
        });
    }
    private class MyThread extends Thread{
        public void run(){
            String URL="http://112.74.40.226/test/public/index.php/index/Index/createComment?userid="+String.valueOf(id)
                    +"&text="+editText1.getText().toString()+"&city="+editText2.getText().toString()+"&county="+editText3.getText().toString()
                    +"&street="+editText4.getText().toString();
            httpHelper=new HttpHelper(URL,"GET");
            Boolean state=httpHelper.connect();
            if(state){
                int result=httpHelper.returnINT();
                if(result==1){
                    Message msg=handler.obtainMessage();
                    Bundle bundle=new Bundle();
                    bundle.putString("i","添加成功");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    Intent intent=new Intent();
                    intent.setClass(AddComment.this,MainIndex.class);
                    startActivity(intent);
                }else{
                    Message msg=handler.obtainMessage();
                    Bundle bundle=new Bundle();
                    bundle.putString("i","添加失败");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }else{
                Message msg=handler.obtainMessage();
                Bundle bundle=new Bundle();
                bundle.putString("i","连接失败");
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }
    }
}
