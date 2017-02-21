package com.clwater.wifihelp;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    //private List<WIFI> _results;
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
        init();
    }

    private void init() {
       // listview_wifilist = (ListView) findViewById(R.id.listview_wifilist);

    }

    private void updateList(ArrayList<WIFI> finaldata){
        adapterWifi = new AdapterWifi(this , finaldata);
        Log.d("gzb" , "adapterWifi" + adapterWifi);
        listview_wifilist.setAdapter(adapterWifi);

    }

    private void getData(){
        //final List<String> data = new ArrayList<String>();

        WifiSearcher wifiSearcher = new WifiSearcher(this , new WifiSearcher.SearchWifiListener(){
            @Override
            public void onSearchWifiFailed(WifiSearcher.ErrorType errorType) {
                Toast.makeText(MainActivity.this , "" + errorType , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSearchWifiSuccess(List<WIFI> results) {
                //_results = results;
                for (int i = 0 ; i < results.size() ; i++){
                    WIFI wifi = results.get(i);
                    //Log.d("gzb" , "ssid: " + wifi.getSsid() + "  bssid: " + wifi.getBssid());
                    //finaldata.add("ssid: " + wifi.getSsid() + "  bssid: " + wifi.getBssid());
                    finaldata.add(wifi);
                }
                updateList(finaldata);
            }
        });
        wifiSearcher.search();

        //return data;
    }


    @OnItemClick(R.id.listview_wifilist)
    public void listOnClick(int position){
        WIFI wifi = finaldata.get(position);
        Log.d("gzb" , wifi.getSsid() + wifi.getBssid());
        checkPwd(wifi);
    }

    private void checkPwd(WIFI wifi) {
        String url = String.format("http://api.wifi4.cn/Wifi.Info/?token=token&ssid=%s&bssid=%s" , wifi.getSsid() , wifi.getBssid());
        Log.d("gzb" , url);
        OkHttpClient checkpwd_client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = checkpwd_client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("gzb" , "error: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("gzb" , response.message());
            }
        });
    }


}
