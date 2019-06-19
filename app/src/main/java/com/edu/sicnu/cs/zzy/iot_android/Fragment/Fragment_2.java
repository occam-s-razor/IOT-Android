package com.edu.sicnu.cs.zzy.iot_android.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.edu.sicnu.cs.zzy.iot_android.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_2 extends Fragment {
    private static final String TAG = "Fragment_2";
    private TextView tv_temperature;
    private TextView tv_huidity;
    private TextView tv_illuminunce;
    private Switch switch_led_1;
    private Switch switch_led_2;
    private Intent intent;
    public Fragment_2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_2, container, false);
        Bundle bundle = this.getArguments();
        tv_temperature = view.findViewById(R.id.txt_temperature);
        tv_huidity = view.findViewById(R.id.txt_huidity);
        tv_illuminunce = view.findViewById(R.id.txt_illuminunce);
        switch_led_1 = view.findViewById(R.id.switch_led_1);
        switch_led_2 = view.findViewById(R.id.switch_led_2);

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

        return view;
    }


}
