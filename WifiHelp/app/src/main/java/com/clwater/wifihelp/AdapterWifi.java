package com.clwater.wifihelp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gengzhibo on 17/2/21.
 */

public class AdapterWifi extends BaseAdapter {

    private Context context;
    private ArrayList<WIFI> datas;

    /**
     * 构造方法
     */
    public AdapterWifi(Context context,ArrayList<WIFI> datas){
        this.context=context;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder=null;
        if (convertView==null) {
            convertView=View.inflate(context, R.layout.listview_wifi, null);
            mHolder=new ViewHolder(convertView);
            convertView.setTag(mHolder);
        }else {
            mHolder=(ViewHolder) convertView.getTag();
        }

        WIFI wifi = datas.get(position);

        mHolder.textview_list_ssid.setText(wifi.getSsid());
        mHolder.textview_list_bssid.setText(wifi.getBssid());

        return convertView;
    }

    static class ViewHolder{
        @BindView(R.id.textview_list_ssid)
        TextView textview_list_ssid;
        @BindView(R.id.textview_list_bssid)
        TextView textview_list_bssid;

        public ViewHolder(View v) {
            ButterKnife.bind(this,v);
        }
    }
}