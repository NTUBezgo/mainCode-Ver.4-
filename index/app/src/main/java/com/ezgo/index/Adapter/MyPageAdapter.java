package com.ezgo.index.Adapter;

/**
 * Created by Amy on 2017/7/24.
 */


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ezgo.index.R;

import java.util.List;

public class MyPageAdapter extends PagerAdapter {

    private List<View> views; //界面列表
    private LayoutInflater inflater;
    private Context context;

    private Button startMain;

    public MyPageAdapter(Context context, List<View> views){
        this.context=context;
        this.views = views;
    }

    //銷毀arg1位置的界面
    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(views.get(arg1));
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        if(arg1==3){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View itemView = inflater.inflate(R.layout.activity_endguide, arg0, false);
            ((ViewPager) arg0).addView(itemView);
            return itemView;
        }else{
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            return views.get(arg1);
        }
    }

    @Override
    public void finishUpdate(View arg0) {
        // TODO Auto-generated method stub
    }

    //獲得當前界面數
    @Override
    public int getCount() {
        if (views != null)
        {
            return views.size();
        }
        return 0;
    }

    //判斷是否由對象生成界面
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    @Override
    public void startUpdate(View arg0) {
        // TODO Auto-generated method stub
    }
}
