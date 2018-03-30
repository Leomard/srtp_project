package com.ns.srtp_project;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.RadioGroup;

/**
 * Created by 28537 on 2017/9/23.
 */

public class MainIndex extends FragmentActivity {

    private FragmentManager fragmentManager;
    private Button button1,button2,button3;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        radioGroup=findViewById(R.id.rg_tab);
        button1=findViewById(R.id.b1);
        button2=findViewById(R.id.b2);
        button3=findViewById(R.id.b3);
        fragmentManager = getSupportFragmentManager();
        IniGroup();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment_Temperature fragment = new Fragment_Temperature();
        transaction.add(R.id.content,fragment );
        transaction.commit();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                int s=radioGroup.getCheckedRadioButtonId ();
                if(s== R.id.b1)
                {
                    Fragment fragment=new Fragment_Temperature();
                    transaction.replace(R.id.content,fragment );
                    transaction.commit();

                }
                else if(s== R.id.b2)
                {
                    Fragment fragment=new Fragment_Travel();
                    transaction.replace(R.id.content,fragment );
                    transaction.commit();
                }
                else if(s== R.id.b3)
                {
                    Fragment fragment=new Fragment_Personnel();
                    transaction.replace(R.id.content,fragment );
                    transaction.commit();
                }

            }
        });
    }
    private void IniGroup()
    {
        Drawable drawableWeiHui = getResources().getDrawable(R.drawable.view_1_selector);
        drawableWeiHui.setBounds(0, 0, 70, 70);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        button1.setCompoundDrawables(null, drawableWeiHui, null, null);//只放上面

        Drawable drawableAdd = getResources().getDrawable(R.drawable.view_2_selector);
        drawableAdd.setBounds(0, 0, 70, 70);
        button2.setCompoundDrawables(null, drawableAdd, null, null);

        Drawable drawableRight = getResources().getDrawable(R.drawable.view_3_selector);
        drawableRight.setBounds(0, 0, 70, 70);
        button3.setCompoundDrawables(null, drawableRight, null, null);
        //初始化底部标签
    }
}
