package com.driverstack.devicemanager.activity.support;

import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.driverstack.devicemanager.remoteservice.RemoteService;
import com.driverstack.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.devicemanager.utils.Utils;

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
