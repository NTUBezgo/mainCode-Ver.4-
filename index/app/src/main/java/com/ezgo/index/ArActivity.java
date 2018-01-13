package com.ezgo.index;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.Locale;


public class ArActivity extends UnityPlayerActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    Context context;

    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;   // Google API用戶端物件
    private LocationRequest mLocationRequest;   // Location請求物件

    private ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private PendingIntent mGeofencePendingIntent;
    private MyData myData;

    private Button endBtn; //結束導航按鈕
    private TextView tv_arrival; //顯示抵達哪裡

    private String targetPosition[]=new String[3]; //導航目標的位置
    private int[] recordDone = new int[7]; //是否答完題目
    private String mMarkers[][]; //存放座標
    private TextView tv_distance;
    private LinearLayout linearLayout2;

    private float distence[] = new float[1]; //距離

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //---------------取得目標位置---------------------
        Bundle bundle=getIntent().getExtras();
        targetPosition[0]=bundle.getString("targetLat");
        targetPosition[1]=bundle.getString("targetLng");
        targetPosition[2]=bundle.getString("targetTitle");

        recordDone=bundle.getIntArray("recordDone");
        recordDone[6]=0; //教育中心不答題

        ///---------------取得目前語言---------------------
        String language=bundle.getString("nowLanguage");
        setLanguagee(language); //設定語言

        setContentView(R.layout.activity_ar); //-----先改語系再setContentView
        context=this;

        myData=new MyData(getResources());
        tv_arrival = (TextView) findViewById(R.id.tv_arrival); //顯示抵達哪一動物
        mMarkers=myData.getWorkSheetMarkers();
        tv_distance = (TextView) findViewById(R.id.textDistence);
        linearLayout2 = (LinearLayout) findViewById(R.id.linear2);


        //------------開啟Unity----------------
        LinearLayout u3dLayout = (LinearLayout) findViewById(R.id.u3d_layout);
        u3dLayout.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        //---------------設定Loading幀動畫--------------------
        ImageView mImageViewFilling = (ImageView) findViewById(R.id.loadingDog);
        ((AnimationDrawable) mImageViewFilling.getBackground()).start();
        mHandler.sendEmptyMessageDelayed(LOADING_OVER, 6000); //隱藏Loading頁------------



        //檢查是否有開啟GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(context, R.string.notice_gps, Toast.LENGTH_LONG).show();
        }

        //連接GOOGLE API
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //結束導航按鈕
        endBtn = (Button) findViewById(R.id.endBtn);
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ArActivity.this);
                dialog.setTitle(R.string.notice_end);
                dialog.setPositiveButton(R.string.notice_sure,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mUnityPlayer.quit();
                    }

                });
                dialog.setNegativeButton(R.string.notice_cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }

                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });



        //接收地理圍欄intent
        LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(this);
        GoogleReceiver receiver = new GoogleReceiver(this);
        lbc.registerReceiver(receiver, new IntentFilter("googlegeofence"));

    }

    //---------------設定語言------------------
    private void setLanguagee(String language){
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        if(language.contains("zh")){
            conf.setLocale(Locale.TRADITIONAL_CHINESE);
        }else{
            conf.setLocale(Locale.ENGLISH);
        }
        res.updateConfiguration(conf, dm);
    }

    //------------隱藏Loading頁-------------------
    private static final int LOADING_OVER = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOADING_OVER:     //若已經答過題-->跳至Loading頁
                    View view = (View) findViewById(R.id.loadingPage);
                    view.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

    };

    //------------Unity取得目標位置-------------------
    public String[] getTargetPosition() {
        return targetPosition;
    }


    //------------------------------接收地理圍欄intent-----------------------------------------------
    public class GoogleReceiver extends BroadcastReceiver {
        ArActivity arActivity;
        String geoFrom;

        public GoogleReceiver(Activity activity){
            arActivity = (ArActivity)activity;
        }

        @Override
        public void onReceive(Context context, Intent geointent) {
            Button sendBtn=(Button) findViewById(R.id.startQ); //開始答題按鈕

            Bundle geobundle=geointent.getExtras();
            geoFrom=geobundle.getString("from");
            int where=GeofenceTransitionsIntentService.WHICHAREA;

            try {
                if(geoFrom.equals("enter")){  //----------------------若進入範圍內

                    UnityPlayer.UnitySendMessage("Main Camera", "changeAni", "true");  //呼叫unity函式設定動作

                    if(where==6){
                        tv_arrival.setText(getString(R.string.ar_arrived) +getString(R.string.ar_Ec));
                    }else{

                        if(recordDone[where]==1){ //作答過
                            sendBtn.setVisibility(View.GONE);
                            tv_arrival.setText(getString(R.string.ar_arrived) + mMarkers[where][2] + getString(R.string.ar_alreadyDone));
                        }else if(recordDone[where]==0){ //沒作答過
                            sendBtn.setVisibility(View.VISIBLE);
                            tv_arrival.setText(getString(R.string.ar_arrived) + mMarkers[where][2] + getString(R.string.ar_arrived1));
                        }

                        linearLayout2.setVisibility(View.VISIBLE);

                        ///Toast.makeText(ArActivity.this, R.string.ar_enterRange ,Toast.LENGTH_SHORT).show();

                        sendBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startQuestion();
                            }
                        });
                    }

                }else if(geoFrom.equals("exit")){ //----------------------若離開範圍
                    UnityPlayer.UnitySendMessage("Main Camera", "changeAni", "false"); //呼叫unity函式設定動作
                    //Toast.makeText(ArActivity.this, R.string.ar_exitRange ,Toast.LENGTH_SHORT).show();
                    linearLayout2.setVisibility(View.INVISIBLE);
                }

            }catch (Exception e){
                //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
                WrongActivity mWrontAct = new WrongActivity();
                String witchWrongBlock = "enter or exit geofence";

                ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

                mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

                Intent intent = new Intent();
                intent.setClass(ArActivity.this, WrongActivity.class);
                startActivity(intent);

                finish();
            }
        }
    }

    //----------------開始答題--------------------------
    public void startQuestion(){
        try{
            Intent intent = new Intent();
            intent.setClass(ArActivity.this, AnimalintroActivity.class);

            Bundle bundle = new Bundle();
            bundle.putInt("index",GeofenceTransitionsIntentService.WHICHAREA); //存進入的地理圍欄id
            intent.putExtras(bundle);

            startActivity(intent);
            mUnityPlayer.quit();
        }catch (Exception e){
            //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
            WrongActivity mWrontAct = new WrongActivity();
            String witchWrongBlock = "startQuestion";

            ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

            mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

            Intent intent = new Intent();
            intent.setClass(ArActivity.this, WrongActivity.class);
            startActivity(intent);

            finish();
        }
    }

    //---------------加入Geofence--------------
    private void addGeoFence(){
        Double geofenceList[][]=myData.getGeofenceList();

        for (int i=0; i<geofenceList.length; i++){
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(i))
                    .setCircularRegion(geofenceList[i][0], geofenceList[i][1],25)  //測試用 原為25------------------------------
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    //--------------建立Geofence----------------
    private void startGeofenceMonitoring(){
        try{
            //加入Geofence
            addGeoFence();

            // 建立Geofence請求物件
            GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofences(mGeofenceList)
                    .build();

            mGeofencePendingIntent = getGeofencePendingIntent();
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, geofenceRequest,mGeofencePendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()){
                                //Log.e(TAG, "Successfully added geofence");
                                //Toast.makeText(ArActivity.this,"Geofence成功",Toast.LENGTH_SHORT).show();
                            }else{
                                //Log.e(TAG, "Failed to add geofence"+status.getStatus());
                                //Toast.makeText(ArActivity.this,"Geofence失敗",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }catch (SecurityException e){
        }
    }

    private PendingIntent getGeofencePendingIntent(){
        if(mGeofencePendingIntent != null){
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //--------------停止Geofence----------------
    private void stopGeofenceMonitoring(){
        ArrayList<String> geofenceIds = new ArrayList<String>();
        Double geofenceList[][]=myData.getGeofenceList();

        for(int i=0; i<geofenceList.length; i++){
            geofenceIds.add(String.valueOf(i));
        }

        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,geofenceIds);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        // 移除位置請求服務
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            stopGeofenceMonitoring();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // 移除Google API用戶端連線
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // 連線到Google API用戶端
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到Google Services
        // 啟動位置更新服務
        // 位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged

        // 建立Location請求物件
        mLocationRequest = new LocationRequest()
                .setInterval(2000)  // 設定讀取位置資訊的間隔時間為一秒（1000ms）
                .setFastestInterval(2000)   // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);   // 設定優先讀取高精確度的位置資訊（GPS）

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        startGeofenceMonitoring(); //建立地理圍欄
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Google Services連線中斷
        // int參數是連線中斷的代號
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, "google_play_service_missing", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置

        double myLat=location.getLatitude();
        double myLng=location.getLongitude();

        //distanceBetween(現在的緯度,現在的經度,目標緯度,目標經度,儲存的變數(是一個陣列)) 單位：公尺
        Location.distanceBetween(myLat,myLng,Float.valueOf(targetPosition[0]),Float.valueOf(targetPosition[1]) ,distence);
        tv_distance.setText(getString(R.string.ar_distence) + targetPosition[2] + getString(R.string.ar_distence1) +  "：" + (int)distence[0] + getString(R.string.ar_meter));
    }


    //------------------------------------------螢幕方向------------------------------------
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 什麼都不用寫
        }
        else {
            // 什麼都不用寫
        }
    }

}
