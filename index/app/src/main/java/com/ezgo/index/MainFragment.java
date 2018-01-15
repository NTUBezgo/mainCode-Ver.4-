package com.ezgo.index;


import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.getRecordDoneAsyncTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.ezgo.index.getWorksheet.getUser_id;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private View rootView;
    GoogleMap mMap;

    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;   // Google API用戶端物件
    private LocationRequest mLocationRequest;   // Location請求物件

    private MyData myData;

    private String mMarkers[][]; //存放座標
    private List<Marker> markerList = new ArrayList<Marker>(); //存放Marker
    private String targetPosition[]=new String[3]; //導航目標的位置(傳給unity)
    private Button btnAr;   //開始導航按鈕

    private int[] recordDone = new int[7]; //是否答完題目

    //存放闖關單的動物頭像
    private int[] wsIcon={R.drawable.circle_hyena, R.drawable.circle_bear, R.drawable.circle_wolf,
            R.drawable.circle_prairiedog, R.drawable.circle_kookaburra, R.drawable.circle_deer, R.drawable.gift};

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        myData=new MyData(getResources());

        try{
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mainMap);
            mapFragment.getMapAsync(MainFragment.this);
        }catch (Exception e){
            e.printStackTrace();
        }

        //檢查是否有開啟GPS
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(getActivity(), R.string.notice_gps , Toast.LENGTH_LONG).show();
        }

        //連接GOOGLE API
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        btnAr =(Button) rootView.findViewById(R.id.btnn_ar);    //開始導航按鈕

        return rootView;
    }

    /*
    private void addTestCircle(){   //-----------------------------------------測試用circle-------------------------------
        Double geofenceList[][]=myData.getGeofenceList();
        for(int i=0; i<geofenceList.length; i++){
            addTestCircle(geofenceList[i][0], geofenceList[i][1]);
        }
    }*/

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
        getActivity().setTitle(R.string.app_title); //將標題設為EZ Go
    }

    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到Google Services啟動位置更新服務，位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged

        // 建立Location請求物件
        mLocationRequest = new LocationRequest()
                .setInterval(2000)  // 設定讀取位置資訊的間隔時間為一秒（1000ms）
                .setFastestInterval(5000)   // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);   // 設定優先讀取高精確度的位置資訊（GPS）

        if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); //開啟我的位置圖層
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},102);
        }
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
            Toast.makeText(getActivity(), "google_play_service_missing", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;

            LatLng taiZoo = new LatLng(24.994909, 121.585132);

            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); /*地圖種類*/
            mMap.setMinZoomPreference(12.0f);    //設定偏好的最小縮放層級
            mMap.setMaxZoomPreference(17.0f);   //設定偏好的最大縮放層級

            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setCompassEnabled(true); /*顯示指北針*/
            uiSettings.setZoomControlsEnabled(true); //顯示縮放按鈕
            uiSettings.setMyLocationButtonEnabled(true); /*顯示自己位置按鈕*/

            moveMap(taiZoo);//移動到動物園位置
            addTileOverlay();//新增動物園地圖圖層

            checkRecordDone(); //確認有沒有答過題目

            //判斷要顯示的marker
            if(myData.getIsFromWS()){
                jumpWorksheetMarker();  //闖關單跳至地圖
            }else{
                chooseWorkSheetMarkers();  //建立各闖關單marker
            }

            //各園區marker點擊事件
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    btnAr.setVisibility(View.VISIBLE); //開始導航按鈕 顯示

                    targetPosition[0]=String.valueOf(marker.getPosition().latitude);
                    targetPosition[1]=String.valueOf(marker.getPosition().longitude);
                    targetPosition[2]=String.valueOf(marker.getTitle());

                    return false;
                }
            });

            //marker資訊視窗點擊事件
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                }
            });

            mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                @Override
                public void onInfoWindowClose(Marker marker) {
                    btnAr.setVisibility(View.INVISIBLE);  //開始導航按鈕 隱藏
                }
            });

            //addTestCircle();  //-----------------------------------------測試用circle-------------------------------

        }catch (Exception e){
            //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
            WrongActivity mWrontAct = new WrongActivity();
            String witchWrongBlock = "onMapReady";

            ActivityManager activityManager=(ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

            mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

            Intent intent = new Intent();
            intent.setClass(getActivity(), WrongActivity.class);
            startActivity(intent);

            getActivity().finish();
        }
    }

    //--------------------------------------------------開始導航----------------------------------------
    private void startAr(String targetPosition[]){
        try{
            Intent intent=new Intent();
            Bundle bundle = new Bundle();

            bundle.putString("targetLat",targetPosition[0]);
            bundle.putString("targetLng",targetPosition[1]);
            bundle.putString("targetTitle",targetPosition[2]);
            bundle.putIntArray("recordDone",recordDone); //每種動物是否答題過
            bundle.putString("nowLanguage",getWorksheet.getLanguage()); //目前語言

            intent.putExtras(bundle);
            intent.setClass(getActivity(), ArActivity.class);
            startActivity(intent);
        }catch (Exception e){
            //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
            WrongActivity mWrontAct = new WrongActivity();
            String witchWrongBlock = "startAr";

            ActivityManager activityManager=(ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

            mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

            Intent intent = new Intent();
            intent.setClass(getActivity(), WrongActivity.class);
            startActivity(intent);

            getActivity().finish();
        }
    }

    //---------------1取得闖關單座標---------------
    public void chooseWorkSheetMarkers(){
        btnAr.setText(R.string.main_startAr);  //設定文字為開始導航
        btnAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAr(targetPosition);
            }
        });

        removeMarkers();
        mMarkers=myData.getWorkSheetMarkers();
        setMarkers(1);
    }

    //-----------------2選取闖關單題目跳至其座標-----------------
    public void jumpWorksheetMarker(){
        btnAr.setText(R.string.main_startAr);  //設定文字為開始導航
        btnAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAr(targetPosition);
            }
        });

        removeMarkers();
        mMarkers=myData.getPositionFromWS();
        setMarkers(2);
        myData.setIsFromWS(false);
        LatLng position = new LatLng(Double.parseDouble(mMarkers[0][0]),Double.parseDouble(mMarkers[0][1]));
        moveMap(position);
    }

    //-----------------新增Markers-----------------
    private void setMarkers(int type){
        for(int i=0; i<mMarkers.length; i++){
            LatLng position = new LatLng(Double.parseDouble(mMarkers[i][0]),Double.parseDouble(mMarkers[i][1]));

            MarkerOptions markerOptions = new MarkerOptions();
            if(type==1){    //選取闖關單標誌
                markerOptions.position(position).icon(BitmapDescriptorFactory.fromResource(wsIcon[i])); //動物頭像Marker
            }else if(type==2){  //從闖關單頁面跳至此
                markerOptions.position(position).icon(BitmapDescriptorFactory.fromResource(wsIcon[Integer.parseInt(mMarkers[i][3])]));
            }

            Marker marker = mMap.addMarker(markerOptions);
            marker.setTitle(mMarkers[i][2]);

            markerList.add(marker);
        }
    }

    //-----------------移除所有Markers-----------------
    private void removeMarkers(){
        for (Marker marker: markerList) {
            marker.remove();
        }
        markerList.clear();
    }

    /*-------------------地理圍欄 測試用----------------------
    private void addTestCircle(Double lat,Double lng){
        LatLng latLng = new LatLng(lat,lng);

        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(25)
                .strokeWidth(0)
                .strokeColor(Color.argb(200, 255,0,0))
                .fillColor( Color.argb(50, 255,0,0) );
        mMap.addCircle( circleOptions );
    }*/

    //---------------------------------移動地圖到參數指定的位置-------------------------
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .bearing(147)
                        .zoom(16)
                        .build();

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //----------------------------------新增動物園地圖圖層------------------------------
    private void addTileOverlay(){
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                String s = String.format("http://ezgo.twjoin.com/img/map/%d/%d/%d.png",
                        zoom, x, y);

                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }

                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }

            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 12;
                int maxZoom = 17;

                if ((zoom < minZoom || zoom > maxZoom)) {
                    return false;
                }
                return true;
            }
        };

        TileOverlay tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider));
    }

    //----------------------------------確認有沒有答過題目------------------------------
    private void checkRecordDone(){
        getRecordDoneAsyncTask myAsyncTask = new getRecordDoneAsyncTask(new getRecordDoneAsyncTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray jsonArray = object.getJSONArray("recordDone");

                    for (int i = 0 ; i<jsonArray.length() ; i++) {
                        // recordDone[i] == 0 代表還沒看過這隻動物
                        // recordDone[i] == 1 代表看過這隻動物
                        recordDone[i] = Byte.valueOf(jsonArray.getJSONObject(i).getString("recordDone"));
                        //Log.e("recordDone", "" + recordDone[i]);

                        if (recordDone[i] == 1) {
                            //若答過題
                        }
                    }
                } catch (Exception e) {
                    //在witchBlock寫入這裡是哪個測試區塊的標示 如：這裡是上傳使用者資料的區塊
                    WrongActivity mWrontAct = new WrongActivity();
                    String witchWrongBlock = "mainFragment checkRecordDone";

                    ActivityManager activityManager=(ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                    String thisActivityName=activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

                    mWrontAct.setError(e.toString(),witchWrongBlock,thisActivityName);

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WrongActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });myAsyncTask.execute(Common.getRecordDoneUrl + getUser_id());
    }

}
