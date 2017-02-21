package com.clwater.wifihelp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clwater.wifihelp.EventBus.EventBusListviewUpdate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {



    @BindView(R.id.listview_wifilist)
    ListView listview_wifilist;

    private AdapterWifi adapterWifi;

    private List<WIFI> wifilist = new ArrayList<WIFI>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        ScanWifi();
    }

    private void ScanWifi() {
        WifiSearcher wifiSearcher = new WifiSearcher(this , new WifiSearcher.SearchWifiListener(){
            @Override
            public void onSearchWifiFailed(WifiSearcher.ErrorType errorType) {
                Toast.makeText(MainActivity.this , "error" , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSearchWifiSuccess(List<WIFI> results) {
                Log.d("gzb" , "scan");
                CheckAllPwd(results);
               // ShowList(results);
            }
        });
        wifiSearcher.search();
    }

    private void ShowList(List<WIFI> results) {
        adapterWifi = new AdapterWifi(this , results);
        listview_wifilist.setAdapter(adapterWifi);
    }

    private void CheckAllPwd(List<WIFI> results) {
        int index = 0 ;
        WIFI wifi = results.get(index);
        CheckPwd(wifi , results , index);
    }

    private void CheckNext(List<WIFI> results, int index) {
        index++;
        if (index < results.size()) {
            WIFI wifi = results.get(index);
            CheckPwd(wifi, results, index);
        }else {
            EventBus.getDefault().post(new EventBusListviewUpdate());
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void updateSingle(EventBusListviewUpdate e) {
//        int firstVisiblePosition = listview_wifilist.getFirstVisiblePosition();
//        int lastVisiblePosition = listview_wifilist.getLastVisiblePosition();
//
//        if (e.getIndex() >= firstVisiblePosition && e.getIndex() <= lastVisiblePosition) {
//            View view = listview_wifilist.getChildAt(e.getIndex() - firstVisiblePosition);
//            TextView textView = (TextView) view.findViewById(R.id.textview_list_pwd);
//            textView.setText(e.getPwd());
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpListDate(EventBusListviewUpdate e) {
        adapterWifi = new AdapterWifi(this , wifilist);
        listview_wifilist.setAdapter(adapterWifi);
    }

    private void CheckPwd(final WIFI wifi ,  final List<WIFI> results ,  final int index) {
        final EventBusListviewUpdate ev = new EventBusListviewUpdate();
        ev.setIndex(index);
        ev.setPwd("正在检索...");
       // EventBus.getDefault().post(ev);
        String url = String.format("http://api.wifi4.cn/Wifi.Info/?token=token&ssid=%s&bssid=%s" , wifi.getSsid() , wifi.getBssid());
        url = url.replace(" " , "%20");
        Log.d("gzb" , url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("gzb" , "error:  " + e);
                String pwd = "检索失败";
                wifi.setPwd(pwd);
                wifilist.add(wifi);
                CheckNext(results , index);

            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String result_get = response.body().string();
                Log.d("gzb" , result_get);
                String pwd = anaLysispwd(result_get);
                if (pwd.isEmpty()){
                    pwd = "检索失败";
                }
                wifi.setPwd(pwd);
                wifilist.add(wifi);
                CheckNext(results , index);
            }
        });
    }

    
    private String anaLysispwd(String result_get) {
        String pwd = "";
        try {
            JSONObject json = new JSONObject(result_get);
            JSONObject _jsonobjecy = json.getJSONObject("data");
            pwd = _jsonobjecy.getString("pwd");
            if (pwd.isEmpty()) {
                Log.d("gzb", "not");
            }else {
                Log.d("gzb" , pwd);
            }
        } catch (JSONException e) {
        }

        return pwd;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
