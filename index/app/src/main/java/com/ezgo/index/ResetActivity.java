package com.ezgo.index;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.NavigationAsyncTask;
import com.ezgo.index.MyAsyncTask.getterAsyncTask;

public class ResetActivity extends AppCompatActivity {

    private Button btn_continue;
    private Button btn_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        btn_continue = (Button)findViewById(R.id.btn_continue);
        btn_reset= (Button)findViewById(R.id.btn_reset);

        btn_continue.setOnClickListener(new View.OnClickListener() { //繼續遊玩
            @Override
            public void onClick(View v) {
                toLoading();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() { //重新開始
            @Override
            public void onClick(View v) {
                resetRecord();
            }
        });



    }
    //----------------------------------------------------
    private void resetRecord(){
        getterAsyncTask myResetAsyncTask = new getterAsyncTask(new getterAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
            }
        });
        if(!myResetAsyncTask.isCancelled()) {
            //執行上傳動作
            myResetAsyncTask.execute(Common.resetRecord + getWorksheet.getUser_id());//上傳給userID給伺服器清除紀錄
            //Log.v("user_id:", user_id + "question[index]:"+ question_id[index] +"Ans:" +Ans+ "index" + index );  //question_id[index]標註這題是哪一題, Ans為答案是否正確
        } else {
            //Toast.makeText(ResetActivity.this, "The connection has been canceled", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent();

        intent.setClass(ResetActivity.this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }

    //-------------------------------------------------------------------------------------------------
    private void toLoading(){
        Intent intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putString("from", "ResetActivity");
        intent.putExtras(bundle);
        //----------------------------
        intent.setClass(ResetActivity.this, LoadingActivity.class);
        startActivity(intent);
        finish();
    }
}