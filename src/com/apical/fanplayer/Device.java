package com.apical.fanplayer;

public class Device {
	
	private String deivceUUID;
	private String mDeviceIP;
	
	public Device(){
		
	}
	
	public Device(String UUID,String deviceIP){
		this.deivceUUID = UUID;
		this.mDeviceIP   = deviceIP;
	}
	
	public String getmDeviceUUID() {
		return deivceUUID;
	}
	public void setDeviceUUID(String mDeviceUUID) {
		this.deivceUUID = mDeviceUUID;
	}
	public String getmDeviceIP() {
		return mDeviceIP;
	}
	public void setDeviceIP(String mDeviceIP) {
		this.mDeviceIP = mDeviceIP;
	}
	
}
