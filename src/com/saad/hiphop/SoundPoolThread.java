package com.saad.hiphop;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class SoundPoolThread extends Thread {
	final private String TAG = "Hiphop soundPool";
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundsMap;
	public static final int SOUNDS = 10;

	private Context mContext;
	public Handler mHandler;
	
	public SoundPoolThread(Context context) {
		super();
		mContext = context;
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		soundsMap = new HashMap<Integer, Integer>();
		soundsMap.put(1, soundPool.load(context, R.raw.cb, 1));
		soundsMap.put(2, soundPool.load(context, R.raw.bd0000, 1));
		soundsMap.put(3, soundPool.load(context, R.raw.cl, 1));
		soundsMap.put(4, soundPool.load(context, R.raw.ds, 1));
		soundsMap.put(5, soundPool.load(context, R.raw.ds1, 1));
		soundsMap.put(6, soundPool.load(context, R.raw.ds2, 1));
		soundsMap.put(7, soundPool.load(context, R.raw.ds3, 1));
		soundsMap.put(8, soundPool.load(context, R.raw.h, 1));
		soundsMap.put(9, soundPool.load(context, R.raw.ma, 1));
		soundsMap.put(10, soundPool.load(context, R.raw.rs, 1));
	}

	public void run() {
		Looper.prepare();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				int soundNumber = msg.arg1;
				mHandler.post(new playSoundRunnable(soundNumber));
			}
		};

		Looper.loop();
	}
	
	class playSoundRunnable implements Runnable {
		int mSoundNumber;
		playSoundRunnable(int soundNumber) {
			mSoundNumber = soundNumber;
		}
		
		public void run() {
			playSound(mSoundNumber);
		}
	}

	public void playSound(int sound) {
		AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;

		soundPool.play(soundsMap.get(sound), volume, volume, 1, 0, 0);
	}
}
