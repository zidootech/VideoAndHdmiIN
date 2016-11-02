/**
 * Copyright (C) 2016 Zidoo (www.zidoo.tv)
 * Created by : jiangbo@zidoo.tv
 */
package com.example.zidoopipdemo;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.example.zidoopipdemo.view.VideoView;
import com.zidoo.test.hdmi.MyLog;

/**
 * play video
 * 
 * @author jiangbo
 * 
 *         www.zidoo.tv 2014-09-01
 */
public class PlayVideoTool {
	public VideoView	videoView	= null;
	private String		playPath	= null;

	public PlayVideoTool(VideoView videoView) {
		super();
		this.videoView = videoView;
		play();
	}

	// play video
	public void play() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getVideo("/storage");
				if (playPath != null) {
					MyLog.v("bob  playPath = " + playPath);
					playVideoView(playPath);
				} else {
					MyLog.v("bob  playPath = null");

				}
			}
		}).start();

	}

	// get usb first video path
	private void getVideo(String root) {
		MyLog.v("bob   root == " + root);
		File file = new File(root);
		if (file.isDirectory()) {
			File[] direFile = file.listFiles();
			if (direFile != null) {
				for (int i = 0; i < direFile.length; i++) {
					// if (direFile[i].isDirectory() &&
					// direFile[i].getAbsolutePath().contains("storage")) {
					if (direFile[i].isDirectory()) {
						getVideo(direFile[i].getAbsolutePath());
					} else {
						if (isMovieFile(direFile[i].getName())) {
							playPath = direFile[i].getAbsolutePath();
							return;
						}
					}
				}
			}
		}
	}

	/**
	 * play video
	 * 
	 * @author jiangbo 2015-9-8
	 * @param path
	 */
	private void playVideoView(final String path) {
		try {
			// set full dis
			videoView.stopPlayback();
			videoView.setOnErrorListener(mErrorListener);
			videoView.setOnInfoListener(mInfoListener);
			videoView.setOnPreparedListener(mPreparedListener);
			videoView.setOnCompletionListener(mCompletionListener);
			videoView.setVideoUrl(path);
			// videoView.setVideoUrl("http://vodcdn.video.taobao.com/oss/ali-video/a68805464328a61da085c84bc9c35d72/video.mp4");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnPreparedListener					mPreparedListener	= new OnPreparedListener() {
																public void onPrepared(MediaPlayer mp) {

																}
															};
	private MediaPlayer.OnInfoListener	mInfoListener		= new MediaPlayer.OnInfoListener() {
																public boolean onInfo(MediaPlayer mp, int what, final int extra) {
																	return false;
																}
															};
	private MediaPlayer.OnErrorListener	mErrorListener		= new MediaPlayer.OnErrorListener() {
																public boolean onError(MediaPlayer mp, int what, int extra) {
																	// playVideoView(playPath);
																	return false;
																}
															};

	OnCompletionListener				mCompletionListener	= new OnCompletionListener() {
																public void onCompletion(MediaPlayer arg0) {
																	playVideoView(playPath);
																}
															};

	public void onDestroy() {
		videoView.stopPlayback();
	}

	/**
	 * is video
	 * 
	 * @author jiangbo 2015-9-8
	 * @param path
	 * @return
	 */
	public static boolean isMovieFile(String path) {
		try {
			String ext = path.substring(path.lastIndexOf(".") + 1);
			if (ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("f4v")) {
				return true;
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		}

		return false;
	}

}
