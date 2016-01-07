package com.mc.vlcdemo.player;

import org.videolan.libvlc.LibVLC;

import android.content.Context;
import android.media.AudioManager;

public class VlcPlayer implements IAudioPlayer{
	
	public static final int STATUS_DEFAULT = -1;
	public static final int STATUS_NOTHING_SPECIAL = 0;
	public static final int STATUS_OPENING = 1;
	public static final int STATUS_BUFFERING = 2;
	public static final int STATUS_PLAYING = 3;
	public static final int STATUS_PAUSED = 4;
	public static final int STATUS_STOPPED = 5;
	public static final int STATUS_ENDED = 6;
	public static final int STATUS_ERROR = 7;
	
	private Context mContext;
	
	private LibVLC mLibVLC;
	private LibVLC mDestroyingLibVLC;
	
	public VlcPlayer(Context context){
		mContext = context;
	}

	@Override
	public void play(String path) {
		stop();
		try {
			Thread.sleep(2000);
			mLibVLC = LibVLC.getInstance();
			mLibVLC.init(mContext);
			mLibVLC.playMRL(path);
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		if (mLibVLC != null) {
			mDestroyingLibVLC = mLibVLC;
			mLibVLC = null;
			new Thread(new Runnable() {
				@Override
				public void run() {
					mDestroyingLibVLC.destroy();
				}
			}).start();
		}
	}

	@Override
	public boolean isPlaying() {
		if (mLibVLC != null && mLibVLC.getPlayerState() == STATUS_PLAYING) {
			return ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE)).isMusicActive();
		}
		return false;
	}
	
}
