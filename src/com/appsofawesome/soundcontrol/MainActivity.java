package com.appsofawesome.soundcontrol;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.Writer;

import android.app.Activity;
//import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.Context;
//import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
//import android.os.Build;

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
		//String preferencesName = getPreferenceManager().getSharedPreferencesName();
		Utils.context = this;

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
		Utils.updateVolume(results[1], results[0], this);
	}

	public void testMotion(View view) {
		if (sense == null || !sense.recording) {
			sense = new MotionSense();
			sense.pollMotion(this);
			((Button) view).setText("recording");
		}
		else {
			boolean[] walkingLaying = sense.isWalking(sense.pollStop());
			Log.d(TAG, "walking: " + walkingLaying[0]);
			TextView text = ((TextView) findViewById(R.id.textView1));
			((Button) view).setText("test motion sensor");
			text.setText("Walking: " + walkingLaying[0] + " Laying: " + walkingLaying[1]);
		}
	}
	
	public void settingsClick(View view) {
		//intent to go to new activity. 
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}





}
