package com.ezgo.index;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
    //---------選項的字體大小
    private float fontSize = 19;
    //----------目前語言
    private String nowLanguage = "";

    //---------使用者的資料
    private static String[] question = new String[100];
    private static String[] description = new String[100];
    private static String[] option = new String[100];
    private static String[] recordQuestion_id = new String[100];
    private static String[] optionQuestion_id = new String[100];
    private static String[] recordDone = new String[100];

    private static String[] nowAnswer = new String[5];
    //---------取得總共有幾個選項(每一題有多個選項)這裡指所有題目共有幾個選項
    private static int optionLength = 0;
    //---------取得總共有幾題題目
    private static int questionLength = 0;
    //---------判斷傳進來的值是哪一張圖片
    private static int imgNo ;
    //---------目前題目在第0題，要從地理圍欄傳入該動物是哪隻動物(詳見postIndex) range:0-14
    private static int index = 0;
    //---------判斷使用者是否有點下正確答案的radioButton
    private static byte Ans = 2;
    //---------判斷使用者已經回答幾題range:0-2 當=3時會被重置為0 (333行)
    private int count = 0;
    //---------用於答對或答錯後顯示什麼提示訊息
    private String showCurrent;
    private String showFail;
    private int titleNumber = 0;
    //計算單題選項總數
    int optionSum;
    private static int titleNo[] ={
            R.drawable.number_wood_1,R.drawable.number_wood_2,R.drawable.number_wood_3
    };
    /*
    private int[] imgViewsDrable= {R.drawable.sticker_hyena, R.drawable.sticker_bear, R.drawable.sticker_wolf,
            R.drawable.sticker_prairiedog , R.drawable.sticker_kookaburra, R.drawable.sticker_deer
    };
    */
    private static int titleAnimal[] ={
            R.drawable.worksheet_title_hyena,R.drawable.worksheet_title_bear,R.drawable.worksheet_title_wolf,
            R.drawable.worksheet_title_prairiedog,R.drawable.worksheet_title_kookaburra,R.drawable.worksheet_title_deer
    };
    private static int optionImg[] ={
            R.drawable.optiona,R.drawable.optionb,R.drawable.optionc,R.drawable.optiond
    };
    private static int optionCheckImg[] ={
            R.drawable.optionchecka,R.drawable.optioncheckb,R.drawable.optioncheckc,R.drawable.optioncheckd
    };
    static Boolean[] setCheck = new Boolean[4];
    //true = 選項介面 false = 解釋介面
    private static boolean state = true;
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

    ImageView imageCircle;
    ImageView imageNumber;
    ImageView yesNoImg;

    private int user_id;
    public Button mBtnChk;
    public Button mBtnResume;
    public Button mBtnNextquest;
    public RadioGroup rg;
    private TextView title;
    private TextView textDescription;
    private TextView textHint;
    private LinearLayout textDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet);
        //-------------連接資料庫來取得值，不需要更改

        //------------------取得使用者ID
        user_id = getWorksheet.getUser_id();
        //------------------取得目前語系該用的字體
        nowLanguage =  getWorksheet.getLanguage();
        setFontSize();
        //--------------------------------------------------
        rg  = (RadioGroup) findViewById(R.id.rg);


        showCurrent = getString(R.string.worksheet_hintRight);
        showFail = getString(R.string.worksheet_hintWrong);

        imageCircle = (ImageView) findViewById(R.id.ImgViewCircle);
        imageNumber = (ImageView) findViewById(R.id.ImgViewNumber);
        yesNoImg = (ImageView) findViewById(R.id.YesNoImg);

        title = (TextView) findViewById(R.id.title);
        title.setTextSize(20.f);
        textDescription = (TextView) findViewById(R.id.textDescription);
        textHint = (TextView) findViewById(R.id.textHint);
        mBtnChk = (Button)findViewById(R.id.btnChk);
        mBtnResume = (Button)findViewById(R.id.btnResume);
        mBtnNextquest = (Button)findViewById(R.id.btnNextquest);

        textDetail = (LinearLayout) findViewById(R.id.textDetail);
        mBtnNextquest.setVisibility(View.GONE);
        textDetail.setVisibility(View.GONE);

        imageNumber.setImageResource(titleNo[titleNumber]);

        //判斷目前該顯示哪一張圓圈動物頭
        switch (imgNo){
            case 0:
                imageCircle.setImageResource(titleAnimal[imgNo]);
                break;
            case 1:
                imageCircle.setImageResource(titleAnimal[imgNo]);
                break;
            case 2:
                imageCircle.setImageResource(titleAnimal[imgNo]);
                break;
            case 3:
                imageCircle.setImageResource(titleAnimal[imgNo]);
                break;
            case 4:
                imageCircle.setImageResource(titleAnimal[imgNo]);
                break;
            case 5:
                imageCircle.setImageResource(titleAnimal[imgNo]);
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
                            yesNoImg.setImageResource(R.drawable.worksheet_true);
                            showAns();
                        } else if(Ans ==0){
                            yesNoImg.setImageResource(R.drawable.worksheet_false);
                            showAns();

                        }else{
                            Toast.makeText(WorksheetActivity.this, R.string.worksheet_notice1, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if(rg != null){
                            rg.removeAllViews();
                        }
                        mBtnNextquest.setVisibility(View.VISIBLE);
                        mBtnChk.setVisibility(View.GONE);
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
        getWorksheet.getJSON();
    }
    protected void onStop() {
        super.onStop();

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
        try {
            for (int i = 0; i < optionLength; i++) {
                if (sign != -1) break;
                if (optionQuestion_id[i].equals(recordQuestion_id[index])) {
                    sign = i;
                    break;
                }
            }

            if (option[index] != null) {
                //設定題目的文字
                title.setText(question[index]);
                //顯示確認按鈕
                mBtnChk.setVisibility(View.VISIBLE);
                mBtnResume.setVisibility(View.GONE);
                RadioButton rb;
                OnClickListener mOnClickListener = new OnClickListener();
                optionSum = 0;

                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                while (optionQuestion_id[sign].equals(recordQuestion_id[index])) {
                    rb = new RadioButton(this);
                    if (optionQuestion_id[sign + 1] == null) {
                        optionQuestion_id[sign + 1] = "100";
                    }
                    params.setMargins(0, 0, 0, 3);

                    if (optionQuestion_id[sign].equals(optionQuestion_id[sign + 1])) {
                        rb.setId(1 + optionSum);             //id = 1,2,3
                        rb.setText("　　" + stringFormat(option[sign]));
                        nowAnswer[optionSum + 1] = option[sign];//nowAnswer[0] 使用範圍是1-4
                        rb.setTextSize(fontSize);
                        rb.setLineSpacing(0.8F, 0.8F);
                        rb.setTextColor(Color.BLACK);
                        rb.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                        rb.setBackgroundResource(optionImg[optionSum]);
                        rb.setLayoutParams(params);
                        rb.setGravity(Gravity.CENTER);
                        rb.setButtonDrawable(null);
                        if (optionSum == 0) {
                            rb.setChecked(true);
                        }
                        rb.setOnClickListener(mOnClickListener);
                        rg.addView(rb);
                        sign += 1;
                    } else {
                        rb.setId(1 + optionSum);             //id = 1,2,3
                        rb.setText("　　" + stringFormat(option[sign]));
                        nowAnswer[optionSum + 1] = option[sign];
                        rb.setTextSize(fontSize);
                        rb.setTextColor(Color.BLACK);
                        rb.setLineSpacing(0.8F, 0.8F);
                        rb.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                        rb.setBackgroundResource(optionImg[optionSum]);
                        rb.setLayoutParams(params);
                        rb.setGravity(Gravity.CENTER);
                        rb.setButtonDrawable(null);
                        rb.setOnClickListener(mOnClickListener);
                        rg.addView(rb);
                        sign += 1;
                        break;

                    }
                    optionSum += 1;
                }
            } else {
                mBtnChk.setVisibility(View.GONE);
                mBtnResume.setVisibility(View.VISIBLE);
                title.setText(getString(R.string.worksheet_reload));
            }
        }catch (Exception e){
            //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
            WrongActivity mWrontAct = new WrongActivity();
            String witchWrongBlock = "updateUser data";

            ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

            mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

            Intent intent = new Intent();
            intent.setClass(WorksheetActivity.this, WrongActivity.class);
            startActivity(intent);

            finish();
        }
    }


    protected void showNextQuest(){
        count += 1;
        //titleNumber的範圍是0-2(三個題目->三張圖片)
        titleNumber +=1;
        state = true;
        title.setVisibility(View.VISIBLE);
        if(count != maxQuestionLength){
            //------------顯示下一題
            index += 1;
            Ans = 2;
            imageNumber.setImageResource(titleNo[titleNumber]);
            mBtnNextquest.setVisibility(View.GONE);
            mBtnChk.setVisibility(View.VISIBLE);
            showData();
        }else{  //count=3等於使用者已回答三題，else表示使用者要回首頁了
            title.setVisibility(View.GONE);
            mBtnChk.setVisibility(View.GONE);
            mBtnNextquest.setVisibility(View.GONE);
            count = 0;
            titleNumber =0;
            //-------------在這裡撰寫回首頁的程式碼
            Intent intent = new Intent();
            intent.setClass(WorksheetActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            //Toast.makeText(WorksheetActivity.this,"答題結束" , Toast.LENGTH_SHORT).show();
        }
          /*
        //測試用，可以顯示所有題目
        if(true){
            //------------顯示下一題
            index += 1;
            Ans = 2;
        }
        */

    }
    //判斷該題是否作答過
    public static boolean checkRecordDone(int index){
        return getWorksheet.getRecordDone(index) .equals("1");
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
            rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
            switch(rg.getCheckedRadioButtonId()){
                case 1:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    setOptionCheck(rg.getCheckedRadioButtonId()-1);
                    break;
                case 2:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    setOptionCheck(rg.getCheckedRadioButtonId()-1);
                    break;
                case 3:

                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    setOptionCheck(rg.getCheckedRadioButtonId()-1);
                    break;
                case 4:
                    if(rg.getCheckedRadioButtonId() == getWorksheet.getAnswer(index)) {
                        Ans = 1;
                    }else {
                        Ans = 0;
                    }
                    setOptionCheck(rg.getCheckedRadioButtonId()-1);
                    break;
            }
            for(int i =0;i<4;i++){
                try {
                    rb = (RadioButton) findViewById(1 + i);
                    if (setCheck[i]) {
                        rb.setBackgroundResource(optionCheckImg[rg.getCheckedRadioButtonId() - 1]);
                    } else {
                        rb.setBackgroundResource(optionImg[i]);
                    }
                }catch (Exception e){

                }
            }
            //Log.v("rg",rg.getCheckedRadioButtonId()+"");
        }
    };


    /*------------------這裡的註解很重要 看不懂請提問
    在地理圍欄中，寫入postCount(n); n的範圍是0-4分別是作答的五隻動物
    0 = 第一題 1 = 第  四  題   2 = 第七題
    3 = 第十題 4 = 第 十三  題
    ------------------*/
    public static void postCount(int pCount){
        //Log.v("pCount ：", pCount + "");
        imgNo = pCount;
        index = pCount*3;
        sign = -1;
    }

    public static void setOptionCheck(int whatOption){
        for(int i = 0 ; i < 4 ; i++){
            setCheck[i] = false;
        }
        setCheck[whatOption] = true;
    }
    public void showAns(){
        state = false;
        if(rg != null){
            rg.removeAllViews();
        }
        title.setVisibility(View.GONE);
        mBtnNextquest.setVisibility(View.VISIBLE);
        mBtnChk.setVisibility(View.GONE);
        textDetail.setVisibility(View.VISIBLE);
        updateAns();//-------------------------------進行上傳動作-----------------------------------------
        if(Ans == 1){
            textHint.setText(showCurrent);
        }else{
            textHint.setText(showFail);
        }

        //資料庫內預設沒有解釋的時候顯示無
        if(getWorksheet.getDescription(index).equals("無")){
        }else{
            textDescription.setText(
                    "\n"+ getWorksheet.getDescription(index));
        }
    }
    public void setFontSize(){
        //中文字體大小:19 英文:15
        if(nowLanguage.contains("zh")){
            fontSize = 19;
        }else {
            fontSize = 14;
        }
    }

    public StringBuffer stringFormat(String str){
        StringBuffer mstringBuffer = new StringBuffer();

        if(nowLanguage.contains("zh")){
            mstringBuffer.append(str);
            if(mstringBuffer.length() ==8){
            }else if(mstringBuffer.length() >8){
                mstringBuffer.insert(9,"\n　");
            }
        }else{
            String[] word;
            //設定一行最多幾個字(不包括空白)
            final int lineLength = 20;

            //字串計數器，限制一行只能有30個英文字
            int count = lineLength;
            word = str.split(" ");
            int wordIndex = 0 ;
            while(word.length > wordIndex){
                if( (count - word[wordIndex].length()) < 0 ){
                    mstringBuffer.append("\n　　"+word[wordIndex] + "  ");
                    Log.v("wordLength:",word[wordIndex].length()+"");
                    Log.v("word:",word[wordIndex]+"");
                    Log.v("count:",count+"");
                    Log.v("none","---------------------------------------------");

                    count = lineLength;
                }else{
                    mstringBuffer.append(word[wordIndex] + " ");
                    Log.v("wordLength:",word[wordIndex].length()+"");
                    Log.v("word:",word[wordIndex]+"");
                    Log.v("count:",count+"");
                }
                count = count - word[wordIndex].length();
                wordIndex++;
            }
        }
    /*
        for(int i = 0 ; i < word.length; i++){
            Log.v("ABC " , (i%3)+"");
            if(((i+1) % 3) ==0){
                mstringBuffer.append(word[i]+"\n");
            }else{
                mstringBuffer.append(word[i] + " ");
            }
        }
        */
        /*
        if(mstringBuffer.length() ==16){

        }else if(mstringBuffer.length() >16){
            mstringBuffer.insert(17,"\n　");
        }
        */
        return mstringBuffer;
    }

}