package com.apical.fanplayer;

import java.util.ArrayList;
import java.util.LinkedList;

import com.example.fanplayer4_2_2.R;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder") public class DeviceAdapter extends BaseAdapter {

	private ArrayList<Device> mData;
    private Context mContext;
	
    public DeviceAdapter(ArrayList<Device> mData,Context mContext){
    	this.mContext = mContext;
    	this.mData    = mData;
    }
    
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Device device      = (Device) getItem(position);
		convertView        = LayoutInflater.from(mContext).inflate(R.layout.device_item_layout,parent,false);
		TextView txt_aUUID = (TextView) convertView.findViewById(R.id.device_name);
        TextView txt_aIP   = (TextView) convertView.findViewById(R.id.device_ip);
        txt_aUUID.setText(device.getmDeviceUUID());
        txt_aIP.setText("  "+device.getmDeviceIP());
        
		return convertView;
	}

}
