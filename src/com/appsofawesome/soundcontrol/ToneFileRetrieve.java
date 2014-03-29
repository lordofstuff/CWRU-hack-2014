package com.appsofawesome.soundcontrol;

import java.lang.reflect.Field;

//import statements for xposed framework
import ...android.xposed.XposedHelpers.findAndHookMethod;
import ...android.xposed.IXposedHookLoadPackage;
import ...android.xposed.XC_MethodHook;
import ...android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.media.Ringtone;
import android.media.MediaPlayer;
import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;

public class ToneFileRetrieve implements IXposedLoadPackage{
	
	//xposed framework:
		// from the Ringtone: 1. get private field Media Player
		//					  2. from Media Player, get AssetFileDescriptor
		//					  3. from ASF, create FileInputStream
		//					  4. read bytes directly from FIS
	
	private Ringtone tone;
	private FileInputStream fis;
	
	public static void setRingtone(Ringtone r){ tone = r; }
	
	public static FileInputStream getFIS(){ return fis; }
	
	//log when the app is loaded
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable{
		XposedBridge.log("We are running "+lpparam.packageName);
	
		//create xposed_init txt file in assets w/ entry-point class names
	
		if(!lpparam.packageName.equals("android.content.res"))
			return;
		
		XposedBridge.log("we are in content.res!");
		//Ringtone ringtone from Ringtone[] tones
		Field f = ringtone.getClass().getDeclaredField("mAudio");
		f.setAccessible(true);
		MediaPlayer mp = (MediaPlayer)f.get(ringtone);
						
		findAndHookMethod("android.content.res.AssetFileDescriptor", lpparam.classLoader, "createInputStream", new XC_MethodHook{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
				
			}
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable{
				fis = param.getResult(); // this is a guess: where is autocomplete?????
				//call another method and pass fis to it
				
			}
		});		
	}
	
	
}
