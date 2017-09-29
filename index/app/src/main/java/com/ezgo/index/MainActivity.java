package com.ezgo.index;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.TextView;
import android.widget.Toast;

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
    int choosefont;

    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        myData =new MyData(getResources());
        choosefont = myData.getFont();
        fragmentManager = getSupportFragmentManager();

        //--------------設定ActionBar-----------------
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //--------------設定預設字型-----------------
        if(choosefont==1) FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/wt011.ttf");
        else if(choosefont==2) FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/wp010-08.ttf");

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

    //------------------------------------------選取闖關單list跳至其座標-----------------------------------------
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
            case R.id.action_area:  //選擇顯示館區的Markers
                mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_frame);
                mainFragment.chooseAreaMarkers();
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
                mMenu.findItem(R.id.action_area).setVisible(true);
                break;
            case R.id.nav_worksheet:     //---------切換闖關單頁面---------
                fragment = new WorkSheetFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                mMenu.findItem(R.id.action_area).setVisible(false);
                break;
            case R.id.nav_dogInfo:     //---------切換旺哥小檔案頁面---------
                fragment = new DoginfoFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                mMenu.findItem(R.id.action_area).setVisible(false);
                break;
            case R.id.nav_info:     //---------切換闖關說明頁面---------
                fragment = new InformationFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                mMenu.findItem(R.id.action_area).setVisible(false);
                break;
            case R.id.nav_contact:     //---------切換相關單位頁面---------
                fragment = new AboutFragment();
                fragmentTransaction.replace(R.id.main_frame,fragment);
                mMenu.findItem(R.id.action_worksheet).setVisible(false);
                mMenu.findItem(R.id.action_area).setVisible(false);
                break;
            case R.id.nav_font:     //---------切換注音---------
                if(choosefont==1){
                    myData.setFont(2);
                }else if(choosefont==2){
                    myData.setFont(1);
                }
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            default:
                break;
        }

        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
