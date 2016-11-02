/**
 * Copyright (C) 2016 Zidoo (www.zidoo.tv)
 * Created by : jiangbo@zidoo.tv
 */
package com.example.zidoopipdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.example.zidoopipdemo.view.VideoView;
import com.zidoo.test.hdmi.HdmiTool;

public class MainActivity extends Activity implements OnClickListener {

	private PlayVideoTool	mPlayVideoTool	= null;

	private boolean			isVideoSount	= true;

	private WebView			mWebView		= null;
	private HdmiTool				mHdmiTool			= null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView() {
		mPlayVideoTool = new PlayVideoTool((VideoView) findViewById(R.id.home_ac_videoview));
		findViewById(R.id.sound).setOnClickListener(this);
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.loadUrl("http://www.zidoo.tv");
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}
		});
		
		ViewGroup hdmiGroud = (ViewGroup) findViewById(R.id.home_ac_hdmi);
		mHdmiTool = new HdmiTool(MainActivity.this, hdmiGroud);
		mHdmiTool.start();
	}

	@Override
	protected void onDestroy() {
		mPlayVideoTool.onDestroy();
		if (mHdmiTool != null) {
			mHdmiTool.onDestroy();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sound:
			isVideoSount = !isVideoSount;
			mHdmiTool.setAudio(!isVideoSount);
			((Button) v).setText(isVideoSount ? "Switch sound (Video)" : "Switch sound (HDMI)");
			break;

		default:
			break;
		}
	}
}
