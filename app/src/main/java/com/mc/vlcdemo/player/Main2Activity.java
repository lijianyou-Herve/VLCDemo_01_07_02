package com.mc.vlcdemo.player;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.mc.vlcdemo.R;

import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.IVideoPlayer;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcException;
import org.videolan.libvlc.Media;

public class Main2Activity extends Activity implements SurfaceHolder.Callback, IVideoPlayer {

    private final static String TAG = "[VlcVideoActivity]";

    private SurfaceView mSurfaceView;
    private LibVLC mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;
    private View mLoadingView;
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_vlc);
        Log.v("fwj", "onCreate============================================");
        mSurfaceView = (SurfaceView) findViewById(R.id.video);
        mLoadingView = findViewById(R.id.video_loading);
        try {
            mMediaPlayer = LibVLC.getInstance();
//            mMediaPlayer.getBufferContent();
        } catch (LibVlcException e) {
            e.printStackTrace();
            String Buffer;
        }

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        mSurfaceHolder.addCallback(this);

        mMediaPlayer.eventVideoPlayerActivityCreated(true);

        EventHandler em = EventHandler.getInstance();
        em.addHandler(mVlcHandler);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        String url = "http://bj.bcebos.com/v1/tomato-dev/38c420a4-e1f4-4beb-b497-7933c03be206/film/25b1b34a-28b3-411d-8276-402f107a7ce0.mp4";
        mSurfaceView.setKeepScreenOn(true);
        mMediaPlayer.setMediaList();
        mMediaPlayer.getMediaList().add(new Media(mMediaPlayer, url), false);
        //		mMediaPlayer.playIndex(0);
        //http://gis-wein1.3322.org:9191/videostream.cgi?user=admin&password=admin
//		mMediaPlayer.playMRL("http://live.3gv.ifeng.com/zixun.m3u8");
//        mMediaPlayer.playMRL(getIntent().getStringExtra(IAudioPlayer.MEDIA_URL));
        mMediaPlayer.playMRL(url);
//        mMediaPlayer.playMRL("rtsp://gis-wef.3322.org:554/user=admin&password=hiav2014&channel=8&stream=1.sdp?");
//		mMediaPlayer.playMRL("http://gis-wein2.3322.org:8181/videostream.cgi?user=admin&password=admin");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("fwj", "onPause============================================");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mSurfaceView.setKeepScreenOn(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("fwj", "onDestroy============================================");
        if (mMediaPlayer != null) {
            mMediaPlayer.eventVideoPlayerActivityCreated(false);

            EventHandler em = EventHandler.getInstance();
            em.removeHandler(mVlcHandler);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("fwj", "surfaceCreated============================================");
        if (mMediaPlayer != null) {
            mSurfaceHolder = holder;
            mMediaPlayer.attachSurface(holder.getSurface(), this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        Log.v("fwj", "surfaceChanged============================================");
        mSurfaceHolder = holder;
        if (mMediaPlayer != null) {
            mMediaPlayer.attachSurface(holder.getSurface(), this);//, width, height
        }
        if (width > 0) {
            mVideoHeight = height;
            mVideoWidth = width;
            System.out.println("mVideoWidth:" + mVideoWidth + "=====&&&&&&&&&&&&&&&&&&&&=====mVideoHeight:" + mVideoHeight);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.v("fwj", "surfaceDestroyed============================================");
        if (mMediaPlayer != null) {
            mMediaPlayer.detachSurface();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.videolan.libvlc.IVideoPlayer#setSurfaceSize(int, int, int, int, int, int)
     */
    @Override
    public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {

        Log.v("fwj", "onConfigurationChanged===" + width + "," + height + "," + visible_width + "," + visible_height + "," + sar_num + "," + sar_den);
        Log.v("fwj", "setSurfaceSize============================================");
        mVideoHeight = height;
        mVideoWidth = width;
        mVideoVisibleHeight = visible_height;
        mVideoVisibleWidth = visible_width;
        mSarNum = sar_num;
        mSarDen = sar_den;
        mHandler.removeMessages(HANDLER_SURFACE_SIZE);
        mHandler.sendEmptyMessage(HANDLER_SURFACE_SIZE);
    }

    private static final int HANDLER_BUFFER_START = 1;
    private static final int HANDLER_BUFFER_END = 2;
    private static final int HANDLER_SURFACE_SIZE = 3;
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    private Handler mVlcHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.getData() == null)
                return;

            switch (msg.getData().getInt("event")) {
                case EventHandler.MediaPlayerTimeChanged:
                    break;
                case EventHandler.MediaPlayerPositionChanged:
                    break;
                case EventHandler.MediaPlayerPlaying:
                    mHandler.removeMessages(HANDLER_BUFFER_END);
                    mHandler.sendEmptyMessage(HANDLER_BUFFER_END);
                    break;
                case EventHandler.MediaPlayerEndReached:
                    //播放完成
                    break;
            }

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_BUFFER_START:
                    showLoading();
                    break;
                case HANDLER_BUFFER_END:
//                hideLoading();
                    break;
                case HANDLER_SURFACE_SIZE:
                    hideLoading();
                    changeSurfaceSize();
                    break;
            }
        }
    };

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        Log.v("fwj", "showLoading============================================");
    }

    private void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
        Log.v("fwj", "hideLoading============================================");
    }

    private void changeSurfaceSize() {

        Log.v("fwj", "changeSurfaceSize============================================");
//		// get screen size
        int dw = getWindowManager().getDefaultDisplay().getWidth();
        int dh = getWindowManager().getDefaultDisplay().getHeight();
        mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
        mSurfaceView.invalidate();
    }
}
