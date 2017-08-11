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

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.worksheetUpdateAsyncTask;

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
    private static int[] worksheetImg = new int[5];
    //---------取得字串的長度上限
    private static int worksheetLength = 0;
    //---------目前題目在第0題，要從地理圍欄傳入該動物是哪隻動物(詳見postIndex)
    private static int index = 0;
    //---------判斷使用者是否有點下正確答案的radioButton
    private static byte Ans = 2;
    //---------判斷使用者已經回答幾題
    private static int count = 0;

    int a ;

    private int user_id;
    public Button mBtnChk;
    public Button mBtnResume;
    public RadioGroup rg;
    private TextView textView;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet);
        //-------------連接資料庫來取得值，不需要更改
        getWorksheet.getJSON();
        //------------------取得使用者ID
        user_id =getWorksheet.getUser_id();
        //------------------
        worksheetImg = MyData.getWorksheetImg();

        image = (ImageView) findViewById(R.id.imageView1);
        //判斷目前該顯示哪一張圖片
        switch (count){
            case 0:
                image.setImageResource(worksheetImg[0]);
                break;
            case 3:
                image.setImageResource(worksheetImg[1]);
                break;
            case 6:
                image.setImageResource(worksheetImg[2]);
                break;
            case 9:
                image.setImageResource(worksheetImg[3]);
                break;
            case 12:
                image.setImageResource(worksheetImg[4]);
                break;
        }
        rg  = (RadioGroup) findViewById(R.id.rg);
        textView = (TextView) findViewById(R.id.title);
        textView.setTextSize(23.0f);
        mBtnChk = (Button)findViewById(R.id.btnChk);
        mBtnResume = (Button)findViewById(R.id.btnResume);

        index +=count * 3 -2;

        getData();
    }

    protected void onStart(){
        super.onStart();

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
                            updateAns();
                            showNextQuest();
                            Toast.makeText(WorksheetActivity.this, "您答錯囉", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(WorksheetActivity.this, "您還沒選擇答案喔！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showNextQuest();
                        Toast.makeText(WorksheetActivity.this, "這題答過囉，...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //重新整理的按鈕
        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        //確認按鈕
        mBtnChk = (Button)findViewById(R.id.btnChk);
        getData();
    }

    protected void showNextQuest(){
        count += 1;

        //count=3等於使用者已回答三題，else表示使用者要回首頁了
        if(count != maxQuestionLength){
            //------------顯示下一題
            index += optionCount[count-1];
            Log.v("index" , index + "");
            Ans = 2;
            showData();
        }
        /*
        //測試用，可以顯示所有題目
        if(true){
            //------------顯示下一題
            index += optionCount[count-1];
           // Log.v("index ",index + "");
           // Log.v("count ",count-1 + "");
           // Log.v("count ",optionCount[count-1] + "");
            Ans = 2;
            showData();
        }
        */
        else{
            //-------------在這裡撰寫回首頁的程式碼
            Intent intent = new Intent();

            intent.setClass(WorksheetActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            index = 0;
            count =0;
            Toast.makeText(WorksheetActivity.this,"答題結束" , Toast.LENGTH_SHORT).show();
        }

    }

    public static boolean checkToken(int i){
        // Log.v("optionArr：",""+getWorksheet.getToken(i));
        if(getWorksheet.getToken(i) == 1){
            return true;
        }else{
            return false;
        }
    }


    //-------------------取得這位使用者的問答題
    private void getData() {
        worksheetLength = getWorksheet.getWorksheetLength();

        for(int i = 0; i < 15 ; i++) {
            optionCount[i] = getWorksheet.getOptionArr(i);
            Log.v("optionCount", +i + ":" + optionCount[i] + "");
        }
        for (int i = 0; i < worksheetLength; i++) {
            question[i] = getWorksheet.getQuestion(i);
            description[i] = getWorksheet.getDescription(i);
            option[i] = getWorksheet.getOption(i);
            question_id[i] = getWorksheet.getQuestion_id(i);
            token[i] = getWorksheet.getToken(i);

            //Log.v("optionCount",question[i] + "");
            //Log.v("description",description[i] + "");
            //Log.v("option",option[i] + "");
            //Log.v("question_id",question_id[i] + "");
            //Log.v("token",token[i] + "");
        }

        showData();
    }
    //-------------------顯示問答題內容
    private void showData(){


        if(rg != null){
            rg.removeAllViews();
        }
        if (option[index] != null) {
            //設定題目的文字
            textView.setText(question[index]);
            //顯示確認按鈕
            mBtnChk.setVisibility(View.VISIBLE);
            mBtnResume.setVisibility(View.GONE);
            RadioButton tv;
            Log.v("optionCount[count] :" , optionCount[count] +"");

            for (int i = 0; i < optionCount[count]; i++) {
                tv = new RadioButton(this);
                tv.setId(1 + i);             //id = 1,2,3
                tv.setText(option[index + i]);
                tv.setTextSize(17.0f);
                tv.setHeight(120);
                tv.setOnClickListener(mOnClickListener);

                rg.addView(tv);
            }
        } else {

            mBtnChk.setVisibility(View.GONE);
            mBtnResume.setVisibility(View.VISIBLE);
            textView.setText("請重新整理頁面！");
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
            //Log.v("user_id:", user_id + "question[index]:"+ question_id[index] +"Ans:" +Ans+ "index" + index );  //question_id[index]標註這題是哪一題, Ans為答案是否正確
        } else {
            Toast.makeText(WorksheetActivity.this, "連線已取消", Toast.LENGTH_SHORT).show();
        }
    }

    //建立監聽器，判斷使用者點擊哪一個按鈕
    private CompoundButton.OnClickListener mOnClickListener = new CompoundButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("rgID " ,rg.getCheckedRadioButtonId() +"");
            switch(rg.getCheckedRadioButtonId()){
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
            }
        }
    };

    /*------------------這裡的註解很重要 看不懂請提問
    在地理圍欄中，寫入postCount(n); n的範圍是0-4分別是作答的五隻動物
    0 = 第一題 1 = 第  四  題   2 = 第七題
    3 = 第十題 4 = 第 十三  題
    ------------------*/
    public static void postCount(int pCount){ count += pCount * 3 ; }

}