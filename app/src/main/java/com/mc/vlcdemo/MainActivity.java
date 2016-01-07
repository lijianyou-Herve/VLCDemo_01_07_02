package com.mc.vlcdemo;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mc.vlcdemo.lrc.LrcInfo;
import com.mc.vlcdemo.lrc.LrcProcess;
import com.mc.vlcdemo.player.AudioController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private final String url =
//			"http://live.51iradio.com/iremember/64k.m3u8";
//			"http://yuanyu-web.chinacloudapp.cn:9099/streamer/live/iplay/iplay_64k.m3u8";
//			"http://yuanyu-web.chinacloudapp.cn:8088/live/iplay/iplay_64k.m3u8";
			"http://live.radiohenan.com:1935/live/jiaotong/playlist.m3u8";

	private EditText editText;

	private Button button;

	private LrcView mlrcView;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        button = (Button) findViewById(R.id.click);

        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				play(url);
			}
		});

        findViewById(R.id.stop).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop();
			}
		});

        editText = (EditText) findViewById(R.id.edit_text);
        editText.setText(url);

        LrcInfo lrcInfo = new LrcInfo();
        lrcInfo.setTitle("一步之遥");
        lrcInfo.setArtist("雅尼");
        lrcInfo.setAlbum("电影");
        List<String> lines = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
        	lines.add("第" + i + "行");
		}
        lrcInfo.setLines(lines);

        LrcProcess lrcProcess = LrcProcess.getInstance();
        lrcProcess.setLrc(lrcInfo, System.currentTimeMillis(), 20000);

        mlrcView = (LrcView) findViewById(R.id.lrcview);
        mlrcView.setLrcCallbak();

    }

	private void play(final String url){
		new VlcLoadingTask(this) {

			@Override
			public void run() {
				AudioController.getInstance(MainActivity.this).play(url);
			}
		}.execute();
	}

	private void stop(){
		AudioController.getInstance(this).stop();
	}

}
