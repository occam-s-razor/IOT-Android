package com.edu.sicnu.cs.zzy.iot_android.Utils;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Created by Me-262-SM on 2019/6/18.
 * Email：zzylikegirls@163.com
 * Version：v1.0
 */
public class ActivityCollector {
    public static LinkedList<Activity> activities = new LinkedList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
