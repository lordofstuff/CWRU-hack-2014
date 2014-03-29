package com.appsofawesome.soundcontrol;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class MotionSense implements SensorEventListener {

	private static final String TAG = "MotionSense";
	private static final float threshold = 0.5f;
	SensorManager sm;
	private Sensor mAccelerometer;
	private ArrayList<Float> processedValues;
	public boolean recording = false;

	public synchronized void pollMotion(Context context) {
		sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		processedValues = new ArrayList<Float>();
		sm.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		//run for one second maybe?
		recording = true;

	}

	public float[] pollStop() {
		sm.unregisterListener(this);
		float[] array = new float[processedValues.size()];
		int i = 0;
		for (float f: processedValues) {
			array[i++] = f;
		}
		derive(array);
		Log.d(TAG, "motion sensing data length: " + array.length);
		recording = false;
		return array;
	}

	private void derive(float[] array) {
		for (int i = 0; i < array.length -1; i++) {
			array[i] = array[i + 1] - array[i];
		}
		array[array.length-1] = array[array.length -2];
	}

	public boolean isWalking(float[] derived) {
		for (float f: derived) {
			Log.d(TAG, "derived accel: " + f);
			if (f > threshold) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		processedValues.add((float) Math.sqrt((Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2))));
	}
}
