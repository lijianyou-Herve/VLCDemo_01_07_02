package com.mc.vlcdemo.lrc;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;


public class LrcProcess {
	
	private static LrcProcess mLrcProcess;
	
	private LrcProcess(){};
	
	private LrcInfo mLrcInfo;
	
	private long mStartTime;
	
	private long mDuration;
	
	private List<LrcCallback> mLrcCallbacks = new ArrayList<LrcProcess.LrcCallback>();
	
	public static LrcProcess getInstance(){
		if (mLrcProcess == null) {
			mLrcProcess = new LrcProcess();
		}
		return mLrcProcess;
	}
	
	public void setLrc(LrcInfo lrcInfo,long start,long duration){
		mLrcInfo = lrcInfo;
		mStartTime = start;
		mDuration = duration;
		
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}
	
	private void endLrc(){
		if (mLrcInfo != null) {
			for (LrcCallback lrcCallback : mLrcCallbacks) {
				lrcCallback.onLrcChanged(null, -1);
			}
			mHandler.removeMessages(0);
			mLrcInfo = null;
		}
	}
	
	private int getLineCount(){
		return mLrcInfo.getLines().size();
	}
	
	private long getGoingDuration(){
		return System.currentTimeMillis() - mStartTime;
	}
	
	private int getCurrentLine(){
		long lineDuration = mDuration / getLineCount();
		int currentLine = (int) (getGoingDuration() / lineDuration);
		currentLine = currentLine < getLineCount() ? currentLine : (getLineCount() - 1);
		return currentLine;
	}
	
	private Handler mHandler = new Handler(new Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			if (mLrcInfo != null) {
				
				if (getGoingDuration() <= mDuration) {
					for (LrcCallback lrcCallback : mLrcCallbacks) {
						lrcCallback.onLrcChanged(mLrcInfo.getLines(), getCurrentLine());
					}
					mHandler.sendEmptyMessageDelayed(0, 1000);
				}else {
					endLrc();
				}
			}
			return false;
		}
	});
	
	public interface LrcCallback{
		
		void onLrcChanged(List<String> lines,int currentLine);
		
	}
	
	public void addCallBack(LrcCallback lrcCallback){
		mLrcCallbacks.add(lrcCallback);
	}
	
	public void removeCallback(LrcCallback lrcCallback){
		mLrcCallbacks.remove(lrcCallback);
	}
	
}
