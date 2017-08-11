package com.ezgo.index;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ezgo.index.Adapter.MyPageAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private View view;
    private ViewPager vp;
    private MyPageAdapter vpAdapter;
    private List<View> views;

    private Button startMain;
    private ImageView circleImage;

    //引導圖片資源
    private static final int[] pics = { R.drawable.guide1,R.drawable.guide2,
            R.drawable.guide3};

    private ImageView[] dots ; //底部小點圖片
    private int currentIndex; //記錄當前選中位置

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.activity_guide, container, false);

        views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        //初始化引導圖片列表
        for(int i=0; i<pics.length; i++) {
            ImageView iv = new ImageView(getActivity());
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

            views.add(iv);
        }
        vp = (ViewPager) view.findViewById(R.id.viewpager);
        vpAdapter = new MyPageAdapter(getActivity(), views);  //初始化Adapter
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this); //绑定回調

        initDots();  //初始化底部小點

        /*startMain = (Button) view.findViewById(R.id.startMain);
        startMain.setText("");*/
        circleImage=(ImageView) view.findViewById(R.id.circle4);
        circleImage.setVisibility(View.GONE);

        return view;
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll);
        dots = new ImageView[pics.length];
        //循環取得小點圖片
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);//都設为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//設置位置tag，方便取出與當前位置對應
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//設置为白色，即選中狀態
    }

    /**
     *設置當前的引導頁
     */
    private void setCurView(int position)
    {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vp.setCurrentItem(position);
    }

    /**
     *這只當前引導小點的選中
     */
    private void setCurDot(int positon)
    {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        dots[positon].setEnabled(false);
        dots[currentIndex].setEnabled(true);
        currentIndex = positon;
    }

    //當滑動狀態改變時調用
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
    }

    //當當前頁面被滑動時調用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    //當新的頁面被選中時調用
    @Override
    public void onPageSelected(int arg0) {
        //設置底部小點選中狀態
        setCurDot(arg0);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("闖關說明");
    }

}
