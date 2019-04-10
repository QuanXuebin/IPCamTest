package com.apical.fanplayer;

import java.util.ArrayList;

public class Device {
	
	private String deviceUUID;
	private String mDeviceIP;
	
	public Device(){
		
	}
	
	public Device(String UUID,String deviceIP){
		this.deviceUUID = UUID;
		this.mDeviceIP   = deviceIP;
	}
	
	public String getmDeviceUUID() {
		return deviceUUID;
	}
	public void setDeviceUUID(String mDeviceUUID) {
		this.deviceUUID = mDeviceUUID;
	}
	public String getmDeviceIP() {
		return mDeviceIP;
	}
	public void setDeviceIP(String mDeviceIP) {
		this.mDeviceIP = mDeviceIP;
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Device)
		{
			Device device = (Device) obj;
			if(deviceUUID == null || device.getmDeviceUUID() == null)
			{
				return false;
			}
			else {
				return deviceUUID.equals(device.getmDeviceUUID());
			}
		}
		return false;
	}
	/**
	 * 比较两个排好序的集合中的元素是否相等,相等就返回true
	 * */
	public boolean listEquals(ArrayList<Device> oldList,ArrayList<Device> newList)
	{
		int tempSize;
		if(oldList.size() < newList.size())
		{
			tempSize = oldList.size();
		}
		else 
		{
			tempSize = newList.size();
		}
		//传入的集合都是按照相同的规则排序的按顺序比较集合中的元素，遇到不相同的元素那么就是这两个集合不相同
		for(int i=0;i<tempSize;i++)
		{
			if(oldList.get(i).equals(newList.get(i)))
			{
				continue;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	
}
