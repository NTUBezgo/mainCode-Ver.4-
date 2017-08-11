package com.ezgo.index;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.events.ChangeEvent;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RunnableFuture;

import static android.content.ContentValues.TAG;


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
    private MyData myData=new MyData();

    private Button endBtn; //結束導航按鈕

    private String targetPosition[]=new String[2]; //導航目標的位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        context=this;

        //------------開啟Unity----------------
        LinearLayout u3dLayout = (LinearLayout) findViewById(R.id.u3d_layout);
        u3dLayout.addView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        //---------------取得目標位置---------------------
        Bundle bundle=getIntent().getExtras();
        targetPosition[0]=bundle.getString("targetLat");
        targetPosition[1]=bundle.getString("targetLng");

        //--------------測試android 呼叫 unity 函式---------------------------------------------------------------------------------------
        /*sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnityPlayer.UnitySendMessage("Manager", "ZoomIn", "");
            }
        });*/

        //檢查是否有開啟GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(context, "請開啟GPS", Toast.LENGTH_LONG).show();
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
                dialog.setTitle("確定要結束嗎?");
                dialog.setPositiveButton("確定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mUnityPlayer.quit();
                    }

                });
                dialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
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

    //------------Unity取得目標位置-------------------
    public String[] getTargetPosition() {
        return targetPosition;
    }

    //-----------------------------------------接收地理圍欄intent-----------------------------------------------------------------------
    public class GoogleReceiver extends BroadcastReceiver {

        ArActivity arActivity;

        public GoogleReceiver(Activity activity){
            arActivity = (ArActivity)activity;
        }

        @Override
        public void onReceive(Context context, Intent geointent) {
            Button sendBtn=(Button) findViewById(R.id.startQ);
            sendBtn.setVisibility(View.VISIBLE);

            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startQuestion();
                }
            });
        }
    }

    //----------------開始答題--------------------------
    public void startQuestion(){
        Intent intent = new Intent();
        intent.setClass(ArActivity.this, AnimalintroActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("index",GeofenceTransitionsIntentService.WHICHAREA); //存進入的地理圍欄id
        intent.putExtras(bundle);

        startActivity(intent);
        mUnityPlayer.quit();
    }

    //---------------加入Geofence--------------
    private void addGeoFence(){
        Double geofenceList[][]=myData.getGeofenceList();

        for (int i=0; i<geofenceList.length; i++){
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(i))
                    .setCircularRegion(geofenceList[i][0], geofenceList[i][1],100)  //測試用設為100------------------------------
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

        startGeofenceMonitoring();
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
        //textView.setText("緯度:"+myLat+"\n經度:"+myLng);
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
