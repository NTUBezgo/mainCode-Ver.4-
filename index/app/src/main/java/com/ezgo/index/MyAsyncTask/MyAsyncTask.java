package com.ezgo.index.MyAsyncTask;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//=========================================
// 非同步工作, 用來取回網站回傳的資料
//=========================================
public class MyAsyncTask extends AsyncTask<String, Integer, String> {

    //----------------------------------------
    // 宣告一個接收回傳結果的程式必須實作的介面
    //----------------------------------------
    public interface TaskListener {
        void onFinished(String result);
    }

    private TaskListener taskListener;

    //-----------------------------------------------------------
    // 建構元, 傳入(1)context, (2)取回資料後執行的程式
    //-----------------------------------------------------------
    public MyAsyncTask(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    //========================================
    // 由主程式呼叫.execute()方法時啟動,
    // 由主程式傳入:(1)主機網址
    //========================================
    @Override
    protected String doInBackground(String... params) {
        String data=null;
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            URL url = new URL(params[0]); //params[0] 是myNavigationAsyncTask.execute(Common.updateUrl, getId);的第一個參數
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //----------------------------------------------
            //  傳給主機的參數(name, amount, deliverDate)
            //----------------------------------------------
            OutputStream os = conn.getOutputStream();

            os.close();

            conn.connect();
            inputStream = conn.getInputStream();

            BufferedReader bufferedReader=new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"));

            data=bufferedReader.readLine();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    //------------------------------------------------------------------
    // 完成資料取回後, 由主程式的taskListener.onFinished()處理取回資料
    //------------------------------------------------------------------
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        taskListener.onFinished(result);
    }

    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}