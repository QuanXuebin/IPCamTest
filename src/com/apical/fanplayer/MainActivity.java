package com.apical.fanplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.example.fanplayer4_2_2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class MainActivity extends Activity {

	private static String      TAG = "FanPlayer MainActivity";
	private static final int   UPDATE_LIST        = 1;
	private static final int   SETTING_PAG        = 0;
	private static final int   PLAY_VIDEO         = 1;
	private static boolean     THREAD_RUN_STATUS  = false;
	private static boolean     onResume           = true;
	private static boolean     BROADCAST_THREAD   = true;
	private static boolean     NOTIFY_THREAD      = true;
	private Object obj                            = null;
	private Context mContext                      = null;
	private Map<String,String> mDeviceData        = null;
	private ArrayList<Device>  mDeviceList        = null;
	private DeviceAdapter      mDeviceAdapter     = null;
	private TextView           mWifiIPtxt         = null;
	private ListView           mListView          = null;
	private DatagramSocket     mDatagramSocket    = null;
	private DatagramPacket     mSendPacket        = null;
	private DatagramPacket     mRecvPacket        = null;
	private byte[]             mSendData          = null;
	private byte[]             mRecvDate          = null;
	private String             mURL               = "rtsp://192.168.0.88/video0";
	private Uri                mUri               = null;
	private Thread             mThread;
	private Thread             mNotifyThread;
	private Handler mHander = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what)
			{
			case UPDATE_LIST:
				mDeviceAdapter.notifyDataSetChanged();
				break;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initView();
        initData();
        //可以在这里绑定发送广播的端口号
		try 
		{
			mDatagramSocket = new DatagramSocket();
		} 
		catch (SocketException e) 
		{
			e.printStackTrace();
		}
        THREAD_RUN_STATUS = true;
        mThread       = new MySendThread();
        mNotifyThread = new MyNotifyThread();
		mThread.start();
		mNotifyThread.start();
    }

	private void init() {
		obj                   = new Object();
		mContext              = MainActivity.this;
		IntentFilter inFilter = new IntentFilter();
		inFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		inFilter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
	}

	private void initView() {
		mListView  = (ListView) findViewById(R.id.list_view);
		mWifiIPtxt = (TextView) findViewById(R.id.wifiIpText);
		mWifiIPtxt.setText("IPCamTest:"+getLocalIPAddress());
	}

	private void initData() {
        mDeviceList    = new ArrayList<Device>();
        mDeviceAdapter = new DeviceAdapter(mDeviceList, mContext);
        mDeviceData    = new HashMap<String,String>();
        String  tempIP = getWifiCutIP();
        for(int j=1;j<255;j++)
        {
        	mDeviceData.put(tempIP+String.valueOf(j), null);
        }
        mListView.setAdapter(mDeviceAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				showListDialog(position);
				
			}
        	
        });
        
	}
	
	/**获取本机WiFi的IP前三个数字信息**/
	public String getWifiCutIP(){
		if(getLocalIPAddress() == null || "".equals(getLocalIPAddress()))
		{
			return null;
		}
		String IP[] = getLocalIPAddress().split("\\.");
		for(int i=0;i<IP.length;i++)
		{
			Log.i(TAG,"wifi_cut: "+IP[i]);
		}
		String wifi_cut   = IP[0]+"."+IP[1]+"."+IP[2]+".";
		Log.i(TAG,"wifi_cut: "+wifi_cut);
		return wifi_cut;
	}
	

	/**用来唤醒广播发送的线程**/
	class MyNotifyThread extends Thread{
		
		@Override
	    public void run() {
			while (true){
				synchronized (obj) 
		    	{
					//只唤醒一次
					if(onResume && NOTIFY_THREAD)
					{
						NOTIFY_THREAD = false;
						obj.notify();
					}
					if(!NOTIFY_THREAD)
					{
						NOTIFY_THREAD = true;
					} 
		    	}
				
			}
		}
	}
	
	/**该线程用来发送局域网广播，查找同网段的设备，负责设备列表的加载**/
	class MySendThread extends Thread {
	    
	    @SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
	    public void run() {
	    	
	    	//发这个"uid?"机器那边就会返回机器的UUID
			mSendData   = "uid?".getBytes();
			try 
			{
				mDatagramSocket.setSoTimeout(1000);
			}
			catch (SocketException e) 
			{
				e.printStackTrace();
			}
	    	
	    	synchronized (obj) 
	    	{
	    		while(THREAD_RUN_STATUS)
		    	{
	    			if(getWifiCutIP()==null)
	    			{
	    				mWifiIPtxt.setText("没有网络");
	    				continue;
	    			}
	    			else
	    			{
	    				mWifiIPtxt.setText("IPCamTest:"+getLocalIPAddress());
	    			}
					//指定这个局域网内接收该广播的端口号
					try 
					{
						mSendPacket = new DatagramPacket(mSendData,mSendData.length,
								InetAddress.getByName(getWifiCutIP()+"255"),8313);
					} 
					catch (UnknownHostException e) 
					{
						e.printStackTrace();
					}
					mSendPacket.setData(mSendData);
					try 
					{
						mDatagramSocket.send(mSendPacket);
					} 
					catch (IOException e) 
					{
						Log.i(TAG,"发送消息出错");
					}
					mRecvDate   = null;
					mRecvDate   = new byte[256];
					mRecvPacket = new DatagramPacket(mRecvDate, 0, mRecvDate.length);
					boolean scan_thread = true;
					long  timeGetIp     = -1;
					long  timeGetIpNow  = -1;
			    	while(scan_thread)
			    	{
			    		Device device = new Device();
			    		try 
			    		{
							mDatagramSocket.setSoTimeout(1000);
						} 
			    		catch (SocketException e) 
						{
							Log.i(TAG,"接收消息超时");
						}
			    		try 
			    		{
							mDatagramSocket.receive(mRecvPacket);
						} 
			    		catch (IOException e) 
						{
			    			
			    			scan_thread = false;
			    			Log.i(TAG,"没有消息过来了");
							break;
						}
			    		String devIp    = null;
			    		boolean addFlag = true;
		    			String recvUUID = new String(mRecvPacket.getData()).trim();
		    			if(mRecvPacket.getAddress() !=null)
		    			{
		    				devIp = mRecvPacket.getAddress().getHostAddress();
		    				timeGetIp = System.currentTimeMillis();
		    				mDeviceData.put(devIp, String.valueOf(timeGetIp));
		    				device.setDeviceUUID(recvUUID);
			    			device.setDeviceIP(devIp);
			    			//相同地址的设备不用重复添加+++++++++++++++++++
			    			if(mDeviceList.size()==0)
			    			{
			    				Log.i(TAG,"reset");
			    				mDeviceList.add(device);
			    			}
			    			else
			    			{
			    				for(int i=0;i<mDeviceList.size();i++)
			    				{
			    					if(mDeviceList.get(i).getmDeviceIP().equals(devIp))
			    					{
			    						addFlag = false; 
			    					}
			    				}
			    				if(addFlag)
			    				{
			    					mDeviceList.add(device);
			    				}
			    			}
			    			//相同地址的设备不用重复添加--------------------
							Log.i(TAG,"devIp: "+devIp);
							Log.i(TAG,"recvUUID: "+recvUUID);
							Log.i(TAG,"mData size: "+mDeviceList.size());
		    			}
		    			
			    	}
			    	Collections.sort(mDeviceList,new ListSort());
			    	
					timeGetIpNow = System.currentTimeMillis();
					for(String str:mDeviceData.keySet())
					{
						String strValue = mDeviceData.get(str);
						if(strValue != null)
						{
							Long timeValue = Long.valueOf(strValue);
							for(int i=0;i<mDeviceList.size();i++)
							{
								String tempIp  = mDeviceList.get(i).getmDeviceIP();
								if(tempIp.equals(str))
								{
									//间隔时长达到5秒就从list集合中移除
									if((timeGetIpNow - timeValue)>6*1000)
									{
										mDeviceList.remove(i);
									}
								}
							}
						}
						
					}
					Collections.sort(mDeviceList,new ListSort());
					Message msg  = mHander.obtainMessage();
					msg.what     = UPDATE_LIST;
					mHander.sendMessage(msg);
					//只唤醒一次
					if(onResume && BROADCAST_THREAD)
					{
						BROADCAST_THREAD = false;
						obj.notify();
					}
					if(!onResume)
					{
						try 
						{
							BROADCAST_THREAD = true;
							obj.wait();
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					}
					//每隔一秒发一次局域网广播
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
		    	}//while end	
			}
	    	
	    }
	}

	/**
     * Activity准备好和用户进行交互的时候被调用
     */
    @Override
    protected void onResume() {
        super.onResume();
        onResume = true;
    }

    /**
     * Activity准备去启动或者恢复另一个Activity的时候调用
     */
    @Override
    protected void onPause() {
        super.onPause();
        onResume = false;
    }
	
	@Override  
    protected void onDestroy() {  
        super.onDestroy();  
        THREAD_RUN_STATUS = false;
    }
	
	private void showListDialog(final int position) {
	    final String[] items = { "管理页面","播放视频" };
	    AlertDialog.Builder listDialog = 
	        new AlertDialog.Builder(MainActivity.this,R.style.Dialog);
	    listDialog.setItems(items, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            // which 下标从0开始
	            // ...To-do
	            switch(which)
	            {
	            case SETTING_PAG:
	            	Toast.makeText(MainActivity.this, 
	    	                "你点击了" + items[which], 
	    	                Toast.LENGTH_SHORT).show();
	            	
	            	mURL   = "http://"+mDeviceList.get(position).getmDeviceIP()+"/index.html";
	            	mUri   = Uri.parse(mURL);
	            	Intent intent_html = new Intent(Intent.ACTION_VIEW,mUri);
	            	startActivity(intent_html);
	            	break;
	            case PLAY_VIDEO:
	            	mURL    = "rtsp://"+mDeviceList.get(position).getmDeviceIP()+"/video0";
					mUri    = Uri.parse(mURL);
					Intent intent_rtsp = new Intent("player",mUri);
					intent_rtsp.addCategory("com.apical.player");
					startActivity(intent_rtsp);
	            	break;
	            }
	        }
	    });
	    listDialog.show();
	}
	
	public String getLocalIPAddress() {
	    String ip = "";
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
	            en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
	                enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address){
	                    ip = inetAddress.getHostAddress();
	                    break;
	                }
	            }
	            if (!TextUtils.isEmpty(ip)) break;
	        }
	    } catch (Exception e) {
	        ip = "";
	    }
	    return ip;
	}
	
		
}
