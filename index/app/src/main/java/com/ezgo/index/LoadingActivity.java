package com.ezgo.index;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoadingActivity extends AppCompatActivity {

    private String getFrom;
    private static final int GOTO_MAIN_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

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
