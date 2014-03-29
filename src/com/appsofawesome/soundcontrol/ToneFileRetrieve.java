package com.appsofawesome.soundcontrol;

import java.lang.reflect.Field;

//import statements for xposed framework
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.media.Ringtone;
import android.media.MediaPlayer;
import android.content.res.AssetFileDescriptor;
import java.io.FileInputStream;

public class ToneFileRetrieve implements IXposedHookLoadPackage{
	
	//xposed framework:
		// from the Ringtone: 1. get private field Media Player
		//					  2. from Media Player, get AssetFileDescriptor
		//					  3. from ASF, create FileInputStream
		//					  4. read bytes directly from FIS
	
	private static Ringtone tone;
	private static FileInputStream fis;
	
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
		
		Field f = tone.getClass().getDeclaredField("mAudio");
		f.setAccessible(true);
		MediaPlayer mp = (MediaPlayer)f.get(tone);
						
		findAndHookMethod("android.content.res.AssetFileDescriptor", lpparam.classLoader, "createInputStream", new XC_MethodHook(){
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
				
			}
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable{
				fis = (FileInputStream) param.getResult(); // this is a guess: where is autocomplete?????
				//call another method and pass fis to it
				
			}
		});		
	}
	
	
}
