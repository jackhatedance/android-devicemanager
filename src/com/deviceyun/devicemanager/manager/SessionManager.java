package com.deviceyun.devicemanager.manager;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

	private static final String PREFER_NAME = "com.driverstack.devicemanager.session";

	// Shared Preferences reference
	SharedPreferences pref;

	// Editor reference for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// User name (make variable public to access from outside)
	public static final String KEY_USERNAME = "username";

	public static final String KEY_TOKEN_KEY = "token_key";

	public static final String KEY_TOKEN_SECRET = "token_secret";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public String getUsername() {
		return pref.getString(KEY_USERNAME, null);
	}

	public String getTokenKey() {
		return pref.getString(KEY_TOKEN_KEY, null);
	}

	public String getTokensSecret() {
		return pref.getString(KEY_TOKEN_SECRET, null);
	}

	// save current user to store
	public void saveUser(String username, String tokenKey, String tokenSecret) {

		editor.putString(KEY_USERNAME, username);
		// Storing name in pref
		editor.putString(KEY_TOKEN_KEY, tokenKey);

		// Storing email in pref
		editor.putString(KEY_TOKEN_SECRET, tokenSecret);

		// commit changes
		editor.commit();
	}

	/**
	 * Clear session details
	 * */
	public void removeUser() {

		// Clearing all user data from Shared Preferences
		editor.remove(KEY_USERNAME);
		editor.remove(KEY_TOKEN_KEY);
		editor.remove(KEY_TOKEN_SECRET);

		editor.commit();
	}

}
