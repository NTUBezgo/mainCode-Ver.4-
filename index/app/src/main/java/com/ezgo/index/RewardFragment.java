package com.ezgo.index;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.worksheetAsyncTask;
import com.ezgo.index.MyAsyncTask.worksheetUpdateAsyncTask;

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

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.reward_alert_1, null);
            TextView tv_ok = (TextView) v.findViewById(R.id.id_name2);

            tv_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad.dismiss();
                }
            });

            dialogBuilder.setView(v);
            ad = dialogBuilder.show();

            exchagneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isConnected()) {    //檢查網路是否開啟
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        View view = LayoutInflater.from(getActivity()).inflate(R.layout.reward_alert, null);
                        TextView tv_btn1 = (TextView) view.findViewById(R.id.btn_wait);
                        TextView tv_btn2 = (TextView) view.findViewById(R.id.btn_go);
                        tv_btn1.setOnClickListener(new View.OnClickListener() { //稍後
                            @Override
                            public void onClick(View view) {
                                ad.dismiss();
                                Toast.makeText(getActivity(), R.string.reward_notice, Toast.LENGTH_SHORT).show();
                            }
                        });
                        tv_btn2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) { //立即兌換
                                ad.dismiss();
                                updateReward();
                                downloadRewardDone();
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.reward_exchange);
    }

    private void downloadRewardDone(){
        worksheetAsyncTask myAsyncTask = new worksheetAsyncTask(new worksheetAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("reward");
                    rewardDone = jsonArray.getJSONObject(0).getString("done");
                    if(rewardDone.equals("1")){
                        exchagneBtn.setImageResource(R.drawable.dog_exchange2);
                        exchagneBtn.setEnabled(false);
                    }
                } catch (Exception e) {
                    //Log.v("ABC", Log.getStackTraceString(e));
                }
            }
        });
        myAsyncTask.execute(Common.downloadRewardDone + getWorksheet.getUser_id());
    }

    private void updateReward(){
        worksheetAsyncTask myNavigationAsyncTask = new worksheetAsyncTask(new worksheetAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
            }
        });
        if(!myNavigationAsyncTask.isCancelled()) {
            //執行上傳動作
            myNavigationAsyncTask.execute(Common.updateRewardUrl + getWorksheet.getUser_id());  //question_id[index]標註這題是哪一題, Ans為答案是否正確
            //Log.v("user_id:", user_id + "question[index]:"+ question_id[index] +"Ans:" +Ans+ "index" + index );  //question_id[index]標註這題是哪一題, Ans為答案是否正確
        } else {
            //Toast.makeText(RewardFragment.this, "連線已取消", Toast.LENGTH_SHORT).show();
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
