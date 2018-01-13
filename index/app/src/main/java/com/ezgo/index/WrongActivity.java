package com.ezgo.index;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.uploadLogAsyncTask;

public class WrongActivity extends AppCompatActivity {

    static String error = "";
    static String witchBlock = "";
    static String activityName = "";
    static String userName = "";
    static EditText mEditText ;
    static Button mBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong);
        mEditText = (EditText) findViewById(R.id.wrong_et);
        mBtn = (Button) findViewById(R.id.wrong_btn);


        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    uploadLog();
                    finish();
                }catch (Exception e){
                    finish();
                }
            }
        });

    }

    public void setError(String error, String witchBlock, String activityName){
        this.error = error;
        this.witchBlock = witchBlock;
        this.activityName = activityName;
    }

    public static void uploadLog(){
        userName = mEditText.getText().toString();
        //---------------------------上傳使用者的裝置ID及新增15筆record的紀錄
        uploadLogAsyncTask mUploadLogAsyncTask = new uploadLogAsyncTask(new uploadLogAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {

            }
        });
        mUploadLogAsyncTask.execute(Common.uploadLog,error,witchBlock,activityName, userName);
    }
}