package com.mc.vlcdemo.player;

import android.content.Context;
import android.content.Intent;

public class AudioController {
	private static AudioController mAudioController;
	
	private AudioController(){};
	
	private static Context mContext;
	
	public static AudioController getInstance(Context context){
		if (mAudioController == null) {
			mAudioController = new AudioController();
			mContext = context;
		}
		return mAudioController; 
	}
	
	public void play(String url){
		Intent intent = new Intent();
		intent.setClass(mContext, VlcPlayService.class);
		intent.setAction(IAudioPlayer.ACTION_PLAY);//android 5.0+ Service Intent must be explicit
		intent.putExtra(IAudioPlayer.MEDIA_URL, url);
		mContext.startService(intent);
	}
	
	public void stop(){
		Intent intent = new Intent();
		intent.setClass(mContext, VlcPlayService.class);
		intent.setAction(IAudioPlayer.ACTION_STOP);//android 5.0+ Service Intent must be explicit
		mContext.startService(intent);
	}
}
