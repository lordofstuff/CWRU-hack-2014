package com.appsofawesome.soundcontrol;


import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.*;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import static de.robv.android.xposed.XposedHelpers.getObjectField;


public class AudioHook implements IXposedHookLoadPackage{

	Context context = null;
	boolean walking;

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.phone")) {
			//XposedBridge.log("in package: " + lpparam.packageName);
			return;
		}
			
		//need this as parameter for hooking. 
		Class connectionClass = findClass("com.android.internal.telephony.Connection", lpparam.classLoader);

		findAndHookMethod("com.android.phone.CallNotifier", lpparam.classLoader, "startIncomingCallQuery", connectionClass, hook); 


	}

	XC_MethodHook hook = new XC_MethodHook() {
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			//at this point, check audio, movement, screen state, 
			//first, get the context though
			Object thisThing = param.thisObject;
			context = (Context) getObjectField(thisThing, "mApplication");
			//start polling accelerometer
			Thread t = new Thread(runner);
			//poll audio
			recorder record = new recorder();
			short[] array = record.record(1);
			double[] results = Processing.amplitude_ratio(array, (short) recorder.RECORDER_SAMPLERATE, (short) 30000);
			Utils.updateVolume(results[1], results[0], context);
			t.start();
			XposedBridge.log("AudioThingy! walking: " + walking + ", volume: " + results[1]);
		}
		@Override
		protected void afterHookedMethod(MethodHookParam param) throws Throwable {

		}
	};

	Runnable runner = new Runnable() {
		MotionSense sense;
		@Override
		public void run() {
			if (sense == null || !sense.recording) {
				sense = new MotionSense();
				sense.pollMotion(context);
				//((Button) view).setText("recording");
			}
			else {
				walking = sense.isWalking(sense.pollStop());

			}		
		}	
	};

	//	



}
