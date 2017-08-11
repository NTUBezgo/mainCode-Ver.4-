package com.ezgo.index;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.actions.ItemListIntents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class WorkSheetFragment extends Fragment {

    private View view;

    private ImageView imageView;
    private ImageView startExchange;
    //private MyData myData = new MyData();

    /*private FragmentManager fragmentManager = getFragmentManager();
    private FragmentTransaction fragmentTransaction;*/

    public WorkSheetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.fragment_worksheet, container, false);

        imageView= (ImageView) view.findViewById(R.id.circle_bear);
        imageView.setColorFilter(ContextCompat.getColor(getActivity(), R.color.worksheetImag1)); // 換色

       /* Toast.makeText(getActivity(), "請至此動物區尋找題目與答案", Toast.LENGTH_SHORT).show();
        myData.fromWS(position);
        ((MainActivity)getActivity()).jumpToMainFragment();*/

        //兌換獎品
        startExchange = (ImageView) view.findViewById(R.id.startExchange);
        startExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).exchangeReward();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("本期闖關單");
    }

}
