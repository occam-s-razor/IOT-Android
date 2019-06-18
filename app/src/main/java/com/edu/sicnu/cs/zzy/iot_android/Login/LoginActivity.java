package com.edu.sicnu.cs.zzy.iot_android.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edu.sicnu.cs.zzy.iot_android.BroadcastReceiver.LoginReceiver;
import com.edu.sicnu.cs.zzy.iot_android.MQTT.MyMQttService;
import com.edu.sicnu.cs.zzy.iot_android.MainActivity;
import com.edu.sicnu.cs.zzy.iot_android.R;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {
    private EditText editText_usr;
    private EditText editText_psw;
    private Button Btn_login;
    private LoginReceiver loginReceiver;
    private HashMap<String,String> result = new HashMap<>();
    public boolean isSuccess;
    private static final String TAG = "LoginActivity";
    public static String Broad = "com.edu.sicnu.cs.zzy.IOT.loginReceiver";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Marked:隐藏状态栏（使其透明）*/
        View decorView = getWindow().getDecorView();
        //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        //设置状态栏颜色为透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //申请权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},0);
        }

        isSuccess = false;
        editText_usr = findViewById(R.id.edt_usr);
        editText_psw = findViewById(R.id.edt_psw);
        Btn_login = findViewById(R.id.btn_login);

        //注册广播监听
        loginReceiver = new LoginReceiver(LoginActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Broad);
        registerReceiver(loginReceiver,intentFilter);

    }
    //没有申请则一直请求
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},0);
            }
        }
    }


    public void btn_Login(View v){
        Btn_login.setEnabled(false);
        final HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("type","login");
        hashMap.put("client_id",getSerialNumber());
        HashMap<String, String> msg = new HashMap<String, String>();
        msg.put("username",editText_usr.getText().toString());
        msg.put("password",editText_psw.getText().toString());
        hashMap.put("msg",msg);

//        JSONArray jsonArray = new JSONArray();
//        jsonArray.put(hashMap);

        JSONObject jsonObject = new JSONObject(hashMap);
        String strjson = jsonObject.toString();
        Log.d(TAG, "btn_Login: "+strjson);

        Intent intent_service = new Intent(this, MyMQttService.class);
        intent_service.putExtra("type",0);
        intent_service.putExtra("data",strjson);
        startService(intent_service);

        //监听是否登录成功
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                for(int i =0;i<100;i++){
                    try {
                        Thread.sleep(30);
                        if(isSuccess){
                            msg.what = 1;
                            handler.sendMessage(msg);
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(!isSuccess){
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

//    public void setSuccess(boolean success) {
//        isSuccess = success;
//    }

    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
    @SuppressLint({"NewApi", "MissingPermission"})
    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "读取设备序列号异常：" + e.toString());
        }
        return serial;
    }


    /**
     * 处理线程发来的消息
     *
     */
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1://获取到what属性为1的message
                    //Toast.makeText(LoginActivity.this, "登录成功..." , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(LoginActivity.this, "连接服务器超时..." , Toast.LENGTH_LONG).show();
                    Btn_login.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };





    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginReceiver);
    }
}
