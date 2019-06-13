package com.edu.sicnu.cs.zzy.iot_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.edu.sicnu.cs.zzy.iot_android.MQTT.MyMQttService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},0);
        }

        Intent intent_service = new Intent(this,MyMQttService.class);
        startService(intent_service);

    }

    public void btn_connect(View v){

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
}
