package com.ezgo.index;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class LoadingActivity extends AppCompatActivity {

    private String getFrom;
    private static final int GOTO_MAIN_ACTIVITY = 0;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //---------------設定幀動畫--------------------
        ImageView mImageViewFilling = (ImageView) findViewById(R.id.loadingDog);
        ((AnimationDrawable) mImageViewFilling.getBackground()).start();

        Bundle bundle=getIntent().getExtras();
        getFrom=bundle.getString("from");

        if(getFrom.equals("GuideActivity")||getFrom.equals("NavigationActivity")){
            mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 1000); //1秒跳轉
        }

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };

}
