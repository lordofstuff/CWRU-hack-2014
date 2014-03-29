package com.appsofawesome.soundcontrol;

import android.content.Context;
import android.media.AudioManager;

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
		// TODO Auto-generated method stub
		return 12;
	}

	private static int getMinVol() {
		// TODO Auto-generated method stub
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

}
