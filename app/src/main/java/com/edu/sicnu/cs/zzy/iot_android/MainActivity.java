package com.edu.sicnu.cs.zzy.iot_android;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.edu.sicnu.cs.zzy.iot_android.Fragment.Fragment_1;
import com.edu.sicnu.cs.zzy.iot_android.Fragment.Fragment_2;
import com.edu.sicnu.cs.zzy.iot_android.Fragment.Fragment_3;
import com.edu.sicnu.cs.zzy.iot_android.MQTT.MyMQttService;
import com.edu.sicnu.cs.zzy.iot_android.Utils.ActivityCollector;

import org.json.JSONObject;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Fragment fragment1,fragment2,fragment3;
    private Fragment currentFragment=null;
    FragmentManager fragmentManager;
    private boolean isQuit = false;


    //设置bottomNavigationView的点击方法
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_info:
                switchFragment(fragment1);
                break;
            case R.id.navigation_message:
                switchFragment(fragment2);
                break;
            case R.id.navigation_mine:
                switchFragment(fragment3);
                break;
            case R.id.navigation_exit:
                appExit(this);
                break;
        }

        navigationView.setCheckedItem(item.getItemId());
//        MenuItem menuItem = bottomNavigationView.getMenu().findItem(item.getItemId());
//        menuItem.setChecked(true);  //当侧边栏选中菜单项跳转时使底部导航栏状态也随之改变
        drawerLayout.closeDrawers();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.navigation_monitor:
                Intent intent = new Intent(this,VideoActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCollector.addActivity(this);
        //Marked:改变状态栏颜色
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBlank));

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //右上角图标
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_stat_name_toolbar_right));

        //左上角按钮
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_stat_name_mine);
        }



        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_message);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setCheckedItem(R.id.navigation_message);
        navigationView.setNavigationItemSelectedListener(this);

        //实例化fragment
        fragment1 = new Fragment_1();
        fragment2 = new Fragment_2();
        fragment3 = new Fragment_3();

        switchFragment(fragment2);  //将第二个fragment放在前面

        ;



    }

    /**
     *Toolbar右上角按钮
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_navigation,menu);
        return true;
    }

    /**
     * 使Toolbar的popumenu中有图标
     * @param view
     * @param menu
     * @return
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    //利用反射获取私有方法
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }


    /**
     * 切换Fragment
     * @param fragment
     */
    public void switchFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        if (currentFragment != fragment){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(currentFragment!=null){
                transaction.hide(currentFragment);    //将原先的fragment隐藏
            }
            currentFragment = fragment; //替换当前fragment
            if(!fragment.isAdded()){
                transaction.add(R.id.frame,fragment);   //如未加入则加入
            }
            if(fragment == fragment2){
                //bundle.putSerializable("array",musiclist);
                //fragment.setArguments(bundle);
            }
            transaction.show(fragment);
            transaction.commit();
        }
    }


    /**
     * 退出APP
     * @param context
     */
    public void appExit(Context context){
        try{
            ActivityCollector.finishAll();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send(String string){
        Intent intent = new Intent(this, MyMQttService.class);
        intent.putExtra("type","2");
        intent.putExtra("data",string);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isQuit) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            isQuit = true;

            //这段代码意思是,在两秒钟之后isQuit会变成false
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        isQuit = false;
                    }
                }
            }).start();
        } else {
            //System.exit(0);
            appExit(this);
        }
    }
}
