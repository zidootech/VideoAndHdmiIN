/**
 * Copyright (C) 2016 Zidoo (www.zidoo.tv)
 * Created by : jiangbo@zidoo.tv
 */
package com.zidoo.test.hdmi;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.realtek.hardware.RtkHDMIRxManager;
import com.realtek.server.HDMIRxParameters;
import com.realtek.server.HDMIRxStatus;

public class RealtekeHdmi extends HdmiInterface {

	public FloatingWindowTextureListener	mListener					= null;
	public View								mPreview					= null;
	public TextureView						mTextureView				= null;
	public SurfaceTexture					mSurfaceTextureForNoPreview	= null;
	private RtkHDMIRxManager				mHDMIRX						= null;
	private Handler							mHandler					= null;
	private boolean							mPreviewOn					= false;
	private final static int				DISPLAY						= 0;
	private final static int				DISPLAYTIME					= 200;
	private BroadcastReceiver				mHdmiRxHotPlugReceiver		= null;
	private int								mFps						= 0;
	private int								mWidth						= 0;
	private int								mHeight						= 0;

	public RealtekeHdmi(Context mContext) {
		super(mContext);
	}

	private void init() {
		initView();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DISPLAY: {
					if (isConnect && isEnable) {
						play();
					}
				}
					break;
				default:
					break;
				}
			}
		};
		initHdmiConnect();
	}

	private void initHdmiConnect() {
		mHdmiRxHotPlugReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean hdmiRxPlugged = intent.getBooleanExtra(HDMIRxStatus.EXTRA_HDMIRX_PLUGGED_STATE, false);
				isConnect = hdmiRxPlugged;
				if (mHdmiListener != null) {
					mHdmiListener.connect(hdmiRxPlugged);
				}
				if (isEnable) {
					if (isConnect) {
						play();
					} else {
						stop();
					}
				}
			}
		};

		isConnect = isConnect(mContext);
		IntentFilter hdmiRxFilter = new IntentFilter(HDMIRxStatus.ACTION_HDMIRX_PLUGGED);
		mContext.registerReceiver(mHdmiRxHotPlugReceiver, hdmiRxFilter);
	}

	public static boolean isConnect(Context context) {
		IntentFilter intentFilter = new IntentFilter(HDMIRxStatus.ACTION_HDMIRX_PLUGGED);
		Intent batteryStatus = context.registerReceiver(null, intentFilter);
		boolean hdmiRxPlugged = batteryStatus.getBooleanExtra(HDMIRxStatus.EXTRA_HDMIRX_PLUGGED_STATE, false);
		return hdmiRxPlugged;
	}

	public boolean stop() {
		if (mPreview != null) {
			mPreview.setVisibility(View.INVISIBLE);
		}
		boolean rlt = true;
		if (mHDMIRX != null) {
			mHDMIRX.stop();
			mHDMIRX.release();
			mHDMIRX = null;
		} else {
			rlt = false;
		}
		isDisPlay = false;
		mFps = 0;
		mWidth = 0;
		mHeight = 0;
		return rlt;
	}

	public boolean play() {
		if (mPreview == null) {
			return false;
		}
		mPreview.setVisibility(View.VISIBLE);
		mHandler.removeMessages(DISPLAY);
		MyLog.v("play------------- mIsPlaying = " + isDisPlay + " mPreviewOn = " + mPreviewOn);
		if (!isDisPlay && mPreviewOn) {
			mHDMIRX = new RtkHDMIRxManager();
			HDMIRxStatus rxStatus = mHDMIRX.getHDMIRxStatus();
			if (rxStatus != null && rxStatus.status == HDMIRxStatus.STATUS_READY) {
				if (mHDMIRX.open() != 0) {
					mWidth = 0;
					mHeight = 0;
					mHDMIRX = null;
					mHandler.sendEmptyMessageDelayed(DISPLAY, DISPLAYTIME);
					return false;
				}
				HDMIRxParameters hdmirxGetParam = mHDMIRX.getParameters();
				getSupportedPreviewSize(hdmirxGetParam, rxStatus.width, rxStatus.height);
				mFps = getSupportedPreviewFrameRate(hdmirxGetParam);
				// mScanMode = rxStatus.scanMode;

			} else {
				mHandler.sendEmptyMessageDelayed(DISPLAY, DISPLAYTIME);
				return false;
			}
			try {
				SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
				mHDMIRX.setPreviewDisplay3(surfaceTexture);
				// configureTargetFormat
				HDMIRxParameters hdmirxParam = new HDMIRxParameters();
				MyLog.v("hdmi setPreviewSize  mWidth = " + mWidth + "  mHeight = " + mHeight + "  mFps = " + mFps);
				hdmirxParam.setPreviewSize(mWidth, mHeight);
				hdmirxParam.setPreviewFrameRate(mFps);
				// set sorce format
				mHDMIRX.setParameters(hdmirxParam);
				// configureTargetFormat end
				mHDMIRX.play();
				isDisPlay = true;
				MyLog.v("hdmi mIsPlaying  successfull");
			} catch (Exception e) {
				stop();
				e.printStackTrace();
				MyLog.e("play erro = " + e.getMessage());
			}
		} else if (!mPreviewOn) {
			mHandler.sendEmptyMessageDelayed(DISPLAY, DISPLAYTIME);
			return false;
		} else {
			return false;
		}
		return true;
	}

	private int getSupportedPreviewFrameRate(HDMIRxParameters hdmirxGetParam) {
		List<Integer> previewFrameRates = hdmirxGetParam.getSupportedPreviewFrameRates();
		int fps = 0;
		if (previewFrameRates != null && previewFrameRates.size() > 0)
			fps = previewFrameRates.get(previewFrameRates.size() - 1);
		else
			fps = 30;
		return fps;
	}

	private void getSupportedPreviewSize(HDMIRxParameters hdmirxGetParam, int rxWidth, int rxHeight) {
		List<com.realtek.hardware.RtkHDMIRxManager.Size> previewSizes = hdmirxGetParam.getSupportedPreviewSizes();
		int retWidth = 0, retHeight = 0;
		if (previewSizes == null || previewSizes.size() <= 0)
			return;
		for (int i = 0; i < previewSizes.size(); i++) {
			if (previewSizes.get(i) != null && rxWidth == previewSizes.get(i).width) {
				retWidth = previewSizes.get(i).width;
				retHeight = previewSizes.get(i).height;
				if (rxHeight == previewSizes.get(i).height)
					break;
			}
		}
		if (retWidth == 0 && retHeight == 0) {
			if (previewSizes.get(previewSizes.size() - 1) != null) {
				retWidth = previewSizes.get(previewSizes.size() - 1).width;
				retHeight = previewSizes.get(previewSizes.size() - 1).height;
			}
		}

		mWidth = retWidth;
		mHeight = retHeight;
	}

	private void initView() {
		mTextureView = new TextureView(mContext);
		mListener = new FloatingWindowTextureListener();
		mTextureView.setSurfaceTextureListener(mListener);
		mPreview = mTextureView;
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mPreview.setLayoutParams(param);
		mRootView.addView(mPreview);
	}

	class FloatingWindowTextureListener implements TextureView.SurfaceTextureListener {
		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
			mPreviewOn = true;
			// play();
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
			// stop();
			mPreviewOn = false;
			return true;
		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		}
	}

	@Override
	public boolean startDisPlay() {
		isEnable = true;
		play();
		return false;
	}

	@Override
	public boolean stopDisPlay() {
		isEnable = false;
		stop();
		return false;
	}

	@Override
	public boolean exit() {
		stopDisPlay();
		mContext.unregisterReceiver(mHdmiRxHotPlugReceiver);
		mHdmiRxHotPlugReceiver = null;
		MyLog.v("exit");
		return false;
	}

	@Override
	public void init(ViewGroup rootView) {
		mRootView = rootView;
		init();
	}

	@Override
	public boolean setAudio(boolean isOpenAudio) {
		try {
			if (mHDMIRX != null && isDisPlay) {
				mHDMIRX.setPlayback(true, isOpenAudio);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
