package com.ezgo.index;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.ezgo.index.Common.Common;
import com.ezgo.index.MyAsyncTask.getterAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by thunder on 2017/5/16.
 */

public class getWorksheet {

    private static String[] question= new String[100];
    private static String[] answer= new String[100];
    private static String[] description= new String[100];
    private static String[] option= new String[100];
    private static String[] record_question_id= new String[100];
    private static String[] option_question_id= new String[100];
    private static String[] recordDone= new String[100];
    private static String userDone= "";
    private static String user_id;
    private static int optionLength ;
    private static int questionLength ;
    private static String nowLanguage ;
    private static String common = "";


    public static void getJSON() {
        //Log.v("language",MainActivity.getLanguage().equals("en")+"");
        // Log.v("language",getWorksheet.getUserDone()+"");

        if(nowLanguage.contains("zh")){
            common = Common.showQuest;
        }else{
            common = Common.showQuest_en;
        }

        getterAsyncTask myAsyncTask = new getterAsyncTask(new getterAsyncTask.TaskListener() {

            @Override
            public void onFinished(String result) {
                try {
                    JSONObject object = new JSONObject(result);

                    JSONArray jsonArray = object.getJSONArray("record");

                    int j=0;
                    questionLength = jsonArray.length();
                    for (int i = 0 ; i<jsonArray.length() ; i++){
                        question[i] = jsonArray.getJSONObject(i).getString("question");
                        answer[i] = jsonArray.getJSONObject(i).getString("answer");
                        description[i] = jsonArray.getJSONObject(i).getString("description");
                        record_question_id[i] = jsonArray.getJSONObject(i).getString("question_id");
                    }
                    jsonArray = object.getJSONArray("option");

                    for (int i = 0 ; i<jsonArray.length() ; i++) {
                        option[i] = jsonArray.getJSONObject(i).getString("qOption");
                        option_question_id[i] = jsonArray.getJSONObject(i).getString("question_id");
                    }

                    optionLength = jsonArray.length();

                } catch (Exception e) {
                }
            }
        });
        myAsyncTask.execute(common + getUser_id());
    }
    //-----------------使用者的題目清單maxIndex:15
    public static String getQuestion(int i){ return question[i];}
    public static int getAnswer(int i){ return Integer.valueOf(answer[i]); }
    public static String getDescription(int i){ return description[i]; }
    public static String getRecord_Question_id(int i){ return record_question_id[i]; }
    public static String getRecordDone(int i){ return recordDone[i]; }
    //-----------------使用者的選項，索引的範圍不確定，
    public static String getOption(int i){ return option[i]; }
    public static String getOptionQuestion_id(int i){ return option_question_id[i]; }
    //------------------
    public static int getOptionLength(){ return optionLength; }
    public static int getQuestionLength(){ return questionLength; }
    public static int getUser_id(){ return  Integer.valueOf(user_id) ; }  //user_id 為數字型態
    public static String getUserDone(){ return  userDone; } //是否兌換過獎品
    protected static void setLanguage(String Language){  nowLanguage = Language; }//設定目前系統語言
    protected static String getLanguage(){ return nowLanguage; }
    protected static void postUser_id(String user){ user_id = user ; }
    protected static void postRecordDone(String sendRecordDone,int i){ recordDone[i] = sendRecordDone; }
    protected static void postUserDone(String userdone){ userDone = userdone; }

}
