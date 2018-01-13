package com.ezgo.index;


import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.getterAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class RewardFragment extends Fragment {

    private View view;
    private ImageView exchagneBtn;

    private AlertDialog ad;

    private String rewardDone;

    public RewardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_reward, container, false);
        exchagneBtn = (ImageView) view.findViewById(R.id.iv_exchange);

        if (isConnected()) {    //檢查網路是否開啟
            downloadRewardDone(); //取得兌換紀錄

            exchagneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isConnected()) {    //檢查網路是否開啟

                            //----提示-確認是否要兌換----
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.reward_alert, null);
                            Button btn_later= (Button) view.findViewById(R.id.btn_later);
                            Button btn_get= (Button) view.findViewById(R.id.btn_get);

                            btn_later.setOnClickListener(new View.OnClickListener() { //稍後
                                @Override
                                public void onClick(View view) {
                                    ad.dismiss();
                                }
                            });
                            btn_get.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) { //立即兌換
                                    try{
                                        ad.dismiss();
                                        updateReward();
                                        downloadRewardDone();

                                        //----------提示-可重玩並重整MainActivity-------
                                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.reward_alert_1, null);
                                        Button btn_know= (Button) v.findViewById(R.id.btn_know);

                                        btn_know.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });

                                        dialogBuilder.setView(v);
                                        ad = dialogBuilder.show();
                                    }catch (Exception e) {
                                        //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
                                        WrongActivity mWrontAct = new WrongActivity();
                                        String witchWrongBlock = "click exchageBtn";

                                        ActivityManager activityManager=(ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                                        String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

                                        mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

                                        Intent intent = new Intent();
                                        intent.setClass(getActivity(), WrongActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }
                            });

                            dialogBuilder.setView(view);
                            ad = dialogBuilder.show();

                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.notice_network);
                        builder.setCancelable(false);

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.notice_network);
            builder.setCancelable(false);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return view;
    }


    //---------------------取得兌換紀錄--------------
    private void downloadRewardDone(){
        getterAsyncTask myAsyncTask = new getterAsyncTask(new getterAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("reward");
                    rewardDone = jsonArray.getJSONObject(0).getString("done");
                    if(rewardDone.equals("1")){
                        exchagneBtn.setImageResource(R.drawable.dog_exchange2);
                        exchagneBtn.setEnabled(false);
                        getWorksheet.postUserDone(rewardDone); //更改變數
                    }
                } catch (Exception e) {
                    //Log.v("ABC", Log.getStackTraceString(e));
                }
            }
        });
        myAsyncTask.execute(Common.downloadRewardDone + getWorksheet.getUser_id());
    }

    //---------------------更新兌換紀錄--------------
    private void updateReward(){
        getterAsyncTask myNavigationAsyncTask = new getterAsyncTask(new getterAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
            }
        });
        if(!myNavigationAsyncTask.isCancelled()) {
            //執行上傳動作
            myNavigationAsyncTask.execute(Common.updateRewardUrl + getWorksheet.getUser_id());  //question_id[index]標註這題是哪一題, Ans為答案是否正確
            //Log.v("user_id:", user_id + "question[index]:"+ question_id[index] +"Ans:" +Ans+ "index" + index );  //question_id[index]標註這題是哪一題, Ans為答案是否正確
        } else {
        }
    }

    private boolean isConnected(){  //檢查網路是否開啟
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
