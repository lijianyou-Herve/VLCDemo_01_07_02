package com.mc.vlcdemo.player;



public interface IAudioPlayer {
	
	String ACTION_PLAY = "com.mc.vlcdemo.action_play";
	String ACTION_STOP = "com.mc.vlcdemo.action_stop";
	String ACTION_CANCEL = "com.mc.vlcdemo.action_cancel";
	
	String MEDIA_URL = "media_url";
	
	void play(String path);
	
	void stop();
	
	boolean isPlaying();
	
}
