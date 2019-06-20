package com.edu.sicnu.cs.zzy.iot_android.MQTT;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.edu.sicnu.cs.zzy.iot_android.Login.LoginActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MyMQttService extends Service {
    public final String TAG = MyMQttService.class.getSimpleName();

    private static MqttAndroidClient mqttAndroidClient;
    public static MqttAsyncClient mqttAsyncClient;
    private MqttConnectOptions mMqttConnectOptions;
    public        String HOST           = "tcp://119.23.61.148:61613"; //服务器地址（协议+地址+端口号）
    public        String USERNAME       = "admin"; //用户名
    public        String PASSWORD       = "password"; //密码
    public static String PUBLISH_TOPIC  = "client_conversation"; //发布主题
    public static String SUBSCRIBE_TOPIC ; //订阅主题
    public String CLIENTID ; //客户端ID，一般以客户端唯一标识符表示，这里初始化时用设备序列号表示

    private boolean isfirst;


    public MyMQttService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isfirst = true;
        init();
        doClientConnection();


    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if(isfirst){
//            try {
//                mqttAsyncClient.subscribe("ZZY_conversation",1);
//                isfirst = false;
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
//        }
        int type = intent.getIntExtra("type",-1);
        switch (type){
            case 0:
                //登录
                String strjson = intent.getStringExtra("data");
                publish(strjson);   //发布

                break;
            case 1:
                //

                break;
            case 2:
                //发送控制命令

                break;
            case 3:
                //修改密码

                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 发布 （模拟其他客户端发布消息）
     *
     * @param message 消息
     */
    public static void publish(String message) {
        String topic = PUBLISH_TOPIC;
        Integer qos = 1;
        Boolean retained = false;
        byte[] test = message.getBytes();
        MqttMessage mqttmessage = new MqttMessage();
        mqttmessage.setPayload(message.getBytes());

        try {
            if(mqttAsyncClient.isConnected()){
                mqttAsyncClient.publish(topic, test,qos,retained);
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订阅
     */
    public void subTopic(String[] Topic, int[] Qos){
        try {
            mqttAsyncClient.subscribe(Topic,Qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
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


    //MQTT是否连接成功的监听
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
//            try {
//                mqttAsyncClient.subscribe("ZZY_conversation",1);
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
            String[] topic = {"ZZY_conversation",CLIENTID+"_conversation"};
            int[] qos = {1,1};
            subTopic(topic,qos);

        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
        }
    };

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            Log.i(TAG, "收到消息： " + new String(message.getPayload()));
            String data = new String(message.getPayload());
            HashMap<String, Object> hashMap= JSON.parseObject(data, HashMap.class);
            String result = (String) hashMap.get("type");
            if(result.equals("login_return")){
                sendMessage_login(hashMap);
            }else if(result.equals("dashboard")){
                sendMessage_info(hashMap);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.i(TAG, "成功发送消息 ");
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开 ");
        }
    };



    /**
     * 初始化
     */
    private void init() {
        String serverURI = HOST; //服务器地址（协议+地址+端口号）
        CLIENTID = getSerialNumber();
        try {
            mqttAsyncClient = new MqttAsyncClient(serverURI, CLIENTID,new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }

        /**
         *
         * mqttAndroidClient = new MqttAndroidClient(MyMQttService.this, HOST, CLIENTID,new MemoryPersistence());
         * mqttAndroidClient.setCallback(mqttCallback);
         * 使用以上方法无法连接到服务器
        */
        //mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), HOST, CLIENTID);

        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setAutomaticReconnect(true); //断开后，是否自动连接
        mMqttConnectOptions.setCleanSession(false); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒
        mMqttConnectOptions.setKeepAliveInterval(20); //设置心跳包发送间隔，单位：秒
        mMqttConnectOptions.setUserName(USERNAME); //设置用户名
        mMqttConnectOptions.setPassword(PASSWORD.toCharArray()); //设置密码
        mMqttConnectOptions.setMqttVersion(3);

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic = PUBLISH_TOPIC;
        Integer qos = 1;
        Boolean retained = false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mMqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            //doClientConnection();
        }
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection() {
        if (!mqttAsyncClient.isConnected() && isConnectIsNomarl()) {
            try {
                mqttAsyncClient.setCallback(mqttCallback); //设置监听订阅消息的回调

                mqttAsyncClient.connect(mMqttConnectOptions, null, iMqttActionListener);

                mqttAsyncClient.setCallback(mqttCallback); //设置监听订阅消息的回调

//                mqttAndroidClient.connect(mMqttConnectOptions,getApplicationContext(),iMqttActionListener);
//                mqttAndroidClient.setCallback(mqttCallback);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) { //info.isAvailable()已过时
            String name = info.getSubtypeName(); //info.getTypeName()已过时
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    doClientConnection();
//                }
//            }, 3000);
            return false;
        }
    }

    /**
     * 将接受到的消息发送出去（登录界面）
     * @param hashMap
     */
    private void sendMessage_login(HashMap<String, Object> hashMap){
        Intent intent = new Intent(LoginActivity.Broad);
        intent.putExtra("data",hashMap);
        sendBroadcast(intent);
    }

    /**
     * 将接受到的消息发送出去（消息界面）
     * @param hashMap
     */
    private void sendMessage_info(HashMap<String, Object> hashMap){
        Intent intent = new Intent("zcd.voicerobot");
        intent.putExtra("data",hashMap);
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        try {
            mqttAsyncClient.disconnect(); //断开连接
            //mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();

    }

}
