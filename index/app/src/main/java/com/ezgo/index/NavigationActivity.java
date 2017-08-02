package com.ezgo.index;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.NavigationAsyncTask;
import com.ezgo.index.MyAsyncTask.worksheetAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.ezgo.index.getWorksheet.getWorksheetLength;


public class NavigationActivity extends Activity {

    Context context;
    String getId;
    public static String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_navigation);

        //預設字型
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/wp010-08.ttf");

        //---------------------------上傳使用者的裝置ID及新增15筆record的紀錄
        NavigationAsyncTask myNavigationAsyncTask = new NavigationAsyncTask(new NavigationAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                try{
                    if(result==null){
                        Toast.makeText(context, "請檢查網路是否有開啟", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("result");

                    user_id = jsonArray.getJSONObject(0).getString("user_id");
                    Log.v("user_id :" , user_id);
                }catch(Exception e){}
            }
        });

        if(!myNavigationAsyncTask.isCancelled()) {
            if(getId==null){
                getId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //取得Android ID
                if(getId.equals("9774d56d682e549c")){
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); //取得Device ID
                    getId = tm.getDeviceId();
                }
            }

            //------------------------------------------------------------
            //  傳給非同步工作的參數(主機網址, name, amount, deliverDate)
            //------------------------------------------------------------
            myNavigationAsyncTask.execute(Common.updateUserUrl, getId); //第一個參數是Common的網址,第二個是要上傳的值
        } else {
            Toast.makeText(context, "連線已取消", Toast.LENGTH_SHORT).show();
        }
        //---------------------------上傳結束
        //------------------------取得該名使用者的user_id
        //getWorksheet.postUser_id(user_id);
        getWorksheet.postUser_id("2");
       // getWorksheet.getJSON();


        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 1000); //1秒跳轉

    }

    //-------------------------------------------------------------------------------------------------
    private static final int GOTO_MAIN_ACTIVITY = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    //將原本Activity的換成MainActivity
                    intent.setClass(NavigationActivity.this, WorksheetActivity.class);
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
