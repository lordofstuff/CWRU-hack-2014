package com.appsofawesome.soundcontrol;
import java.lang.reflect.Method;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.*;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findMethodExact;
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
		//XposedBridge.log("in Phone.");
//		Method getContext = findMethodExact("android.content.ContextWrapper", lpparam.classLoader, "getBaseContext");
//		Method getPhoneApp = findMethodExact("com.android.phone.PhoneApp" , lpparam.classLoader, "getInstance");
//		context = (Context) getContext.invoke(getPhoneApp.invoke(null, null), null);
//		

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
			updateVolume(results[1], results[0]);
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

	private void updateVolume(double lowToHigh, double volume) {
		//get transformed volume (translate a noise level to a volume on the device
		int deviceVolume = transformVolume(volume, AudioManager.STREAM_RING);
		setVolume(AudioManager.STREAM_RING, deviceVolume);

	}

	private int transformVolume(double volume, int stream) {
		//Log.d(TAG, "volume: " + volume);
		final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audio.getStreamMaxVolume(stream);
		double normVolume = ((volume - getMinVol()) / (getMaxVol() - getMinVol()));
		return  round((normVolume * maxVolume));
	}
	
	private int round(double d) {
		if ((d * 100) % 100 < 50) {
			return (int) d;
		}
		return (int) d + 1;
	}

	private int getMaxVol() {
		// TODO Auto-generated method stub
		return 12;
	}

	private int getMinVol() {
		// TODO Auto-generated method stub
		return 8;
	}

	private void setVolume(int stream, int volume) {
		final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		int maxVolume = audio.getStreamMaxVolume(stream);
		//Log.d(TAG, "max volume on ringer stream" + maxVolume);
		if (volume > maxVolume) {
			volume = maxVolume;
		}
		else if (volume < 0) {
			volume = 0;
		}

		int flags = AudioManager.FLAG_PLAY_SOUND;
		flags = flags | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE;
		flags = flags | AudioManager.FLAG_SHOW_UI;
		flags = flags | AudioManager.FLAG_VIBRATE;

		/*
		 * apply volume to the system
		 */
		 audio.setStreamVolume(stream, volume, flags);
	}
	
}
