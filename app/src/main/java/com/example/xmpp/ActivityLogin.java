package com.example.xmpp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xmpp.Utils.ThreadUtils;
import com.example.xmpp.Utils.ToastUtils;
import com.example.xmpp.service.IMservice;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.w3c.dom.Text;

public class ActivityLogin extends AppCompatActivity {

    public static final String HOST = "192.168.56.1";//主机IP
    public static final int PORT = 5222;//端口号
    private Button btnLoign;
    private TextView etpassword;
    private TextView etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }
    private void initListener(){
        btnLoign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String  user_Name=etUsername.getText().toString();
                final String  pass_word=etpassword.getText().toString();
                //判断用户名是为空
                if(TextUtils.isEmpty(user_Name)){
                    etUsername.setError("用户名不能为空");
                }
                if(TextUtils.isEmpty(pass_word)){
                    etpassword.setError("密码不能为空");
                }
                ThreadUtils.runInThread(new Runnable() {
                    @Override
                    public void run() {

                            //创建连接配置对象
                            ConnectionConfiguration config=new ConnectionConfiguration(HOST, PORT);
                            //额外的配置
                            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//明文传输
                            config.setDebuggerEnabled(true);//开启调试
                            //创建连接对象
                            XMPPConnection conn= new XMPPConnection(config);

                        try {
                            //开始连接
                            conn.connect();
                            //连接成功
                            //开始登录

                            conn.login(user_Name, pass_word);
                            //已经登录成功

                            ToastUtils.showToastSafe(ActivityLogin.this,"登陆成功");
                            //调到主界面
                            Intent intent=new Intent(ActivityLogin.this,indexActivity.class);
                            startActivity(intent);
                            //保存连接对象
                            IMservice.conn=conn;

                            //保存当前登录账户
                            String account = user_Name+"@"+ActivityLogin.ACTIVITY_SERVICE;
                            IMservice.mCurAccout = account;//admin
                            finish();


                        }catch (XMPPException e){
                            e.printStackTrace();
                            ToastUtils.showToastSafe(ActivityLogin.this,"登陆失败");
                        }


                    }
                });




            }
        });


    }
    private  void initView(){
        etUsername = (TextView) findViewById(R.id.username);
        etpassword = (TextView) findViewById(R.id.password);

        btnLoign = (Button) findViewById(R.id.login);
    }
}
