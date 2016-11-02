/**
 * Copyright (C) 2016 Zidoo (www.zidoo.tv)
 * Created by : jiangbo@zidoo.tv
 */
package com.zidoo.test.hdmi;

import android.content.Context;
import android.view.ViewGroup;

public abstract class HdmiInterface {

	public Context			mContext		= null;

	public ZHdmiListener	mHdmiListener	= null;

	public boolean			isConnect		= false;

	public boolean			isDisPlay		= false;

	public boolean			isEnable		= false;

	public ViewGroup		mRootView		= null;

	public HdmiInterface(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public void setZHdmiListener(ZHdmiListener mHdmiListener) {
		this.mHdmiListener = mHdmiListener;
	}

	public abstract boolean startDisPlay();

	public abstract boolean stopDisPlay();

	public abstract boolean exit();

	public abstract void init(ViewGroup rootView);

	public abstract boolean setAudio(boolean isOpenAudio); 
	
}
