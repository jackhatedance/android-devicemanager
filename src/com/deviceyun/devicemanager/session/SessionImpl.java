package com.deviceyun.devicemanager.session;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionImpl implements Session {

	SharedPreferences pref;

	Editor editor;

	// User name (make variable public to access from outside)
	public static final String KEY_USERNAME = "username";

	public static final String KEY_TOKEN_KEY = "token_key";

	public static final String KEY_TOKEN_SECRET = "token_secret";

	public static final String KEY_SERVER_URL = "serverUrl";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";

	// Constructor
	public SessionImpl(SharedPreferences pref) {
		this.pref = pref;

		editor = pref.edit();
	}

	@Override
	public String getString(String key) {
		return pref.getString(key, null);
	}

	@Override
	public void put(String key, String value) {
		editor.putString(key, value);
		editor.commit();

	}

	public void clearSessionAttributes() {
		editor.remove(KEY_TOKEN_KEY);
		editor.remove(KEY_TOKEN_SECRET);

		editor.commit();
	}

	@Override
	public void clear() {
		editor.clear();
		editor.commit();
	}
}
