package com.ezgo.index;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class KnowledgeFragment extends Fragment {

    private View view;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public KnowledgeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_knowledge, container, false);

        int [] ID={R.id.kn_hyena, R.id.kn_bear, R.id.kn_wolf, R.id.kn_prairiedog, R.id.kn_kookaburra, R.id.kn_deer};



        for(int i=0;i<6;i++){
            ImageView imageView = (ImageView) view.findViewById(ID[i]);

            final int finalI=i;

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntroductionFragment fragment = new IntroductionFragment();
                    fragmentManager=getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame,fragment);
                    Bundle bundle = new Bundle();

                    bundle.putInt("id", finalI);
                    fragment.setArguments(bundle);
                    fragmentTransaction.commit();
                }});
        }

        return view;
    }

}
