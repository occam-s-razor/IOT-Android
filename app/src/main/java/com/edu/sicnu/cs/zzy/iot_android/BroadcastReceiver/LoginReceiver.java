package com.edu.sicnu.cs.zzy.iot_android.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.edu.sicnu.cs.zzy.iot_android.Login.LoginActivity;

/**
 * Created by Me-262-SM on 2019/6/16.
 * Email：zzylikegirls@163.com
 * Version：v1.0
 */
public class LoginReceiver extends BroadcastReceiver {
    LoginActivity activity;

    public LoginReceiver() {

    }

    public LoginReceiver(LoginActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
