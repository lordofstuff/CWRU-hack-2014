package com.appsofawesome.soundcontrol;

import android.media.RingtoneManager;
import android.media.Ringtone;
import android.app.*;
import android.database.Cursor;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.io.File;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;

//grabs all ringtones from device and orders them by dominant frequency using fft
public class RingSort{	
	
	private static short[][] datas;
	private static int numTones;
	
	private static int samplesize = 1024; //bytes
	
	public static Ringtone[] getTones(){
		Ringtone[] tones;
		RingtoneManager rm = new RingtoneManager(new Activity());
		rm.setType(RingtoneManager.TYPE_RINGTONE);
		
		Cursor cursor = rm.getCursor();
		cursor.moveToFirst();
		numTones = cursor.getColumnCount();
		tones = new Ringtone[numTones];
		
		for(int i = 0; i < cursor.getColumnCount(); i++){
			tones[i] = rm.getRingtone(i);
			cursor.move(1);
		}
		return tones;
	}
	
	public static short[] toShortArray(FileInputStream fis){
		short[] data = new short[samplesize];
		int i = 0;
		try{
		while(i < samplesize && (data[i] = (short)(fis.read())) >= 0) i++;
		}catch(IOException e){
			return null;
		}		
		return data;
	}
	
	public static ArrayList<ArrayList<Ringtone>> sort(){
		Ringtone[] tones = getTones();
		datas = new short[numTones][samplesize];
		for(int i=0; i < numTones; i++){
			ToneFileRetrieve.setRingtone(tones[i]);
			datas[i] = toShortArray(ToneFileRetrieve.getFIS()); //could be null
		}
		double[][] stats = new double[numTones][3]; //interested in dominant freq = stats[2]
		//find median
		double low = stats[0][2], high = stats[0][2];
		for(int i=1; i < numTones; i++){
			if(stats[i][2] > high) high = stats[i][2];
			if(stats[i][2] < low) low = stats[i][2];
		}
		double median;
		if(high != low)
			 median = (high-low)/2;
		else median = high;
		ArrayList<Ringtone> lows = new ArrayList<Ringtone>();
		ArrayList<Ringtone> highs = new ArrayList<Ringtone>();
		for(int i=0; i < numTones; i++){
			if(stats[i][2] < median) lows.add(tones[i]);
			else highs.add(tones[i]);
		}
		ArrayList<ArrayList<Ringtone>> sorted = new ArrayList<ArrayList<Ringtone>>();
		sorted.add(lows);
		sorted.add(highs);
		return sorted;
	}

}
