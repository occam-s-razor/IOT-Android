package com.edu.sicnu.cs.zzy.iot_android.Fragment;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.edu.sicnu.cs.zzy.iot_android.MQTT.MyMQttService;
import com.edu.sicnu.cs.zzy.iot_android.R;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_2 extends Fragment {
    private static final String TAG = "Fragment_2";
    private MyReceiver Receiver;
    private TextView tv_temperature;
    private TextView tv_huidity;
    private TextView tv_illuminunce;
    private Switch switch_led_1;
    private Switch switch_led_2;
    private Intent intent;
    final HashMap<String, Object> hashMap = new HashMap<String, Object>();
    final HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
    String ID;

    public Fragment_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_2, container, false);
        Bundle bundle = this.getArguments();


        //注册 广播接收器
        Receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("zcd.voicerobot");
        getActivity().registerReceiver(Receiver, filter);


        tv_temperature = view.findViewById(R.id.txt_temperature);
        tv_huidity = view.findViewById(R.id.txt_huidity);
        tv_illuminunce = view.findViewById(R.id.txt_illuminunce);
        switch_led_1 = view.findViewById(R.id.switch_led_1);
        switch_led_2 = view.findViewById(R.id.switch_led_2);

        //switch_led_1.setChecked(true);
        //switch_led_2.setChecked(false);

        //intent = new Intent(getActivity(), MyMQttService.class);
        ID = getSerialNumber();
        switch_led_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(MainActivity.this, ""+isChecked , Toast.LENGTH_LONG).show();
                if (isChecked == true) {
                    //开灯
                    hashMap.put("type", "gateway_control");
                    hashMap.put("client_id", getSerialNumber());
                    hashMap.put("light1", "true");
                    try {
                        MyMQttService.mqttAsyncClient.publish("client_conversation", new JSONObject(hashMap).toString().getBytes(), 1, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    hashMap.clear();
                } else {
                    //关灯
                    hashMap.put("type", "gateway_control");
                    hashMap.put("client_id", getSerialNumber());
                    hashMap.put("light1", "false");
                    try {
                        MyMQttService.mqttAsyncClient.publish("client_conversation", new JSONObject(hashMap).toString().getBytes(), 1, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    hashMap.clear();
                }
            }
        });

        switch_led_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    //开灯
                    hashMap2.put("type", "gateway_control");
                    hashMap2.put("client_id", getSerialNumber());
                    hashMap2.put("light2", "true");
                    try {
                        MyMQttService.mqttAsyncClient.publish("client_conversation", new JSONObject(hashMap2).toString().getBytes(), 1, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    hashMap2.clear();
                } else {
                    //关灯
                    hashMap2.put("type", "gateway_control");
                    hashMap2.put("client_id", getSerialNumber());
                    hashMap2.put("light2", "false");
                    try {
                        MyMQttService.mqttAsyncClient.publish("client_conversation", new JSONObject(hashMap2).toString().getBytes(), 1, false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    hashMap2.clear();
                }
            }
        });
        return view;
    }


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

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                HashMap<String, Object> hashMap = (HashMap<String, Object>) intent.getSerializableExtra("data");
                //JSON.parseObject(message, HashMap.class);
                JSONObject jsonObject = new JSONObject(hashMap);
                //HashMap<String,String> result = (HashMap<String,String>)hashMap.get("sensors_value");
                JSONObject jsonObject1;
                jsonObject1 = jsonObject.getJSONObject("sensors_value");
                tv_temperature.setText(jsonObject1.getString("temperature")+" ℃");
                tv_huidity.setText(jsonObject1.getString("humidity")+" %RH");
                tv_illuminunce.setText(jsonObject1.getString("beam")+" Lux");
                switch_led_1.setChecked(jsonObject1.getBoolean("light1"));
                switch_led_2.setChecked(jsonObject1.getBoolean("light2"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
