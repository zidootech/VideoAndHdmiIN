/**
 * Copyright (C) 2016 Zidoo (www.zidoo.tv)
 * Created by : jiangbo@zidoo.tv
 */
package com.zidoo.test.hdmi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.zidoopipdemo.R;

public class HdmiTool {

	private Context			mContext		= null;

	public ViewGroup		mRootView		= null;

	private HdmiInterface	mHdmiInterface	= null;

	private View			mHdmieSigleView	= null;

	public HdmiTool(Context mContext, ViewGroup mRootView) {
		super();
		this.mContext = mContext;
		this.mRootView = mRootView;
		init();
	}

	private void init() {
		mHdmieSigleView = mRootView.findViewById(R.id.home_ac_video_hdmi_nosigle);
		mHdmiInterface = new RealtekeHdmi(mContext);
		mHdmiInterface.setZHdmiListener(new ZHdmiListener() {

			@Override
			public void connect(boolean isConnect) {
				if (mHdmiInterface.isEnable) {
					mHdmieSigleView.setVisibility(isConnect ? View.GONE : View.VISIBLE);
				}
			}
		});
		mHdmiInterface.init(mRootView);
	}

	public void start() {
		if (mHdmiInterface.isEnable) {
			return;
		}
		mHdmieSigleView.setVisibility(mHdmiInterface.isConnect ? View.GONE : View.VISIBLE);
		mHdmiInterface.startDisPlay();
	}

	public void stop() {
		if (!mHdmiInterface.isEnable) {
			return;
		}
		mHdmieSigleView.setVisibility(View.GONE);
		mHdmiInterface.stopDisPlay();
	}

	public void onDestroy() {
		mHdmiInterface.exit();
	}

	public void setAudio(boolean b) {
		// TODO Auto-generated method stub
		mHdmiInterface.setAudio(b);
	}

}
