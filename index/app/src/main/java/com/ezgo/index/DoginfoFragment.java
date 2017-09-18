package com.ezgo.index;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoginfoFragment extends Fragment {


    public DoginfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doginfo, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_dogInfo);
    }

}
