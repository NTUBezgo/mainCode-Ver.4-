package com.ezgo.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ezgo.index.MyAsyncTask.*;
import com.ezgo.index.Common.Common;

public class WorksheetActivity extends AppCompatActivity {
    //---------每隻動物最多有3題題目
    private static final int maxQuestionLength = 3;
    //---------使用者的資料
    private static String[] question = new String[100];
    private static String[] description = new String[100];
    private static String[] option = new String[100];
    private static String[] question_id = new String[100];
    private static byte[] token = new byte[100];
    private static int[] optionCount = new int[45];
    //---------取得字串的長度上限
    private static int worksheetLength = 0;
    //---------目前題目在第0題，要從地理圍欄傳入該動物是哪隻動物(詳見postIndex)
    private static int index = 0;
    //---------判斷使用者是否有點下正確答案的radioButton
    private static byte Ans = 2;
    //---------判斷使用者已經回答幾題
    private int count = 0;

    int a ;
    RadioButton[] tv = new RadioButton[5];
    private int user_id;
    public Button mBtnChk;
    public Button mBtnResume;
    private RadioGroup rg;
    private TextView textView;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet);
        //-------------
       // getWorksheet.postUser_id("2");
        //-------------連接資料庫來取得值，不需要更改
        getWorksheet.getJSON();
        //------------------取得使用者ID
        user_id =getWorksheet.getUser_id();
        //------------------
        image = (ImageView) findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.wolf_env);
        textView = (TextView) findViewById(R.id.title);
        textView.setTextSize(23.0f);
        mBtnChk = (Button)findViewById(R.id.btnChk);
        mBtnResume = (Button)findViewById(R.id.btnResume);

        getData();
    }

    protected void onStart(){
        super.onStart();

        rg  = (RadioGroup) findViewById(R.id.rg);


        //---------------監聽"確定"的按鈕
        mBtnChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (v.getId() == mBtnChk.getId()) {
                   //-------判斷使用者是否有選擇正確的答案
                   if (!checkToken(index)) {
                       if (Ans == 1) {
                           //進行上傳動作
                           updateAns();
                           showNextQuest();
                           Toast.makeText(WorksheetActivity.this, "恭喜您答對了！", Toast.LENGTH_SHORT).show();
                       } else if(Ans ==0){
                           showNextQuest();
                           updateAns();
                           Toast.makeText(WorksheetActivity.this, "您答錯囉", Toast.LENGTH_SHORT).show();
                       }else{
                           Toast.makeText(WorksheetActivity.this, "您還沒選擇答案喔！", Toast.LENGTH_SHORT).show();
                       }
                   } else {
                       showNextQuest();
                       Toast.makeText(WorksheetActivity.this, "這題答過囉，...", Toast.LENGTH_SHORT).show();
                   }
               }
                if(count == 15){
                    Intent intent = new Intent();
                    //將原本Activity的換成MainActivity
                    intent.setClass(WorksheetActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
           }
        });
        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });
    }

    protected void onResume() {
        super.onResume();

        mBtnChk = (Button)findViewById(R.id.btnChk);
        getData();
    }

    protected void showNextQuest(){
        count += 1;

        if(count != maxQuestionLength){
            //------------顯示下一題
            index += optionCount[count-1];
            Log.v("index ",index + "");
            Ans = 0;
            showData();
        }
        else{
            //-------------在這裡撰寫回首頁的程式碼
            //index = 0;
            //count =0;
            //Toast.makeText(WorksheetActivity.this,"答題結束" , Toast.LENGTH_SHORT).show();
        }
    }
    //連接資料庫進行上傳
    private void updateAns(){
        worksheetUpdateAsyncTask myNavigationAsyncTask = new worksheetUpdateAsyncTask(new worksheetUpdateAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
            }
        });
        if(!myNavigationAsyncTask.isCancelled()) {
            //執行上傳動作
            myNavigationAsyncTask.execute(Common.updateAnsUrl + user_id , question_id[index] ,Ans+"" );  //question_id[index]標註這題是哪一題, Ans為答案是否正確
        } else {
            Toast.makeText(WorksheetActivity.this, "連線已取消", Toast.LENGTH_SHORT).show();
        }
    }

    /*------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    在地理圍欄中，寫入postIndex(n); n的範圍是0~4分別是作答的五隻動物
    0 = 第一題 1 = 第四題 2 = 第七題 3 = 第十題 4 = 第十三題
    ------------------*/
    private void postCount(int count){
        this.count += count * 3   ;
    }

    protected boolean checkToken(int i){
        if(getWorksheet.getToken(i) == 1){
            return true;
        }else{
            return false;
        }
    }


    //-------------------取得這位使用者的問答題
    private void getData() {
        worksheetLength = getWorksheet.getWorksheetLength();

        for(int i = 0; i < 15 ; i++){
            optionCount[i] = getWorksheet.getOptionArr(i);
            Log.v("optionCount",optionCount[i] + "");
        }
        for (int i = 0; i < worksheetLength; i++) {
            question[i] = getWorksheet.getQuestion(i);
            description[i] = getWorksheet.getDescription(i);
            option[i] = getWorksheet.getOption(i);
            question_id[i] = getWorksheet.getQuestion_id(i);
            token[i] = getWorksheet.getToken(i);
            Log.v("optionCount",question[i] + "");
          //  Log.v("description",description[i] + "");
            Log.v("option",option[i] + "");
          //  Log.v("question_id",question_id[i] + "");
          //  Log.v("token",token[i] + "");
        }


        //-----------取得每一題有幾個選項
            //optionCount[i] = getWorksheet.getOptionArr(i);
            //

        showData();
    }
    //-------------------顯示問答題內容
    private void showData(){
        if (option[index] != null) {
            //設定題目的文字
            textView.setText(question[index]);
            //顯示確認按鈕
            mBtnChk.setVisibility(View.VISIBLE);
            mBtnResume.setVisibility(View.GONE);

            Log.v("optionCount[count] :" , optionCount[count] +"");

            rg.removeAllViews();
            for (int i = 0; i < optionCount[count]; i++) {
                tv[i] = new RadioButton(this);
                tv[i].setId(1 + i);             //id = 1,2,3
                tv[i].setText(option[index + i]);
                tv[i].setTextSize(17.0f);
                tv[i].setHeight(120);
                tv[i].setOnClickListener(mOnClickListener);

                rg.addView(tv[i]);
            }
        } else {
            mBtnChk.setVisibility(View.GONE);
            mBtnResume.setVisibility(View.VISIBLE);
            textView.setText("請重新整理頁面！");
        }
    }

    //建立監聽器，判斷使用者點擊哪一個按鈕
    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("rgID " ,rg.getCheckedRadioButtonId() +"");
            a = rg.getCheckedRadioButtonId();
            switch(a){
                case 1:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;

                    }else {
                        Ans = 0;
                    }
                    break;
                case 2:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    break;
                case 3:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    // Toast.makeText(WorksheetActivity.this,"id:" +rg.getCheckedRadioButtonId() + "Ans: "+ Ans , Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    // Toast.makeText(WorksheetActivity.this,"id:" +rg.getCheckedRadioButtonId() + "Ans: "+ Ans , Toast.LENGTH_SHORT).show();
                    break;
                default :
                    Ans =2;
            }
            a = 0;
        }
    };
}
