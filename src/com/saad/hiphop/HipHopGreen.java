package com.saad.hiphop;

import android.util.Log;

public class HipHopGreen {
	private final String TAG = "HipHopGreen";

	private final int RED_CHECK_COUNT = 7;
	private final float DELTA_TOLERANCE = 0.1f;
	private final int DELTA_ENTRY_COUNT = RED_CHECK_COUNT - 1;

	private int tapCount = 0;
	private long timeStampOld = 0;
	private long timeStampNew = 0;

	private long delta[] = new long[DELTA_ENTRY_COUNT];

	public enum State {
		NONE, GREEN, RED
	};

	private long timeInterval = 0;

	State state = State.NONE;

	public long getTimeInterval() {
		return timeInterval;
	}

	public void resetTapCount() {
		tapCount = 0;
	}

	private boolean checkRed() {
		long avg = 0;
		boolean red = true;
		for (int i = 0; i < DELTA_ENTRY_COUNT; i++) {
			avg += delta[i]/DELTA_ENTRY_COUNT;
		}
		//Log.d(TAG, "Avg = " + avg);

//		for (int i = 0; i < DELTA_ENTRY_COUNT; i++) {
//			long errorAbs = Math.abs(delta[i] - avg);
//			//Log.d(TAG, "Absolute error = " + errorAbs);
//			float errorRel = (float) errorAbs / avg;
//			//Log.d(TAG, "Relative error = " + errorRel);
//			if (errorRel > DELTA_TOLERANCE) {
//				//Log.d(TAG, "Greater than delta tolerance");
//				red = false;
//				break;
//			}
//		}
		timeInterval = (long)avg;
		return red;
	}

	public boolean recordTapEvent(long timeStampMillis) {
		Log.d(TAG, "Time stamp = " + timeStampMillis);
		if (tapCount == 0) {
			timeStampOld = timeStampMillis;
		} else {
			timeStampNew = timeStampMillis;
			delta[tapCount - 1] = timeStampNew - timeStampOld;
			timeStampOld = timeStampNew;
			Log.d(TAG, "Delta = "+delta[tapCount - 1]);
		}

		tapCount++;
		Log.d(TAG, "Tap count = " + tapCount);

		if (tapCount == RED_CHECK_COUNT) {
			if (checkRed()) {
				tapCount = 0;
				return true;
			} else {
				tapCount = DELTA_ENTRY_COUNT;
				for (int i = 0; i < DELTA_ENTRY_COUNT - 1; i++) {
					delta[i] = delta[i + 1];
				}
			}
		}

		return false;
	}

}
