package com.driverstack.devicemanager.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

	public final static String DEVELOPMENT_MODE = "developmentMode";
	public final static boolean DEVELOPMENT_MODE_DEFAULT_VALUE = false;

	public final static String SERVER_URL = "serverUrl";
	public final static String SERVER_URL_DEFAULT_VALUE = "http://www.driverstack.com/api/1.0/";

	SharedPreferences preferences;

	public Settings(Context context) {

		preferences = PreferenceManager.getDefaultSharedPreferences(context);

	}

	public boolean getDevelopmentMode() {
		return preferences.getBoolean(DEVELOPMENT_MODE,
				DEVELOPMENT_MODE_DEFAULT_VALUE);

	}

	private String getServerUrl() {

		return preferences.getString(SERVER_URL, SERVER_URL_DEFAULT_VALUE);

	}

	public String getEffectiveServerUrl() {
		if (getDevelopmentMode())
			return getServerUrl();
		else
			return SERVER_URL_DEFAULT_VALUE;
	}

}
