package com.ezgo.index;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.NavigationAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;


public class NavigationActivity extends Activity {

    Context context;
    String getId;
    public String user_id;
    public String recordDone;

    private ImageView imageView;

    //----------
    static boolean doneChk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        WorksheetActivity.postCount(3);

        setContentView(R.layout.activity_navigation);

        //------------------------------------------設定旺事如意logo動畫-----------------------------------------
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f
                , Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000); //時間
        scaleAnimation.setRepeatCount(0); //不重複
        imageView = (ImageView) findViewById(R.id.navDog);
        imageView.startAnimation(scaleAnimation);

        //---------------------------上傳使用者的裝置ID及新增15筆record的紀錄
        NavigationAsyncTask myNavigationAsyncTask = new NavigationAsyncTask(new NavigationAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                try{
                    if(result==null){
                        Toast.makeText(context, R.string.notice_network, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("result");
                    user_id = jsonArray.getJSONObject(0).getString("user_id");
                    recordDone = jsonArray.getJSONObject(0).getString("done");
                    getWorksheet.postUser_id(user_id);

                    for (int i = 0 ; i < jsonArray.length(); i++){
                        getWorksheet.postRecordDone(jsonArray.getJSONObject(i).getString("done"),i);
                        if((jsonArray.getJSONObject(i).getString("done").equals("1") )){
                            doneChk = true;
                        };
                    }

                    //Log.e("user_id :" , user_id);

                    //---------------------------跳轉頁面

                    if(doneChk){
                        mHandler.sendEmptyMessageDelayed(GOTO_LOADING_ACTIVITY, 3000); //秒跳轉
                    }else{
                        mHandler.sendEmptyMessageDelayed(GOTO_GUIDE_ACTIVITY, 3000); //秒跳轉
                    }
                    //mHandler.sendEmptyMessageDelayed(GOTO_RESET_ACTIVITY, 3000); //秒跳轉

                }catch(Exception e){}
            }
        });

        if(!myNavigationAsyncTask.isCancelled()) {
            if(getId==null){
                getId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //取得Android ID
                //Log.e("getId :" , getId);
                if(getId.equals("9774d56d682e549c")){
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); //取得Device ID
                    getId = tm.getDeviceId();
                }
            }
        } else {
            Toast.makeText(context, "The connection has been canceled", Toast.LENGTH_SHORT).show();
        }
        myNavigationAsyncTask.execute(Common.updateUserUrl, getId); //第一個參數是Common的網址,第二個是要上傳的值
        //---------------------------上傳結束

    }


    //-------------------------------------------------------------------------------------------------
    private static final int GOTO_LOADING_ACTIVITY = 0;
    private static final int GOTO_GUIDE_ACTIVITY = 1;
    private static final int GOTO_RESET_ACTIVITY = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            getWorksheet.getJSON();
            Intent intent = new Intent();
            switch (msg.what) {
                case GOTO_LOADING_ACTIVITY:     //若已經答過題-->跳至Loading頁
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "NavigationActivity");
                    intent.putExtras(bundle);
                    //----------------------------
                    intent.setClass(NavigationActivity.this, LoadingActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case GOTO_GUIDE_ACTIVITY:     //若尚未答過題-->跳至說明頁
                    intent.setClass(NavigationActivity.this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case GOTO_RESET_ACTIVITY:     //已答完題並兌換過獎品-->跳至是否重玩頁
                    intent.setClass(NavigationActivity.this, ResetActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }

    };
    //-------------------------------------------------------------------------------------------------
}
