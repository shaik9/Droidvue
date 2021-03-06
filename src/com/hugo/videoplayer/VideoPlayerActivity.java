package com.hugo.videoplayer;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.hugo.droidapplication.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.FrameLayout;

public class VideoPlayerActivity extends Activity implements
		SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
		VideoControllerView.MediaPlayerControl {

	SurfaceView videoSurface;
	MediaPlayer player;
	VideoControllerView controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_video_player);
		videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
		SurfaceHolder videoHolder = videoSurface.getHolder();
		videoHolder.addCallback(this);
      
		player = new MediaPlayer();
		controller = new VideoControllerView(this);
		try {
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setVolume(1.0f, 1.0f);
			player.setDataSource(this,
					Uri.parse(getIntent().getStringExtra("url")));
			// player.setDataSource(this,
			// Uri.parse("http://183.82.98.11:1935/vod/mp4:Movie1.mp4/playlist.m3u8"));
			player.setOnPreparedListener(this);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		controller.show();
		return false;
	}

	// Implement SurfaceHolder.Callback
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		player.setDisplay(holder);
		player.prepareAsync();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	// End SurfaceHolder.Callback

	// Implement MediaPlayer.OnPreparedListener
	@Override
	public void onPrepared(MediaPlayer mp) {
		controller.setMediaPlayer(this);
		controller
				.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
		player.start();
	}

	// End MediaPlayer.OnPreparedListener

	// Implement VideoMediaController.MediaPlayerControl
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return player.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return player.isPlaying();
	}

	@Override
	public void pause() {
		player.pause();
	}

	@Override
	public void seekTo(int i) {
		player.seekTo(i);
	}

	@Override
	public void start() {
		player.start();
	}

	@Override
	public boolean isFullScreen() {
		return false;
	}

	/*@Override
	public void toggleFullScreen() {

	}*/

	// End VideoMediaController.MediaPlayerControl
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			int remaintime = player.getDuration()-player.getCurrentPosition();
			player.stop();
			player.release();	
		} else if (keyCode == 85) {
			controller.show();
			if (player.isPlaying()) {
				player.pause();
			} else {
				player.start();		
			}
		} else if (keyCode == 23) {
			controller.show();
			player.pause();
		} else if (keyCode == 19) {
			controller.show();
			player.seekTo(0);
			player.start();
		} else if (keyCode == 89) {
			controller.show();
			if (player.getCurrentPosition() - 120000 > 0
					&& (player.isPlaying())) {
				player.seekTo(player.getCurrentPosition() - 120000);
				player.start();
			}
		} else if (keyCode == 90) {
			controller.show();
			if (player.getCurrentPosition() + 120000 < player.getDuration()
					&& (player.isPlaying())) {
				player.seekTo(player.getCurrentPosition() + 120000);
				player.start();
			}
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP
				|| keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
			AudioManager audio = (AudioManager) getSystemService(VideoPlayerActivity.this.AUDIO_SERVICE);
			switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
				return true;
			default:
				return super.dispatchKeyEvent(event);
			}
		} else
			super.onKeyDown(keyCode, event);
		return true;
	}
}
