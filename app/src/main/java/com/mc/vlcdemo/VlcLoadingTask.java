package com.mc.vlcdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class VlcLoadingTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	
	private ProgressDialog dialog;
	
	public VlcLoadingTask(Context context){
		this.context = context;
	}
	
	@Override
	protected void onPreExecute() {
		if (context != null) {
			dialog = ProgressDialog.show(context, "提示", "加载中...", true);
			dialog.setCanceledOnTouchOutside(true);
		}
		super.onPreExecute();
	}
	
	public abstract void run();
	
	@Override
	protected Void doInBackground(Void... params) {
		run();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (context != null && dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		context = null;
		super.onPostExecute(result);
	}

	
}
