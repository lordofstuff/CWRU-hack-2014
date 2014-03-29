package com.appsofawesome.soundcontrol;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * application level API for recording raw audio. 
 * @author Stephen
 *
 */
public class recorder {
	//adapted from here: http://stackoverflow.com/questions/8499042/android-audiorecord-example

	public static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord recorder = null;
	//private Thread recordingThread = null;
	private boolean isRecording = false;
	private Object lock = new Object();

	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format


	public synchronized short[] record(double seconds) {
		final short[] array = new short[RECORDER_SAMPLERATE * getRecordTime()];
		AudioRecord recorder = startRecording();
		//wait one seconds ish
		try {
			wait(getRecordTime());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		recorder.read(array, 0, array.length);
		stopRecording();
		
		return array;
	}

	private int getRecordTime() {
		return 1000; //1 second
	}

	private AudioRecord startRecording() {
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

		recorder.startRecording();
		isRecording = true;
		//	    recordingThread = new Thread(new Runnable() {
		//	        public void run() {
		//	            writeAudioDataToFile();
		//	        }
		//	    }, "AudioRecorder Thread");
		//	    recordingThread.start();
		return recorder;
	}

	private void stopRecording() {
		// stops the recording activity
		if (null != recorder) {
			isRecording = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			//recordingThread = null;
		}
	}

}
