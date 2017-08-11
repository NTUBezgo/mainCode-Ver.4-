package com.ezgo.index;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class AnimalintroActivity extends AppCompatActivity {

    private static final int GOTO_MAIN_ACTIVITY = 0;
    private static int index; //進入哪一個動物範圍
    private ImageView introPic;

    //動物介紹
    private int[] animalIntro={R.drawable.circle_hyena, R.drawable.circle_bear, R.drawable.circle_wolf,
            R.drawable.circle_prairiedog, R.drawable.circle_kookaburra, R.drawable.gift};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animalintro);

        Bundle bundle=getIntent().getExtras();
        index=bundle.getInt("index");

        introPic = (ImageView) findViewById(R.id.introPic);
        introPic.setImageResource(animalIntro[index]);


        mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 3000); //3秒跳轉
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GOTO_MAIN_ACTIVITY:
                    Intent intent = new Intent();
                    intent.setClass(AnimalintroActivity.this, WorksheetActivity.class);
                    WorksheetActivity.postCount(index);
                    startActivity(intent);
                    finish();
                    break;

                default:
                    break;
            }
        }

    };

    public static void testSet(int a ){index+=a;}
}
