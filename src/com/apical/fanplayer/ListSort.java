package com.apical.fanplayer;

import java.util.Comparator;

public class ListSort<ArrayList> implements Comparator<ArrayList>{

	@Override
	public int compare(Object obj1, Object obj2) {
		Device d1 = (Device)obj1;
		Device d2 = (Device)obj2;
		if(d1.getmDeviceIP().compareTo(d2.getmDeviceIP())>0)
		{
			return 1;
		}
		return -1;
	}

}
