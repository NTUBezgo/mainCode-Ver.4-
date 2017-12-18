package com.ezgo.index;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by 8320E on 2017/4/5.
 */

public class MyData{

    /*private Context context;

    public MyData(Context current){
        this.context=current;
    }

    String[] array= context.getResources().getStringArray(R.array.AreaMarkers);
    */


    private Resources res;

    public MyData(Resources res){
        this.res=res;
        setMarkers();
    }

    //建立闖關單marker
    private String workSheetMarkers[][]={
            {"24.9946605","121.5887605","斑點鬣狗"},{"24.9975801","121.5799735","臺灣黑熊"},
            {"24.9932772","121.5900815","北美灰狼"},{"24.9921553","121.5890408","黑尾草原犬鼠"},
            {"24.995106","121.583514","笑翠鳥"},{"24.9977223","121.5810719","山羌"},
            {"24.9978621","121.5818524","教育中心"}
    };


    public void setMarkers(){ //設定名稱從strings取得
        String[] array1= res.getStringArray(R.array.WorksheetMarkers);
        for(int i=0; i<7; i++){ workSheetMarkers[i][2] = array1[i]; }
    }

    public String[][] getWorkSheetMarkers(){return workSheetMarkers;}

    //闖關單選擇題圖示
    private static int worksheetImg[] ={
            R.drawable.env_hyena,R.drawable.env_bear,
            R.drawable.env_wolf,R.drawable.env_prairiedog,
            R.drawable.env_kookaburra,R.drawable.env_deer
    };

    public static int[] getWorksheetImg(){return worksheetImg;}

    //建立地理圍欄範圍
    private Double geofenceList[][]={
            {24.9946605, 121.5887605}, //斑點鬣狗
            {24.9975801, 121.5799735}, //臺灣黑熊
            {24.9932772, 121.5900815}, //北美灰狼
            {24.9921553, 121.5890408}, //黑尾草原犬鼠
            {24.995106, 121.583514}, //笑翠鳥
            {24.9977223, 121.5810719}, //山羌
            {24.9978621, 121.5818524} //教育中心
    };

    public Double[][] getGeofenceList(){
        return geofenceList;
    }

    //闖關單icon跳至主畫面
    private static boolean isFromWS=false;
    private static String position[][]=new String[1][4];

    public void fromWS(int i){
        position[0][0]=workSheetMarkers[i][0];
        position[0][1]=workSheetMarkers[i][1];
        position[0][2]=workSheetMarkers[i][2];
        position[0][3]=String.valueOf(i);
        setIsFromWS(true);
    }
    public void setIsFromWS(boolean a) {isFromWS=a;}
    public boolean getIsFromWS(){ return isFromWS;}
    public String[][] getPositionFromWS(){return position;}


}
