package com.deviceyun.devicemanager.activity.support;

import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.deviceyun.devicemanager.utils.Utils;

public class BaseActionBarActivity extends ActionBarActivity {

	protected RemoteService remoteService;
	protected Locale currentLocale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		remoteService = RemoteServiceFactory.getRemoteService(this);
		currentLocale = Utils.getLocale(BaseActionBarActivity.this);
		
	}

}
