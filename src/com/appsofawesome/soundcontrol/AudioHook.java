package com.appsofawesome.soundcontrol;


import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.*;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findField;

import static de.robv.android.xposed.XposedHelpers.getObjectField;


public class AudioHook implements IXposedHookLoadPackage{

	Context context = null;
	boolean[] walkingLaying;
	SharedPreferences prefs;

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.phone")) {
			//XposedBridge.log("in package: " + lpparam.packageName);
			return;
		}
//		else if (lpparam.packageName.equals("com.appsofawesome.SoundControl")) {
//			(com.appsofawesome.soundcontrol.TestActivity.class).
//		}

		//need this as parameter for hooking. 
		Class connectionClass = findClass("com.android.internal.telephony.Connection", lpparam.classLoader);

		findAndHookMethod("com.android.phone.CallNotifier", lpparam.classLoader, "startIncomingCallQuery", connectionClass, hook); 
		//this next method signature differs slightly between 4.3 and 4.4. in 4.3 there is no parameter. 
		findAndHookMethod("com.android.phone.CallNotifier", lpparam.classLoader, "onCustomRingQueryComplete",/* connectionClass,*/ hook2);

	}

	XC_MethodHook hook = new XC_MethodHook() {
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
			//at this point, check audio, movement, screen state, 
			//first, get the context though
			Object thisThing = param.thisObject;
			context = (Context) getObjectField(thisThing, "mApplication");
			//prefs = context.getSharedPreferences(
				      //"com.appsofawesome.SoundControl", Context.MODE_WORLD_READABLE);
			
			
			
//			if (!prefs.getBoolean("xposed_switch", true)) {
//				return;
//			}
			Thread t = null;
//			if (isWalkingFeatureOn() || isLayingFeatureOn()) {
				//start polling accelerometer
				t = new Thread(runner);
				t.start();
//			}
			short[] array = null;
			double[] results = null;
//			if (isSoundSampleOn() || isRingtoneChangeOn()) {
				//poll audio
				recorder record = new recorder();
				array = record.record(1);
				results = Processing.amplitude_ratio(array, (short) recorder.RECORDER_SAMPLERATE, (short) 30000);
//			}
			//Utils.updateVolume(results[1], results[0], Utils.context);
			
			if (t!= null) {
				//stop recording the  motion and  get a result there
				runner.run();
			}
			Utils.updateSoundSettings(context, (float) results[0], (float) results[1], walkingLaying[0], walkingLaying[1], true);
			XposedBridge.log("AudioThingy! walking: " + walkingLaying[0] + ", volume: " + results[0] + " Laying: " + walkingLaying[1]);
		}
		@Override
		protected void afterHookedMethod(MethodHookParam param) throws Throwable {

		}
	};

	XC_MethodHook hook2 = new XC_MethodHook() {
		@Override
		protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//			if (isRingtoneChangeOn()) {
				XposedBridge.log("This is where I would change the ringtone.");
				// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android-apps/4.4.2_r1/com/android/phone/CallNotifier.java#CallNotifier.onCustomRingQueryComplete%28com.android.internal.telephony.Connection%29
				Field r = findField(findClass("com.android.phone.CallNotifier", ((Context) param.thisObject).getClassLoader()), "mRinger");
				//			Ringer r = mRinger;
				//			r.setCustomRingtoneUri(ci.contactRingtoneUri);
				findMethod
//			}
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
				walkingLaying = sense.isWalking(sense.pollStop());
			}		
		}	
	};

//	protected boolean isRingtoneChangeOn() {
//		return prefs.getBoolean("frequency_switch", true);
//		
//	}
//
//	protected boolean isLayingFeatureOn() {
//		return prefs.getBoolean("laying_switch", true);
//		
//	}
//
//	protected boolean isWalkingFeatureOn() {
//		return prefs.getBoolean("walking_switch", true);
//	}
//
//	protected boolean isSoundSampleOn() {
//		return prefs.getBoolean("sound_sample_switch", true);
//	}

}
