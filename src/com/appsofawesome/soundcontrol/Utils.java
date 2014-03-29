package com.appsofawesome.soundcontrol;

import de.robv.android.xposed.XposedBridge;
import android.content.Context;
import android.media.AudioManager;
import android.os.PowerManager;

public class Utils {
	
	static void updateVolume(double lowToHigh, double volume, Context context) {
		//get transformed volume (translate a noise level to a volume on the device
		int deviceVolume = transformVolume(volume, AudioManager.STREAM_RING, context);
		setVolume(AudioManager.STREAM_RING, deviceVolume, context);

	}
	
	private static int transformVolume(double volume, int stream, Context context) {
		//Log.d(TAG, "volume: " + volume);
		final AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audio.getStreamMaxVolume(stream);
		double normVolume = ((volume - getMinVol()) / (getMaxVol() - getMinVol()));
		return  round((normVolume * maxVolume));
	}
	
	private static int round(double d) {
		if ((d * 100) % 100 < 50) {
			return (int) d;
		}
		return (int) d + 1;
	}

	private static int getMaxVol() {
		return 12;
	}

	private static int getMinVol() {
		return 8;
	}

	private static void setVolume(int stream, int volume, Context context) {
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
	
//	private void saveToFile(short[] array) {
//	try {
//		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("TestData.txt", Context.MODE_PRIVATE));
//		//	        outputStreamWriter.write("poop");
//		Log.d(TAG, "array length: " + array.length);
//		for (short s: array) {
//			outputStreamWriter.write(Short.toString(s));
//			outputStreamWriter.write("\n");
//		}
//		outputStreamWriter.close();
//	}
//	catch (IOException e) {
//		Log.e("Exception", "File write failed: " + e.toString());
//	} 
//}
	
	public static void updateSoundSettings(Context context, float amplitude, float highToLow, boolean walking, boolean laying) {
		if (laying) {
			//act according to user settings
			XposedBridge.log("laying: " + laying);
		}
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		if (isScreenOn) {
			//do stuff
			XposedBridge.log("screen on: " + isScreenOn);
		}
		if (walking) {
			XposedBridge.log("walking: " + walking);
		}
		if (highToLow > 1) {
			
		}
		updateVolume(highToLow, amplitude, context);
	}

	public static float getLevelTolerance() {
		return 1;
	}
	
	//lots of master switches, basically
	boolean isXposedPartOn() {
		return true;
	}
	
	static boolean isLayingFeatureOn() {
		return true;
	}
	
	static boolean isWalkingFeatureOn() {
		return true;
	}
	
	static boolean isSoundSampleOn() {
		return true;
	}
	
	static boolean isRingtoneChangeOn() {
		return true;
	}
	
	

}
