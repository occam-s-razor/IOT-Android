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



        //开启MQTT服务
//        Intent intent_service = new Intent(this,MyMQttService.class);
//        startService(intent_service);






    }

    public void btn_connect(View v){

    }
}
