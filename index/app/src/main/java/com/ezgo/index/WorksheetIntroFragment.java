package com.ezgo.index;


import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * A simple {@link Fragment} subclass.
 */
public class WorksheetIntroFragment extends Fragment {

    private View view;
    private ImageView animalCircle;
    private TextView animalTitle;
    private Button intro_back;

    private static String[] question = new String[100];
    //---------判斷傳進來的值是哪一張圖片
    private static int imgNo ;
    //---------目前題目在第0題，要從地理圍欄傳入該動物是哪隻動物(詳見postIndex) range:0-14
    private static int index = 0;

    private int[] imgViewsDrable= {R.drawable.sticker_hyena, R.drawable.sticker_bear, R.drawable.sticker_wolf,
            R.drawable.sticker_prairiedog , R.drawable.sticker_kookaburra, R.drawable.sticker_deer
    };
    private int[] animalCircleID= {R.id.intro_animalCircle1, R.id.intro_animalCircle2, R.id.intro_animalCircle3};
    private int[] animalTitleID= {R.id.intro_animalTitle1, R.id.intro_animalTitle2, R.id.intro_animalTitle3};

    public WorksheetIntroFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_worksheet_intro, container, false);
        intro_back = (Button) view.findViewById(R.id.intro_back);

        intro_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).exchangeWorksheetFragment();
            }
        });

        getData();
        setImage();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.nav_worksheet);
    }

    public void setImage(){
        animalCircle = (ImageView) view.findViewById(R.id.intro_animalCircle);
        animalCircle.setImageResource(imgViewsDrable[imgNo]);
        for(int i = 0 ; i< 3; i++){
            animalTitle = (TextView) view.findViewById(animalTitleID[i]);
            switch (imgNo){
                case 0:
                    animalTitle.setText(question[index+i]);
                    break;
                case 1:
                    animalTitle.setText(question[index+i]);
                    break;
                case 2:
                    animalTitle.setText(question[index+i]);
                    break;
                case 3:
                    animalTitle.setText(question[index+i]);
                    break;
                case 4:
                    animalTitle.setText(question[index+i]);
                    break;
                case 5:
                    animalTitle.setText(question[index+i]);
                    break;
            }
        }

    }
    private void getData() {
        for (int i = 0; i < getWorksheet.getQuestionLength(); i++) {
            question[i] = getWorksheet.getQuestion(i);
        }
    }
    public static void postCount(int pCount){
        imgNo = pCount;
        index = pCount*3;
    }

}
