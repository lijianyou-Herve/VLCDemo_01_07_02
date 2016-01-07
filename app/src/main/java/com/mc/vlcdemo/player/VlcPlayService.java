package com.mc.vlcdemo.player;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.mc.vlcdemo.R;

public class VlcPlayService extends Service {
	
	private IAudioPlayer mAudioPlayer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mAudioPlayer = new VlcPlayer(getApplicationContext());
		
		listenPhoneCall();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(IAudioPlayer.ACTION_PLAY)) {
				play(intent.getStringExtra(IAudioPlayer.MEDIA_URL));
			}else if (action.equals(IAudioPlayer.ACTION_STOP)) {
				stop();
			}else if (action.equals(IAudioPlayer.ACTION_CANCEL)) {
				stop();
				stopForeground(true);
				stopSelf();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void play(String path){
		mAudioPlayer.play(path);
		showNotification();
	}
	
	private void showNotification(){
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notification);
		Intent cancelIntent = new Intent();
		cancelIntent.setClass(this, VlcPlayService.class);
		cancelIntent.setAction(IAudioPlayer.ACTION_CANCEL);//android 5.0+ Service Intent must be explicit
		PendingIntent piCancel = PendingIntent.getService(getApplicationContext(), 1, 
				cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.notification_cancel, piCancel);
		
		Notification nf = new Notification.Builder(getApplicationContext())
				.setTicker("vlcDemo")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContent(remoteViews)
				.setPriority(Notification.PRIORITY_MAX)
				.build();
		nf.bigContentView = remoteViews;
		
		startForeground(3, nf);
	}
	
	private void stop(){
		mAudioPlayer.stop();
	}
	
	private void listenPhoneCall() {
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	private void unListenPhoneCall(){
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		unListenPhoneCall();
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener(){
		
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				stop();
				break;
			}
		};
		
	};
}
