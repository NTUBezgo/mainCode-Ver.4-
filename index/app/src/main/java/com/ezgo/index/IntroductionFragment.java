package com.ezgo.index;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroductionFragment extends Fragment {

    private View view;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private int[] animalIntroID= {R.drawable.knowledge_hyena, R.drawable.knowledge_bear, R.drawable.knowledge_wolf,
            R.drawable.knowledge_prairiedog, R.drawable.knowledge_kookaburra, R.drawable.knowledge_deer
    };
    private Button kn_back;

    public IntroductionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_introduction, container, false);

        Bundle bundle = getArguments();
        int id = bundle.getInt("id");

        ImageView imageView;
        imageView = (ImageView) view.findViewById(R.id.iv_intro);
        imageView.setImageResource(animalIntroID[id]); //設定圖片

        kn_back=(Button) view.findViewById(R.id.kn_back); //返回
        kn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KnowledgeFragment fragment = new KnowledgeFragment();
                fragmentManager=getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_knowledge);
    }

}
