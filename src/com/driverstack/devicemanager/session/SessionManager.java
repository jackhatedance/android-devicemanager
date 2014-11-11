package com.driverstack.devicemanager.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

	private static final String PREFER_NAME = "com.driverstack.devicemanager.session";

	// Shared Preferences reference
	SharedPreferences pref;

	// Editor reference for Shared preferences
	Editor editor;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	Session session;

	// Constructor
	public SessionManager(Context context) {

		pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
		editor = pref.edit();

		if (pref.contains(Session.KEY_USERNAME))
			session = new SessionImpl(pref);
	}

	public boolean isLoggedIn() {

		return session != null;
	}

	public Session createSession(String username, String key, String secret,
			String url) {

		session = new SessionImpl(pref);

		editor.putString(Session.KEY_USERNAME, username);
		editor.putString(Session.KEY_TOKEN_KEY, key);
		editor.putString(Session.KEY_TOKEN_SECRET, secret);
		editor.putString(Session.KEY_SERVER_URL, url);
		editor.commit();

		return session;
	}

	public void destroySession() {
		session.clear();
		session = null;
	}

	public Session getSession() {
		return session;

	}

}
