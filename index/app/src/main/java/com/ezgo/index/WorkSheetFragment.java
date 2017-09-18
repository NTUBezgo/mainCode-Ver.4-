package com.ezgo.index;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.getTokenAsyncTask;
import com.google.android.gms.actions.ItemListIntents;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ezgo.index.getWorksheet.getUser_id;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkSheetFragment extends Fragment {

    private View view;

    private MyData myData = new MyData();
    private ImageView imageView;
    private Button startExchange;

    private int[] imgViewsID= {R.id.circle_hyena,R.id.circle_bear,R.id.circle_wolf,
            R.id.circle_head_prairiedog ,R.id.circle_kookaburra,R.id.circle_deer
    };
    //確認是否六題皆完成
    int chkEnd = 0;
    private static int[] token = new int[10];

    /*private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction fragmentTransaction;*/

    public WorkSheetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_worksheet, container, false);
        startExchange = (Button) view.findViewById(R.id.startExchange);

        getTokenAsyncTask myAsyncTask = new getTokenAsyncTask(new getTokenAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("token");

                    for (int i = 0 ; i<jsonArray.length() ; i++){
                        // 沒有看過的動物要變成灰色
                        // token[i] == 0 代表還沒看過這隻動物
                        // token[i] == 1 代表看過這隻動物
                        token[i] = Byte.valueOf(jsonArray.getJSONObject(i).getString("token"));
                        imageView = (ImageView) view.findViewById(imgViewsID[i]);
                        //判斷頭像的顏色
                        if(token[i] == 0){
                            final int j=i;
                            imageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.worksheetImag1));    // 換色
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //跳到首頁的地圖
                                    Toast.makeText(getActivity(), R.string.worksheet_gotoMap , Toast.LENGTH_SHORT).show();
                                    myData.fromWS(j);
                                    ((MainActivity)getActivity()).jumpToMainFragment();

                                }
                            });

                            /*startExchange.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //在這裡寫 顯示他還沒作答完六題的提示訊息
                                }
                            });*/
                            continue;
                        }else {
                            chkEnd +=1;
                            final int j=i;
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //看動物的問答題
                                    Intent intent = new Intent();
                                    intent.setClass(getActivity(), WorksheetActivity.class);
                                    WorksheetActivity.postCount(j);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    if(chkEnd == 5){
                        //進入這裡表示作答完了，可以跳到兌換獎品頁,可以先不要做判斷，才有辦法進去兌換獎品頁?
                        startExchange.setVisibility(View.VISIBLE);
                        startExchange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity)getActivity()).exchangeReward();
                            }
                        });
                    }

                } catch (Exception e) {
                    //Log.v("ABC", Log.getStackTraceString(e));
                }
            }
        });myAsyncTask.execute(Common.getTokenUrl + getUser_id());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_worksheet);
    }

}
