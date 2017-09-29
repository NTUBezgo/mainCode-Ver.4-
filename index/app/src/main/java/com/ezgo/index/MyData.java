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

    //建立各館區marker
    private String areaMarkers[][]={
            {"24.9985962","121.5805931","臺灣動物區"},{"24.9989718","121.5819383","兒童動物區"},
            {"24.9950215","121.5834188","亞洲熱帶雨林區"},{"24.9952621"," 121.5851489","沙漠動物區"},
            {"24.994184","121.5853326","澳洲動物區"},{"24.9951333","121.5880094","非洲動物區"},
            {"24.9931447","121.5896013","溫帶動物區"},{"24.9957179","121.5888946","鳥園區"},
            {"24.9929758","121.5911959","企鵝館"},{"24.9982291","121.5828744","無尾熊館"},
            {"24.9940697","121.5898494","兩棲爬蟲動物館"},{"24.9967402","121.5807004","昆蟲館"},
            {"24.9968265","121.5830956","大貓熊館"}
    };

    //建立闖關單marker
    private String workSheetMarkers[][]={
            {"24.9946605","121.5887605","斑點鬣狗"},{"24.9975801","121.5799735","臺灣黑熊"},
            {"24.9932772","121.5900815","北美灰狼"},{"24.9921553","121.5890408","黑尾草原犬鼠"},
            {"24.995106","121.583514","笑翠鳥"},{"24.9977223","121.5810719","山羌"},
            {"24.9978621","121.5818524","教育中心"}
    };


    public void setMarkers(){ //設定名稱從strings取得
        String[] array= res.getStringArray(R.array.AreaMarkers);
        String[] array1= res.getStringArray(R.array.WorksheetMarkers);

        for(int i=0; i<13; i++){ areaMarkers[i][2] = array[i]; }
        for(int i=0; i<7; i++){ workSheetMarkers[i][2] = array1[i]; }
    }

    public String[][] getAreaMarkers(){return areaMarkers;}
    public String[][] getWorkSheetMarkers(){return workSheetMarkers;}

    //闖關單選擇題圖示
    private static int worksheetImg[] ={
            R.drawable.env_hyena,R.drawable.env_bear,
            R.drawable.env_wolf,R.drawable.env_prairiedog,
            R.drawable.env_kookaburra,R.drawable.env_deer
    };

    public static int[] getWorksheetImg(){return worksheetImg;}

    //{24.9975801, 121.5799735}, //臺灣黑熊
    //{24.9977223, 121.5810719}, //山羌
    //{24.995106, 121.583514}, //笑翠鳥
    //{25.002292, 121.483586}, //測試用-----------------

    //建立地理圍欄範圍
    private Double geofenceList[][]={
            {24.9946605, 121.5887605}, //斑點鬣狗
            {25.002292, 121.483586}, //測試用-----------------
            {24.9932772, 121.5900815}, //北美灰狼
            {24.9921553, 121.5890408}, //黑尾草原犬鼠
            {24.995106, 121.583514}, //笑翠鳥
            {24.9977223, 121.5810719}, //山羌
            {24.9978621, 121.5818524} //教育中心
    };

    public Double[][] getGeofenceList(){
        return geofenceList;
    }


    //選擇是否注音
    public static int chooseFont=1;
    public void setFont(int a){
        chooseFont=a;
    }
    public int getFont(){return chooseFont;}


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
