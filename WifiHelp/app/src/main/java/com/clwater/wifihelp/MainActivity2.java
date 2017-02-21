package com.clwater.wifihelp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity2 extends AppCompatActivity {

    ArrayList<WIFI> finaldata = new ArrayList<WIFI>();

    @BindView(R.id.listview_wifilist)
    ListView listview_wifilist;

    private AdapterWifi adapterWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getData();
        //init();

    }

    private void checkAllPwd() {

        for (int i = 10 ; i < 15 ; i++){
            final WIFI wifi = finaldata.get(i);
            String url = String.format("http://api.wifi4.cn/Wifi.Info/?token=token&ssid=%s&bssid=%s" , wifi.getSsid() , wifi.getBssid());
            url = url.replace(" " , "%20");
            //Log.d("gzb" , url);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.d("gzb" , "error:  " + e);
                }
                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    String result_get = response.body().string();
                    //Log.d("gzb" , result_get);
                    String pwd = anaLysispwd(result_get);
                    wifi.setPwd(pwd);
                }
            });

            updateList(finaldata);
        }
    }

    private String anaLysispwd(String result_get) {
        String pwd = "";
        try {
            JSONObject json = new JSONObject(result_get);
            JSONObject _jsonobjecy = json.getJSONObject("data");
            pwd = _jsonobjecy.getString("pwd");
            if (pwd.isEmpty()) {
                Log.d("gzb", "pwd can not find");
            }else {
                Log.d("gzb" , pwd);
            }
        } catch (JSONException e) {
        }

        return pwd;
    }

    private void init() {
       // listview_wifilist = (ListView) findViewById(R.id.listview_wifilist);
    }

    private void updateList(ArrayList<WIFI> finaldata){
        ArrayList<WIFI> _f  = ordr(finaldata);


        adapterWifi = new AdapterWifi(this , _f);
        listview_wifilist.setAdapter(adapterWifi);

    }

    private ArrayList<WIFI> ordr(ArrayList<WIFI> finaldata) {
        ArrayList<WIFI> f = new ArrayList<WIFI>();
        for (int i = 0 ; i < finaldata.size() ; i ++){
            WIFI wifi = finaldata.get(i);
            if (wifi.getPwd() == null){
                f.add(wifi);
            }
        }
        return f;
    }


    private void getData(){

        WifiSearcher wifiSearcher = new WifiSearcher(this , new WifiSearcher.SearchWifiListener(){
            @Override
            public void onSearchWifiFailed(WifiSearcher.ErrorType errorType) {
                Toast.makeText(MainActivity2.this , "error" , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSearchWifiSuccess(List<WIFI> results) {
                //_results = results;
                for (int i = 0 ; i < results.size() ; i++){
                    WIFI wifi = results.get(i);
                    finaldata.add(wifi);
                }

                checkAllPwd();
            }
        });
        wifiSearcher.search();
    }


    private void updateSingle(int position , String pwd) {
        int firstVisiblePosition = listview_wifilist.getFirstVisiblePosition();
        int lastVisiblePosition = listview_wifilist.getLastVisiblePosition();

        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = listview_wifilist.getChildAt(position - firstVisiblePosition);
            TextView textView = (TextView) view.findViewById(R.id.textview_list_pwd);
            textView.setText(pwd);
        }
    }

    private void checkPwd(WIFI wifi , final int _position) {
        String url = String.format("http://api.wifi4.cn/Wifi.Info/?token=token&ssid=%s&bssid=%s" , wifi.getSsid() , wifi.getBssid());
        Log.d("gzb" , "url:  " + url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("gzb" , "error:  " + e);
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String result_get = response.body().string();
                Log.d("gzb" , result_get);

                String pwd = "";
                try {
                    JSONObject json = new JSONObject(result_get);
                    JSONObject _jsonobjecy = json.getJSONObject("data");
                    pwd = _jsonobjecy.getString("pwd");
                    if (pwd.isEmpty()) {
                        Log.d("gzb", "pwd can not find");
                    }else {
                        Log.d("gzb" , pwd);
                    }

                    updateSingle(_position , pwd);
                } catch (JSONException e) {
                }
            }
        });
    }


}
