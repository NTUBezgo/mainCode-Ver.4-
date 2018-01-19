package com.ezgo.index;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.getRecordDoneAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.ezgo.index.getWorksheet.getUser_id;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkSheetFragment extends Fragment {

    private View view;

    private MyData myData;
    private ImageView imageView;
    private TextView textView;
    private Button startExchange;
    private TextView tv_exchangeNotice;
    private int[] imgViewsID= {R.id.circle_hyena, R.id.circle_bear, R.id.circle_wolf,
            R.id.circle_head_prairiedog , R.id.circle_kookaburra, R.id.circle_deer
    };
    //確認是否六題皆完成
    int chkEnd = 0;
    private static int[] recordDone = new int[7];

    public WorkSheetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_worksheet, container, false);
        startExchange = (Button) view.findViewById(R.id.startExchange);

        for (int i = 0 ; i<imgViewsID.length ; i++) { //頭像預設為灰色
            imageView = (ImageView) view.findViewById(imgViewsID[i]);
            ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new float[]{
                    0.33F, 0.59F, 0.11F, 0, 0,
                    0.33F, 0.59F, 0.11F, 0, 0,
                    0.33F, 0.59F, 0.11F, 0, 0,
                    0, 0, 0, 1, 0,
            });
            imageView.setColorFilter(colorFilter);// 換色
        }

        textView = (TextView) view.findViewById(R.id.showQuestAmount);
        tv_exchangeNotice = (TextView) view.findViewById(R.id.tv_exchangeNotice);
        myData = new MyData(getResources());

        setAnimalCircle();

        return view;
    }

    private void setAnimalCircle(){

        getRecordDoneAsyncTask myAsyncTask = new getRecordDoneAsyncTask(new getRecordDoneAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    chkEnd=0;
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("ans");
                    textView.setText(getString(R.string.worksheet_correct) + jsonArray.getJSONObject(0).getString("ans"));

                    jsonArray = object.getJSONArray("recordDone");

                    for (int i = 0 ; i<jsonArray.length() ; i++){
                        // recordDone[i] == 0 代表還沒看過這隻動物
                        // recordDone[i] == 1 代表看過這隻動物
                        recordDone[i] = Byte.valueOf(jsonArray.getJSONObject(i).getString("recordDone"));
                        //Log.e("recordDone",""+recordDone[i]);
                        imageView = (ImageView) view.findViewById(imgViewsID[i]);
                        //判斷頭像的顏色
                        if(recordDone[i] == 0){
                            final int j=i;
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //跳到首頁的地圖
                                    Toast.makeText(getActivity(), R.string.worksheet_gotoMap , Toast.LENGTH_SHORT).show();
                                    myData.fromWS(j);
                                    ((MainActivity)getActivity()).jumpToMainFragment();
                                }
                            });
                            continue;
                        }else {
                            chkEnd +=1;
                            final int j=i;
                            imageView.clearColorFilter(); //回答過變回彩色---
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //看動物的問答題
                                    WorksheetIntroFragment.postCount(j);
                                    ((MainActivity)getActivity()).exchangeWorksheetIntro();
                                }
                            });
                        }
                    }
                    if(chkEnd == 6){
                        //進入這裡表示作答完了，可以跳到兌換獎品頁
                        tv_exchangeNotice.setVisibility(View.INVISIBLE);
                        startExchange.setBackground(getResources().getDrawable(R.drawable.btn_reward));
                        startExchange.setText(R.string.worksheet_btnStartExchange);
                        startExchange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity)getActivity()).exchangeReward();
                            }
                        });
                    }

                } catch (Exception e) {
                   /*//在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
                    WrongActivity mWrontAct = new WrongActivity();
                    String witchWrongBlock = "setAnimalCircle";

                    mWrontAct.setError(e.toString(),witchWrongBlock,"WorkSheetFragment");

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WrongActivity.class);
                    startActivity(intent);
                    getActivity().finish();*/
                }
            }
        });myAsyncTask.execute(Common.getRecordDoneUrl + getUser_id());
    }

    @Override
    public void onResume() {
        super.onResume();
        setAnimalCircle();
    }

}
