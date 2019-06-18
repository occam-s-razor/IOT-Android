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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.sicnu.cs.zzy.iot_android.Login.LoginActivity;
import com.edu.sicnu.cs.zzy.iot_android.MQTT.MyMQttService;

public class MainActivity extends AppCompatActivity {
    private TextView tv_temperature;
    private TextView tv_huidity;
    private TextView tv_illuminunce;
    private Switch switch_led_1;
    private Switch switch_led_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_temperature = findViewById(R.id.txt_temperature);
        tv_huidity = findViewById(R.id.txt_huidity);
        tv_illuminunce = findViewById(R.id.txt_illuminunce);
        switch_led_1 = findViewById(R.id.switch_led_1);
        switch_led_2 = findViewById(R.id.switch_led_2);

        switch_led_1.setChecked(true);
        switch_led_2.setChecked(false);

        switch_led_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(MainActivity.this, ""+isChecked , Toast.LENGTH_LONG).show();
                if(isChecked==true){
                    //开灯
                }else{
                    //关灯
                }
            }
        });

        switch_led_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked==true){
                    //开灯
                }else{
                    //关灯
                }
            }
        });














    }

    public void btn_connect(View v){

    }
}
