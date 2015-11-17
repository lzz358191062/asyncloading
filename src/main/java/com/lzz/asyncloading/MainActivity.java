package com.lzz.asyncloading;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "lzz";
    private ListView listview;
    private static final String url = "http://www.imooc.com/api/teacher?type=4&num=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) findViewById(R.id.listView);
        new NewsAsyncTask().execute(url);
        LogUtil.e(TAG, "init");

    }


    class NewsAsyncTask extends AsyncTask<String, Void, List<NewBean>> {

        @Override
        protected List<NewBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewBean> newBeans) {
            super.onPostExecute(newBeans);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this,newBeans,listview);
            listview.setAdapter(adapter);
        }
    }

    private List<NewBean> getJsonData(String url) {
        List<NewBean> beans = new ArrayList<NewBean>();
        String jsonString = "";
        try {
            jsonString = readStream(new URL(url).openStream());
            try {
                JSONObject obj = new JSONObject(jsonString);
                JSONArray array = obj.getJSONArray("data");
                for (int i=0;i<array.length();i++){
                    JSONObject item = array.getJSONObject(i);
                    NewBean bean = new NewBean();
                    bean.newsIconUrl = item.getString("picSmall");
                    bean.newsTitle = item.getString("name");
                    bean.newsContent = item.getString("description");
                    beans.add(bean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return beans;
    }

    private String readStream(InputStream in) {
        InputStreamReader reader ;
        String result="";
        try {
            String line = "";
            reader = new InputStreamReader(in,"utf-8");
            BufferedReader br = new BufferedReader(reader);
            while((line = br.readLine())!=null){
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
