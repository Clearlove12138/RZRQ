package com.example.a14422.demo1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.example.a14422.demo1.R;
import com.example.a14422.demo1.fragment.ChartFragment;
import com.example.a14422.demo1.fragment.TableFragment;

import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.config.Constants;
import me.dkzwm.widget.srl.extra.header.MaterialHeader;


public class ShowActivity extends AppCompatActivity {

    private SmoothRefreshLayout mRefreshLayout;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        mRefreshLayout = findViewById(R.id.smoothRefreshLayout_show);
        mHandler = new Handler();
        initFragment();
        initSmoothRefreshLayout();
    }

    private void initSmoothRefreshLayout() {
        mRefreshLayout.setHeaderView(new MaterialHeader(this));
        mRefreshLayout.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshing() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.refreshComplete(800);
                    }
                }, 1000);
            }
        });
        mRefreshLayout.setDisableLoadMore(false);
        mRefreshLayout.setDisablePerformLoadMore(true);
        mRefreshLayout.setEnableOldTouchHandling(false);
//        mRefreshLayout.getFooterView().getView().setVisibility(View.GONE);
        mRefreshLayout.setSpringInterpolator(new OvershootInterpolator(3f));
        mHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.autoRefresh(Constants.ACTION_NOTIFY, true);
                    }
                },
                100);
    }

    private void initFragment() {
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.show_toolbar);
        String data = intent.getStringExtra("extra_data");
        if (data != null && data.length() > 0) {
            switch (data) {
                case "for_chart":
                    toolbar.setTitle("Chart");
                    replaceFragment(new ChartFragment());
                    break;
                case "for_table":
                    toolbar.setTitle("Table");
                    replaceFragment(new TableFragment());
                    break;
                default:
                    break;
            }
        }
        String scode = intent.getStringExtra("extra_code");
        String sname = intent.getStringExtra("extra_name");
        if (scode != null && scode.length() > 0 && sname != null && sname.length() > 0) {
            toolbar.setTitle(sname+":"+scode);
            replaceFragment(ChartFragment.newInstance(scode,sname));
            Log.e("ShowActivity", scode);
        }
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_show, fragment);
        transaction.commit();
    }

}
