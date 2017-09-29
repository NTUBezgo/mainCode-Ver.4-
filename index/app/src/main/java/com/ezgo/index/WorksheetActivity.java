package com.ezgo.index;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.worksheetUpdateAsyncTask;

public class WorksheetActivity extends AppCompatActivity {
    //---------每隻動物最多有3題題目
    private static final int maxQuestionLength = 3;
    //---------判斷傳進來的值是哪一張圖片
    private static int imgNo ;
    //---------使用者的資料
    private static String[] question = new String[100];
    private static String[] description = new String[100];
    private static String[] option = new String[100];
    private static String[] recordQuestion_id = new String[100];
    private static String[] optionQuestion_id = new String[100];
    private static String[] recordDone = new String[100];
    private static int[] worksheetImg = new int[5];
    private static String[] nowAnser = new String[5];
    //---------取得總共有幾個選項(每一題有多個選項)這裡指所有題目共有幾個選項
    private static int optionLength = 0;
    //---------取得總共有幾題題目
    private static int questionLength = 0;
    //---------目前題目在第0題，要從地理圍欄傳入該動物是哪隻動物(詳見postIndex) range:0-14
    private static int index = 0;
    //---------判斷使用者是否有點下正確答案的radioButton
    private static byte Ans = 2;
    //---------判斷使用者已經回答幾題range:0-2 當=3時會被重置為0
    private static int count = 0;
    //---------用於答對或答錯後顯示什麼提示訊息
    private static  String showCurrent = "恭喜您答對了";
    private static  String showFail = "很可惜答錯了";

    /*
    目前的邏輯：
    陣列長度：0 1 2 3 4 5 6 7 8 9 10 11 12 13 14ttttttttttttttttttttt
    陣列內容：1 1 1 2 2 3 3 3 4 4  4  4  5  5  6
    判斷 陣列長度的 0 跟1的陣列內容是否重複，重複代表是同一題。
    */
    //---------sign = 陣列長度 -1表示沒有值-->資料庫取值失敗
    private static int sign = -1;
    //---------判斷是否要顯示下一題 false代表不顯示, true代表停留在敘述的文字
    boolean showNext = false;

    ImageView imageTop;
    ImageView imageBottom;

    private int user_id;
    public Button mBtnChk;
    public Button mBtnResume;
    public Button mBtnNextquest;
    public RadioGroup rg;
    private TextView textView;
    private TextView textDescription;
    private TextView textHint;
    private LinearLayout textDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet);
        //-------------連接資料庫來取得值，不需要更改
        getWorksheet.getJSON();
        //------------------取得使用者ID
        user_id = getWorksheet.getUser_id();
        //------------------
        worksheetImg = MyData.getWorksheetImg();
        rg  = (RadioGroup) findViewById(R.id.rg);

        imageTop = (ImageView) findViewById(R.id.ImgViewTop);
        imageBottom = (ImageView) findViewById(R.id.ImgViewBottom);
        textView = (TextView) findViewById(R.id.title);
        textView.setTextSize(24.0f);
        textDescription = (TextView) findViewById(R.id.textDescription);
        textHint = (TextView) findViewById(R.id.textHint);
        textDescription.setTextSize(20.0f);
        textHint.setTextSize(22.0f);
        mBtnChk = (Button)findViewById(R.id.btnChk);
        mBtnResume = (Button)findViewById(R.id.btnResume);
        mBtnNextquest = (Button)findViewById(R.id.btnNextquest);

        textDetail = (LinearLayout) findViewById(R.id.textDetail);
        mBtnNextquest.setVisibility(View.GONE);
        textDetail.setVisibility(View.GONE);

        //判斷目前該顯示哪一張圖片
        switch (imgNo){
            case 0:
                imageBottom.setImageResource(worksheetImg[0]);
                imageTop.setVisibility(View.GONE);
                break;
            case 1:
                imageBottom.setImageResource(worksheetImg[1]);
                imageTop.setVisibility(View.GONE);

                break;
            case 2:
                imageBottom.setImageResource(worksheetImg[2]);
                imageTop.setVisibility(View.GONE);
                break;
            case 3:
                imageBottom.setImageResource(worksheetImg[3]);
                imageTop.setVisibility(View.GONE);
                break;
            case 4:
                imageBottom.setVisibility(View.GONE);
                imageTop.setImageResource(worksheetImg[4]);
                break;
            case 5:
                imageBottom.setVisibility(View.GONE);
                imageTop.setImageResource(worksheetImg[5]);
                break;
        }

        getData();
    }

    protected void onStart(){
        super.onStart();

        //監聽"確定"的按鈕
        mBtnChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == mBtnChk.getId()) {
                    //-------判斷使用者是否有選擇正確的答案
                    //清除radioButton 相信你看到這頁會有問題
                    if (!checkRecordDone(index)) {
                        if (Ans == 1) {
                            if(rg != null){
                                rg.removeAllViews();
                            }
                            mBtnNextquest.setVisibility(View.VISIBLE);
                            mBtnChk.setVisibility(View.GONE);
                            textDetail.setVisibility(View.VISIBLE);
                            //進行上傳動作
                            updateAns();
                            //showNextQuest();
                            textHint.setText(showCurrent );
                            //資料庫內預設沒有解釋的時候顯示無
                            if(getWorksheet.getDescription(index).equals("無")){
                            }else{
                                textDescription.setText(
                                        "\n"+ getWorksheet.getDescription(index));
                            }
                        } else if(Ans ==0){
                            if(rg != null){
                                rg.removeAllViews();
                            }
                            mBtnNextquest.setVisibility(View.VISIBLE);
                            mBtnChk.setVisibility(View.GONE);
                            textDetail.setVisibility(View.VISIBLE);
                            updateAns();
                            //showNextQuest();
                            textHint.setText(showFail);
                            if(getWorksheet.getDescription(index).equals("無")){
                            }else{
                                textDescription.setText(
                                        "\n"+ getWorksheet.getDescription(index));
                            }
                        }else{
                            Toast.makeText(WorksheetActivity.this, R.string.worksheet_notice1, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        showNextQuest();
                        Toast.makeText(WorksheetActivity.this, R.string.worksheet_notice2, Toast.LENGTH_SHORT).show();
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
        //顯示下一題的按鈕
        mBtnNextquest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDescription.setText("");
                textHint.setText("");
                textDetail.setVisibility(View.GONE);
                showNextQuest();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        //確認按鈕
        mBtnChk = (Button)findViewById(R.id.btnChk);

        getData();
    }
    //暫時不作用
    protected void showNextQuest(){
        count += 1;

        mBtnNextquest.setVisibility(View.GONE);
        mBtnChk.setVisibility(View.VISIBLE);

        if(count != maxQuestionLength){
            //------------顯示下一題
            index += 1;
            Ans = 2;
        }
        /*
        //測試用，可以顯示所有題目
        if(true){
            //------------顯示下一題
            index += 1;
            Ans = 2;
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
            //Toast.makeText(WorksheetActivity.this,"答題結束" , Toast.LENGTH_SHORT).show();
        }
        showData();
        //count=3等於使用者已回答三題，else表示使用者要回首頁了
    }

    public static boolean checkRecordDone(int index){
        return getWorksheet.getRecordDone(index) .equals("1");
    }
    //-------------------取得這位使用者的問答題
    private void getData() {
        questionLength = getWorksheet.getQuestionLength();
        optionLength = getWorksheet.getOptionLength();
        for (int i = 0; i < questionLength; i++) {
            question[i] = getWorksheet.getQuestion(i);
            description[i] = getWorksheet.getDescription(i);
            recordQuestion_id[i] = getWorksheet.getRecord_Question_id(i);
            recordDone[i] = getWorksheet.getRecordDone(i);
        }
        for (int i = 0; i < optionLength; i++) {
            option[i] = getWorksheet.getOption(i);
            optionQuestion_id[i] = getWorksheet.getOptionQuestion_id(i);
        }
        showData();
    }
    //-------------------顯示問答題內容
    private void showData(){
        for(int i =0 ; i < optionLength;i++){
            if(sign != -1)break;
            if(optionQuestion_id[i].equals(recordQuestion_id[index])){
                sign = i;
                break;
            }
        }
        if (option[index] != null) {
            //設定題目的文字
            textView.setText(question[index]);
            //顯示確認按鈕
            mBtnChk.setVisibility(View.VISIBLE);
            mBtnResume.setVisibility(View.GONE);
            RadioButton rb;
            OnClickListener mOnClickListener = new OnClickListener();
            int i =0;
            while(optionQuestion_id[sign].equals(recordQuestion_id[index])){
                rb = new RadioButton(this);
                if(optionQuestion_id[sign + 1] == null){
                    optionQuestion_id[sign + 1] = "100";
                }
                if(optionQuestion_id[sign].equals(optionQuestion_id[sign + 1])){
                    rb.setId(1 + i);             //id = 1,2,3
                    rb.setText(option[sign]);
                    nowAnser[i+1] = option[sign];//nowAnser[0] 使用範圍是1-4
                    rb.setTextSize(17.0f);
                    rb.setTextColor(Color.BLACK);
                    rb.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                    rb.setHeight(120);
                    if(i ==0){
                        rb.setChecked(true);
                    }
                    rb.setOnClickListener(mOnClickListener);
                    rg.addView(rb);
                    sign +=1;
                }else{
                    rb.setId(1 + i);             //id = 1,2,3
                    rb.setText(option[sign]);
                    nowAnser[i+1] = option[sign];
                    rb.setTextSize(17.0f);
                    rb.setTextColor(Color.BLACK);
                    rb.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                    rb.setHeight(120);
                    rb.setOnClickListener(mOnClickListener);
                    rg.addView(rb);
                    sign +=1;
                    break;
                }
                i+=1;
            }
        } else {
            mBtnChk.setVisibility(View.GONE);
            mBtnResume.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.worksheet_reload));
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
            myNavigationAsyncTask.execute(Common.updateAnsUrl + user_id , recordQuestion_id[index] ,Ans+"" );  //question_id[index]標註這題是哪一題, Ans為答案是否正確
            //Log.v("user_id:", user_id + "question[index]:"+ question_id[index] +"Ans:" +Ans+ "index" + index );  //question_id[index]標註這題是哪一題, Ans為答案是否正確
        } else {
            Toast.makeText(WorksheetActivity.this, "The connection has been canceled", Toast.LENGTH_SHORT).show();
        }
    }

    //建立監聽器，判斷使用者點擊哪一個按鈕
    class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RadioButton rb;
            switch(rg.getCheckedRadioButtonId()){
                case 1:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                    rb.setChecked(true);
                    break;
                case 2:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                    rb.setChecked(true);
                    break;
                case 3:

                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                    rb.setChecked(true);
                    break;
                case 4:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                    rb.setChecked(true);
                    break;
            }
            Log.v("rg",rg.getCheckedRadioButtonId()+"");
        }
    };


    /*------------------這裡的註解很重要 看不懂請提問
    在地理圍欄中，寫入postCount(n); n的範圍是0-4分別是作答的五隻動物
    0 = 第一題 1 = 第  四  題   2 = 第七題
    3 = 第十題 4 = 第 十三  題
    ------------------*/
    public static void postCount(int pCount){
        imgNo = pCount;
        index = pCount*3;
    }
}
