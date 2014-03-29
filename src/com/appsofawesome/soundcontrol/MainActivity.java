package com.appsofawesome.soundcontrol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private static final String TAG = "TEST";
	MotionSense sense;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	public void Test(View view) {
		recorder record = new recorder();
		Log.d(TAG, "starting record");
		short[] array = record.record(1);
		TextView text = ((TextView) findViewById(R.id.textView1));	

		//save data: (if needed)
		//saveToFile(array);
		double[] results = Processing.amplitude_ratio(array, (short) recorder.RECORDER_SAMPLERATE, (short) 30000);
		text.setText(Double.toString(results[0]));
		updateVolume(results[1], results[0]);
		Log.d(TAG, "should be done by now");
	}

	public void testMotion(View view) {
		if (sense == null || !sense.recording) {
			sense = new MotionSense();
			sense.pollMotion(this);
			((Button) view).setText("recording");
		}
		else {
			Boolean walking = sense.isWalking(sense.pollStop());
			Log.d(TAG, "walking: " + walking);
			TextView text = ((TextView) findViewById(R.id.textView1));
			((Button) view).setText("test motion sensor");
			text.setText("Walking: " + walking);
		}
	}

//	private void saveToFile(short[] array) {
//		try {
//			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("TestData.txt", Context.MODE_PRIVATE));
//			//	        outputStreamWriter.write("poop");
//			Log.d(TAG, "array length: " + array.length);
//			for (short s: array) {
//				outputStreamWriter.write(Short.toString(s));
//				outputStreamWriter.write("\n");
//			}
//			outputStreamWriter.close();
//		}
//		catch (IOException e) {
//			Log.e("Exception", "File write failed: " + e.toString());
//		} 
//	}

	/**
	 * called from the analysis method after analysis is complete. 
	 * @param lowToHigh the ratio of low frequencies to high ones. 
	 * @param volume the average volume from 0 to 1
	 */
	public void updateVolume(double lowToHigh, double volume) {
		//get transformed volume (translate a noise level to a volume on the device
		int deviceVolume = transformVolume(volume, AudioManager.STREAM_RING);
		setVolume(AudioManager.STREAM_RING, deviceVolume);

	}

	private int transformVolume(double volume, int stream) {
		
		Log.d(TAG, "volume: " + volume);
		final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
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
		final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		int maxVolume = audio.getStreamMaxVolume(stream);
		Log.d(TAG, "max volume on ringer stream" + maxVolume);
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
