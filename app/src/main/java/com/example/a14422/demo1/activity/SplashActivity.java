package com.example.a14422.demo1.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.example.a14422.demo1.R;
import com.example.a14422.demo1.util.NumberUtil;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private final int SPLASH_DISPLAY_LENGHT = 1400;

    private static int liteCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        scaleImage(this, findViewById(R.id.splash_image), R.drawable.ic_splash_demo);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                liteCount++;
                Log.e("LiteCount",String.valueOf(liteCount));
                if (liteCount == 2){
                    startMainActivity();
                }
            }
        };
        if (!this.getDatabasePath("demo.db").exists()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NumberUtil.CreateSQLite(0,200);
                    handler.sendEmptyMessage(0);
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NumberUtil.CreateSQLStock();
                    handler.sendEmptyMessage(0);
                }
            }).start();
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMainActivity();
                }
            }, SPLASH_DISPLAY_LENGHT);
        }
    }

    public static void scaleImage(final Activity activity, final View view, int drawableResId) {
        // 获取屏幕的高宽
        Point outSize = new Point();
        activity.getWindow().getWindowManager().getDefaultDisplay().getSize(outSize);
        // 解析将要被处理的图片
        Bitmap resourceBitmap = BitmapFactory.decodeResource(activity.getResources(), drawableResId);
        if (resourceBitmap == null) {
            return;
        }
        // 开始对图片进行拉伸或者缩放
        // 使用图片的缩放比例计算将要放大的图片的高度
//        int bitmapScaledHeight = Math.round(resourceBitmap.getHeight() * outSize.x * 1.0f / resourceBitmap.getWidth());
        int bitmapScaledHeight = Math.round(resourceBitmap.getWidth() * outSize.x * 1.0f / outSize.y);
        // 以屏幕的宽度为基准，如果图片的宽度比屏幕宽，则等比缩小，如果窄，则放大
        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(resourceBitmap, resourceBitmap.getWidth(), bitmapScaledHeight, false);
        /*view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //这里防止图像的重复创建，避免申请不必要的内存空间
                if (scaledBitmap.isRecycled())
                    //必须返回true
                    return true;
                // 当UI绘制完毕，我们对图片进行处理
                int viewHeight = view.getMeasuredHeight();
                // 计算将要裁剪的图片的顶部以及底部的偏移量
                int offset = Math.abs(scaledBitmap.getHeight() - viewHeight) / 2;
                // 对图片以中心进行裁剪，裁剪出的图片就是非常适合做引导页的图片了
                Bitmap finallyBitmap = Bitmap.createBitmap(scaledBitmap, 0, offset, scaledBitmap.getWidth(), scaledBitmap.getHeight() - offset * 2);
                if (!finallyBitmap.equals(scaledBitmap)) {//如果返回的不是原图，则对原图进行回收
                    scaledBitmap.recycle();
                    System.gc();
                }
                // 设置图片显示
                view.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), finallyBitmap));
                return true;
            }
        });*/
        view.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), scaledBitmap));
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
