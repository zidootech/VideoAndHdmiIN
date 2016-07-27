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
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.*;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

/**
 * play video
 * 
 * @author jiangbo
 * 
 *         
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
				getVideo("/mnt");
				if (playPath != null) {
					System.out.println("bob  playPath = " + playPath);
					playVideoView(playPath);
				} else {
					System.out.println("bob  playPath = null");

				}
			}
		}).start();

	}

	// get usb first video path
	private void getVideo(String root) {
		System.out.println("bob   root == " + root);
		File file = new File(root);
		if (file.isDirectory()) {
			File[] direFile = file.listFiles();
			if (direFile != null) {
				for (int i = 0; i < direFile.length; i++) {
					if (direFile[i].isDirectory() && direFile[i].getAbsolutePath().contains("usb")) {
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
			//set full dis
			setFull();
			
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

	/**
	 * 
	 * set video full
	 * 
	 * @author jiangbo 2015-9-10
	 */
	private void setFull() {
		TvPictureManager.getInstance().setVideoArc(EnumVideoArcType.values()[0]);
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

	/**
	 * Switch sound
	 * 
	 * @param isHdmi
	 * 
	 */
	public void setAudio(boolean isHdmi) {
		try {
			System.out.println("bob  isHdmi = " + isHdmi);
			TvManager.getInstance().getAudioManager().setInputSource(isHdmi ? EnumInputSource.E_INPUT_SOURCE_HDMI : EnumInputSource.E_INPUT_SOURCE_STORAGE);
		} catch (TvCommonException e) {
			e.printStackTrace();
		}
	}
}
