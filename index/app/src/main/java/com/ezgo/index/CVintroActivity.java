package com.ezgo.index;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

public class CVintroActivity extends AppCompatActivity {

    private String animalName;
    private static final int GOTO_MAIN_ACTIVITY = 0;
    private ImageView answer_image;
    private Button btnIntroduction;
    private TextView TV_animalDetail;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private static int visionImg[] ={
            R.drawable.vision_blackbear,R.drawable.vision_deer,
            R.drawable.vision_kookaburra,R.drawable.vision_prairiedog,
            R.drawable.vision_hyena,R.drawable.vision_wolf
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvintro);


        TV_animalDetail = (TextView) findViewById(R.id.animal_detail);
        btnIntroduction = (Button)  findViewById(R.id.btn_introduction);
        answer_image = (ImageView) findViewById(R.id.answer_image);

        Bundle bundle=getIntent().getExtras();
        animalName=bundle.getString("from");

        if(animalName.equals("blackbear")){
            answer_image.setImageResource(visionImg[0]);
            TV_animalDetail.setText(getResources().getString(R.string.CV_introduction_bear));
        }else if(animalName.equals("deer")){
            answer_image.setImageResource(visionImg[1]);
            TV_animalDetail.setText(getResources().getString(R.string.CV_introduction_deer));
        }else if(animalName.equals("kookaburra")){
            answer_image.setImageResource(visionImg[2]);
            TV_animalDetail.setText(getResources().getString(R.string.CV_introduction_kookaburra));
        }else if(animalName.equals("prairiedog")){
            answer_image.setImageResource(visionImg[3]);
            TV_animalDetail.setText(getResources().getString(R.string.CV_introduction_prairiedog));
        }else if(animalName.equals("hyena")){
            answer_image.setImageResource(visionImg[4]);
            TV_animalDetail.setText(getResources().getString(R.string.CV_introduction_hyena));
        }else if(animalName.equals("wolf")){
            answer_image.setImageResource(visionImg[5]);
            TV_animalDetail.setText(getResources().getString(R.string.CV_introduction_wolf));
        }

        btnIntroduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回主畫面
                Intent intent = new Intent();
                intent.setClass(CVintroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
