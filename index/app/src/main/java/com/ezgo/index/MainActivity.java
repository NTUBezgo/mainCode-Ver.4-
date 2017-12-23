package com.ezgo.index;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    Context context;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private Menu mMenu;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MainFragment mainFragment;
    private NavigationView navigationView;

    private MyData myData;

    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        myData =new MyData(getResources());
        fragmentManager = getSupportFragmentManager();

        //--------------設定ActionBar-----------------
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //---------------drawer設定---------------------
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //-----------------NavigationView設定------------
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //---------設定預設地圖Fragment--------
        FragmentTransaction defultFragment = getSupportFragmentManager().beginTransaction();
        defultFragment.replace(R.id.main_frame, new MainFragment());
        defultFragment.commit();

    }

    //--------------------------------------------------兌換獎品-----------------------------------------
    public void exchangeReward(){
        Fragment fragment = null;
        fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new RewardFragment();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    //-----------------------------------------本期闖關單彩色頭像跳至動物簡介--------------------------------------
    public void exchangeWorksheetIntro(){
        Fragment fragment = null;
        fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new WorksheetIntroFragment();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    //--------------------------------------------------跳回本期闖關單-----------------------------------------
    public void exchangeWorksheetFragment(){
        Fragment fragment = null;
        fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new WorkSheetFragment();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    //------------------------------------------選取灰色頭像跳至其座標-----------------------------------------
    public void jumpToMainFragment(){
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }


    @Override
    public void onBackPressed() {
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){//當按下左上三條線或顯示工具列
            return true;
        }

        switch(item.getItemId()) {
            case R.id.action_worksheet: //選擇顯示闖關單的Markers
                mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_frame);
                mainFragment.chooseWorkSheetMarkers();
                return true;
            case android.R.id.home:
                FragmentManager fm = getSupportFragmentManager();
                if(fm.getBackStackEntryCount() > 0){
                    fm.popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        fragmentTransaction = fragmentManager.beginTransaction();

        // Handle navigation view item clicks here.
        switch(item.getItemId()){
            case R.id.nav_map:     //---------切換地圖頁面---------
                fragment = new MainFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(true);
                break;
            case R.id.nav_worksheet:     //---------切換本期闖關單頁面---------
                fragment = new WorkSheetFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                break;
            case R.id.nav_dogInfo:     //---------切換旺哥小檔案頁面---------
                fragment = new DoginfoFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                break;
            case R.id.nav_info:     //---------切換闖關說明頁面---------
                fragment = new InformationFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                break;
            case R.id.nav_knowledge:     //---------切換動物小知識頁面---------
                fragment = new KnowledgeFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                break;
            case R.id.nav_contact:     //---------切換相關單位頁面---------
                fragment = new AboutFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                break;
            case R.id.nav_language:     //---------切換語言---------
                final String[] language = {"中文","English"};
                final String nowLanguage = getResources().getConfiguration().locale.toString();
                int index=0;    //預設選項
                if(nowLanguage.equals("zh_TW")||nowLanguage.equals("zh")){ index=0; }
                else index=1;

                AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
                dialog_list.setTitle(R.string.main_language);
                dialog_list.setSingleChoiceItems(language, index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            if(!(nowLanguage.equals("zh"))){ switchLanguage("zh"); }
                        }else if(which==1){
                            if(!(nowLanguage.equals("en"))){ switchLanguage("en"); }
                        }
                    }
                }).show();
            default:
                break;
        }

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //-------------------切換語言--------------------
    private void switchLanguage(String language){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        if(language.equals("en")){
            conf.setLocale(Locale.ENGLISH);
        }else{
            conf.setLocale(Locale.TRADITIONAL_CHINESE);
        }
        res.updateConfiguration(conf, dm);

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    //------------------------------------------螢幕方向------------------------------------
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 什麼都不用寫
        }
        else {
            // 什麼都不用寫
        }
    }

}
