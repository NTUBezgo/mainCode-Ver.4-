package com.ezgo.index;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
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
    public String server_vision_no;
    public String userDone;
    public static String nowLanguage;

    private ImageView imageView;

    //----------判斷使用者有沒有作答過題目
    int doneChk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String deviceName = Build.MANUFACTURER +"_"+ Build.DEVICE ;

        context = this;

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        WorksheetActivity.postCount(4);
        //取得目前系統語言
        getWorksheet.setLanguage(nowLanguage = getResources().getConfiguration().locale.toString());

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

                    for (int i = 0 ; i < jsonArray.length(); i++){
                        getWorksheet.postRecordDone(jsonArray.getJSONObject(i).getString("record_done"),i);
                        if((jsonArray.getJSONObject(i).getString("record_done").equals("1") )){
                            doneChk++;
                        };
                    }
                    jsonArray = object.getJSONArray("vision");
                    server_vision_no = jsonArray.getJSONObject(0).getString("vision_no");
                    //-----------------------------------------取得目前版本----------------------------------
                    try {
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        String myVersionName = packageInfo.versionName;
                        //Log.v("vision_no",server_vision_no);
                        if(!myVersionName.equals(server_vision_no)){ //若目前版本不是最新版本
                            go2googleplay();
                        }else{
                            //---- ....... ----
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
                        WrongActivity mWrontAct = new WrongActivity();
                        String witchWrongBlock = "版本型號出錯";

                        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

                        mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

                        Intent intent = new Intent();
                        intent.setClass(NavigationActivity.this, WrongActivity.class);
                        startActivity(intent);

                        finish();
                    }
                    jsonArray = object.getJSONArray("userID");
                    user_id = jsonArray.getJSONObject(0).getString("user_id");
                    getWorksheet.postUser_id(user_id);
                    jsonArray = object.getJSONArray("userDone");
                    userDone = jsonArray.getJSONObject(0).getString("user_done");
                    getWorksheet.postUserDone(userDone);
                    //---------------------------跳轉頁面
                    if(userDone.equals("1")){//跳轉至重新遊玩頁面
                        mHandler.sendEmptyMessageDelayed(GOTO_RESET_ACTIVITY, 3000); //秒跳轉
                    }else{
                        if(doneChk >0) {
                            mHandler.sendEmptyMessageDelayed(GOTO_LOADING_ACTIVITY, 3000); //秒跳轉
                        }
                        else{
                            mHandler.sendEmptyMessageDelayed(GOTO_GUIDE_ACTIVITY, 3000); //秒跳轉
                        }
                    }

                }catch(Exception e){
                    //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
                    WrongActivity mWrontAct = new WrongActivity();
                    String witchWrongBlock = "updateUser data";

                    ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

                    mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

                    Intent intent = new Intent();
                    intent.setClass(NavigationActivity.this, WrongActivity.class);
                    startActivity(intent);

                    finish();
                }
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
        myNavigationAsyncTask.execute(Common.updateUserUrl, getId, deviceName); //第一個參數是Common的網址,第二個是要上傳的值
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
                    //intent.setClass(NavigationActivity.this, LoadingActivity.class);
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

    //--------------------------------------------------------更新版本dialog-------------------------------------------------
    private void go2googleplay(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.check_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.check_update), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final String appPackageName = context.getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        builder.setNegativeButton(getString(R.string.check_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}