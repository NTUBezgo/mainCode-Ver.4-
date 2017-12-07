package com.ezgo.index;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ResetActivity extends AppCompatActivity {

    private Button btn_continue;
    private Button btn_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        btn_continue = (Button)findViewById(R.id.btn_continue);
        btn_reset= (Button)findViewById(R.id.btn_reset);

        btn_continue.setOnClickListener(new View.OnClickListener() { //繼續遊玩
            @Override
            public void onClick(View v) {

                toLoading();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() { //重新開始
            @Override
            public void onClick(View v) {

                toLoading();
            }
        });



    }

    //-------------------------------------------------------------------------------------------------
    private void toLoading(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("from", "ResetActivity");
        intent.putExtras(bundle);

        intent.setClass(ResetActivity.this, LoadingActivity.class);
        startActivity(intent);
        finish();
    }
}
