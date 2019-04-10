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
	 * �Ƚ������ź���ļ����е�Ԫ���Ƿ����,��Ⱦͷ���true
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
		//����ļ��϶��ǰ�����ͬ�Ĺ�������İ�˳��Ƚϼ����е�Ԫ�أ���������ͬ��Ԫ����ô�������������ϲ���ͬ
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
