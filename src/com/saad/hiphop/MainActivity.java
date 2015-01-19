package com.saad.hiphop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements SensorEventListener {

	private final String TAG = "HipHop MainActivity";
	private HipHopGreen hipHopGreen = new HipHopGreen();
	private SoundPoolThread sPThread;
	private int currSound, prevSound = 0;
	private Random rand;

	private SensorManager sensorManager;
	private int mSensorX;
	private float prevSensorX;
	private int minCount;
	private int maxCount;
	private Display mDisplay;
	private SensorManager sm;
	private WindowManager mWindowManager;

	private boolean mReset;

	private List<Thread> mThreads;

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	float gravity[] = { 0, 0, 0 };
	float linear_acceleration[] = { 0, 0, 0 };
	int counter = 0;
	float old_val = 0;
	int last = 0;
	int last_last = 0;
	boolean lastNeg = false;
	boolean lastPos = false;
	int ROTATION = -1;

	@Override
	public void onSensorChanged(SensorEvent event) {

		final float alpha = (float) 0.8;

		gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
		gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
		gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

		linear_acceleration[0] = event.values[0] - gravity[0];
		linear_acceleration[1] = event.values[1] - gravity[1];
		linear_acceleration[2] = event.values[2] - gravity[2];
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;

		/*
		 * if(ROTATION != mDisplay.getRotation()){ gotTap(); }
		 */
		switch (mDisplay.getRotation()) {
		case Surface.ROTATION_0:
			mSensorX = (int) linear_acceleration[1];
			manageSignals(mSensorX, 1);

			break;
		case Surface.ROTATION_90:
			mSensorX = (-(int) linear_acceleration[0]);
			manageSignals(mSensorX, 2);
			break;
		case Surface.ROTATION_180:
			mSensorX = (int) -event.values[1];
			Log.e("main", "Orientation 2 :" + String.valueOf(mSensorX));
			break;
		case Surface.ROTATION_270:
			mSensorX = (int) event.values[0];
			Log.e("main", "Orientation 3 :" + String.valueOf(mSensorX));
			break;
		}
	}

	private void manageSignals(int mSensorX, int num) {
		if (mSensorX > 0 && lastNeg) {
			Log.e("main", num + " going_high:" + counter);
			gotTap();
			counter = 0;
		} else if (mSensorX < 0 && lastPos) {
			Log.e("main", num + " going_low:" + counter);
			gotTap();
			counter = 0;
		}

		if (mSensorX > 0) {
			counter++;
			lastPos = true;
			lastNeg = false;
		} else if (mSensorX < 0) {
			lastNeg = true;
			lastPos = false;
			counter++;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mDisplay = mWindowManager.getDefaultDisplay();
		setContentView(R.layout.activity_main);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		sPThread = new SoundPoolThread(this);
		sPThread.start();
		rand = new Random();
		getRandomSound();
		mThreads = new ArrayList<Thread>();
		mReset = false;
	}

	protected void onDestroy() {
		super.onDestroy();
		mReset = true;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class LoopSound extends Thread {
		long mTime;
		int mSound;

		LoopSound(int sound, long time) {
			super();
			mTime = time;
			mSound = sound;
		}

		public void run() {
			while (true && !mReset) {
				Message msg = sPThread.mHandler.obtainMessage();
				msg.arg1 = mSound;
				sPThread.mHandler.sendMessage(msg);
				try {
					Thread.sleep(mTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void gotTap2(View view) {
		gotTap();
	}

	public void gotTap() {
		// mReset = false;
		long timeStamp = System.currentTimeMillis();
		if (hipHopGreen.recordTapEvent(timeStamp)) {
			// Button btnGotTap = (Button) findViewById(R.id.btnTapMe);
			// btnGotTap.setEnabled(false);
			Thread l_loopsound = new LoopSound(currSound,
					hipHopGreen.getTimeInterval());
			l_loopsound.start();
			mThreads.add(l_loopsound);
			// getRandomSound();
		} else {
			Log.d(TAG, "post sound");
			Message msg = sPThread.mHandler.obtainMessage();
			msg.arg1 = currSound;
			sPThread.mHandler.sendMessage(msg);
		}

	}

	public void reset(View view) {
		mReset = true;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mReset = false;
	}

	public void blaah(View view) {
		Log.d(TAG, "blaah");
		Button btnGotTap = (Button) findViewById(R.id.btnTapMe);
		getRandomSound();

		hipHopGreen.resetTapCount();
		btnGotTap.setEnabled(true);
	}

	void getRandomSound() {
		int randomSound;

		do {
			randomSound = rand.nextInt(SoundPoolThread.SOUNDS) + 1;
		} while (randomSound == prevSound);

		prevSound = currSound;
		currSound = randomSound;
	}

}
