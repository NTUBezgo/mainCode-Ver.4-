package com.ezgo.index.Common;

public class Common {
    //--------------------------
    //(1) 若連結自己的主機
    //--------------------------
    // 確認並修改主機的ip位址
    // 若為192.168開頭的虛擬IP, 執行時模擬器與主機應使用同一分享器內之網路
    //public static String url="http://192.168.56.1:3000";

    //--------------------------
    //(2) 若連結現有測試主機
    //--------------------------
    //Get
    public static String showQuest="http://ezgo.twjoin.com/showQuest/";
    public static String getRecordDoneUrl="http://ezgo.twjoin.com/getRecordDone/";
    public static String downloadRewardDone="http://ezgo.twjoin.com/downloadRewardDone/";
    //Post
    public static String updateUserUrl="http://ezgo.twjoin.com/update";
    public static String updateAnsUrl="http://ezgo.twjoin.com/updateAns/";
    public static String updateRewardUrl="http://ezgo.twjoin.com//updateReward/";
}