/**
 * Copyright (C) 2016 Zidoo (www.zidoo.tv)
 * Created by : lc@zidoo.tv
 */
package com.example.zidoopipdemo.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPipPopManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumPipReturn;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

/**
 * 画中画播放控件
 * 
 * @author lic
 * 
 */
public class PipSurfaceView extends SurfaceView implements Callback {

	public PipSurfaceView(Context context) {
		super(context);
		transparent();
	}

	public PipSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		transparent();
	}

	public PipSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		transparent();
	}

	private void transparent() {
		this.setZOrderOnTop(true);
		// this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		openPip(getLeft(), getTop(), width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			TvManager.getInstance().getPlayerManager().setDisplay(holder);
		} catch (TvCommonException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		closePip();
	}

	/**
	 * 打开画中画
	 * 
	 * @param x
	 *            画中画的横坐标
	 * @param y
	 *            画中画的纵坐标
	 * @param width
	 *            画中画的宽度
	 * @param height
	 *            画中画的高度
	 */
	private void openPip(final int x, final int y, final int width, final int height) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				TvCommonManager commonManager = TvCommonManager.getInstance();
				TvPipPopManager pipPopManager = TvPipPopManager.getInstance();
				pipPopManager.setPipDisplayFocusWindow(EnumScalerWindow.E_MAIN_WINDOW);
				EnumInputSource current = commonManager.getCurrentInputSource();
				EnumPipReturn pipReturn = null;
				if (current == null) {
					commonManager.setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
					pipReturn = pipPopManager.enablePipTV(EnumInputSource.E_INPUT_SOURCE_STORAGE, EnumInputSource.E_INPUT_SOURCE_HDMI, getScaledWindowType(x, y, width, height));
				} else {
					if (current != EnumInputSource.E_INPUT_SOURCE_STORAGE)
						commonManager.setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
					pipReturn = pipPopManager.enablePipTV(current, EnumInputSource.E_INPUT_SOURCE_HDMI, getScaledWindowType(x, y, width, height));
				}
				if (pipReturn != EnumPipReturn.E_PIP_NOT_SUPPORT) {
					pipPopManager.setPipOnFlag(true);
				} else {
					Log.e("PipService", "PIP Error Prompt!!");
				}
			}
		}).start();
	}

	/**
	 * 关闭画中画
	 */
	private void closePip() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				TvPipPopManager pipPopManager = TvPipPopManager.getInstance();
				pipPopManager.disablePip();
				pipPopManager.setPipOnFlag(false);
			}
		}).start();
	}

	private VideoWindowType getScaledWindowType(int x, int y, int width, int height) {
		VideoWindowType videoWindowType = new VideoWindowType();
		videoWindowType.x = x;
		videoWindowType.y = y;
		videoWindowType.width = width;
		videoWindowType.height = height;
		return videoWindowType;
	}
}
