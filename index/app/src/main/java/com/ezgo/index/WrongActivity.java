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
    static String whichBlock = "";
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

        Toast.makeText(WrongActivity.this,R.string.check_network,Toast.LENGTH_SHORT).show();

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    uploadLog();
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                    finish();
                }
            }
        });

    }

    public void setError(String error, String witchBlock, String activityName){
        this.error = error;
        this.whichBlock = witchBlock;
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
        mUploadLogAsyncTask.execute(Common.uploadLog,error,whichBlock,activityName, userName,String.valueOf(getWorksheet.getUser_id()));
    }
}
