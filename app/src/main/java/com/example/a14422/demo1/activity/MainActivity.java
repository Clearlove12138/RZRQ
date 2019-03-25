package com.example.a14422.demo1.activity;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.a14422.demo1.R;
import com.example.a14422.demo1.entity.ScodeEntity;
import com.example.a14422.demo1.fragment.ChartFragment;
import com.example.a14422.demo1.fragment.TableFragment;
import com.example.a14422.demo1.service.MyService;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.litepal.LitePal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mFragmentPagerAdapter;
    private List<Fragment> mFragmentsList;

    protected ImageButton btn_chart;
    protected ImageButton btn_table;
    protected ImageButton btn_home;

    private static boolean isExit = false;
    private static Handler handler;

    private ServiceConnection serviceConnection;
    private MyService.DownloadBinder downloadBinder;

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    private MaterialSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_chart = findViewById(R.id.for_chart);
        setBtnListener(btn_chart);
        btn_table = findViewById(R.id.for_table);
        setBtnListener(btn_table);
        btn_home = findViewById(R.id.for_home);
        setBtnListener(btn_home);
        mViewPager = findViewById(R.id.main_viewpager);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                isExit = false;
            }
        };

        initDatas();
//        initServiceConnection();
    }

    private void initDatas() {
        mFragmentsList = new ArrayList<>();
        mFragmentsList.add(new ChartFragment());
        mFragmentsList.add(new TableFragment());

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentsList.get(i);
            }

            @Override
            public int getCount() {
                return mFragmentsList.size();
            }
        };
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                resetButtonColor();
                selectTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void selectTab(int i) {
        switch (i) {
            case 0:
                btn_chart.setImageResource(R.mipmap.linechart);
                break;
            case 1:
                btn_table.setImageResource(R.mipmap.tablet_fill);
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(i);
    }

    private void resetButtonColor() {
        btn_chart.setImageResource(R.mipmap.linechart);
        btn_home.setImageResource(R.mipmap.home);
        btn_table.setImageResource(R.mipmap.table);
    }

    private void setBtnListener(ImageButton button) {
        button.setOnClickListener(new myListener());
    }

    private void initServiceConnection(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (MyService.DownloadBinder)service;
                downloadBinder.sayHello();
                downloadBinder.startForeNotification("Five");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(this,MyService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);

//        downloadBinder.getNotificationManager().notify(1,downloadBinder.getNotification("Five"));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                isExit = true;
                handler.sendEmptyMessageDelayed(0, 2000);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_github,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
                    Bundle options = ActivityOptions.makeSceneTransitionAnimation(this,findViewById(R.id.action_search),"transition_search_back").toBundle();
                    Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                    startActivity(intent,options);
//                    startActivity(intent);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    class myListener implements View.OnClickListener {
        Intent intent;
        String data;

        @Override
        public void onClick(View v) {
            resetButtonColor();
            switch (v.getId()) {
                case R.id.for_chart:
                    data = "for_chart";
                    Toast.makeText(MainActivity.this, "ShowChart", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, ShowActivity.class);
                    intent.putExtra("extra_data", data);
//                    startActivity(intent);
                    selectTab(0);
                    break;
                case R.id.for_table:
                    data = "for_table";
                    Toast.makeText(MainActivity.this, "ShowTable", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, ShowActivity.class);
                    intent.putExtra("extra_data", data);
//                    startActivity(intent);
                    selectTab(1);
                    break;
                case R.id.for_home:
                    data = "for_home";
                    Toast.makeText(MainActivity.this, "ShowHome", Toast.LENGTH_SHORT).show();
                    btn_home.setImageResource(R.mipmap.home_fill);
//                    showMessagePositiveDialog();
                default:
                    break;
            }
        }
    }

    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("标题")
                .setMessage("确定要发送吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }
}
