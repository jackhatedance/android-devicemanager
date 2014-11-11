package com.driverstack.devicemanager.session;

public interface Session {

	public static final String KEY_USERNAME = "username";

	public static final String KEY_TOKEN_KEY = "token_key";

	public static final String KEY_TOKEN_SECRET = "token_secret";

	public static final String KEY_SERVER_URL = "serverUrl";

	void put(String key, String value);

	String getString(String key);

	void clear();

}
