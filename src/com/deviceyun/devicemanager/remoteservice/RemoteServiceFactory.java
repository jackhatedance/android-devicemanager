package com.deviceyun.devicemanager.remoteservice;

import java.util.Date;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import android.content.Context;

import com.deviceyun.devicemanager.manager.SessionManager;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class RemoteServiceFactory {
	// private static final String urlApi =
	// "http://www.driverstack.com:8080/yunos/api/1.0/";
	// private static final String urlApi =
	// "http://api.dev.deviceyun.com/api/1.0/";
	private static final String DEFAULT_URL = "http://win7dev:8080/web/api/1.0/";
	static private String DEFAULT_USERNAME = "jackding";
	static private String DEFAULT_PASSWORD = "pass";

	public static RemoteService getRemoteService(String username,
			String password) {
		RemoteService remoteService = createRemoteService(username, password);
		return remoteService;
	}

	public static RemoteService getRemoteService(Context context) {
		SessionManager ds = new SessionManager(context);
		
		String key = ds.getTokenKey();
		String secret = ds.getTokensSecret();

		RemoteService remoteService = createRemoteService(key, secret);
		return remoteService;
	}

	private static RemoteService createRemoteService(String username,
			String password) {

		Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.create();
		final Header baseAuth = BasicScheme.authenticate(
				new UsernamePasswordCredentials(username, password), "UTF-8",
				false);

		RequestInterceptor AuthIntercepter = new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade requestFacade) {

				requestFacade
						.addHeader(baseAuth.getName(), baseAuth.getValue());

			}
		};

		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(DEFAULT_URL).setConverter(new GsonConverter(gson))
				.setRequestInterceptor(AuthIntercepter).build();

		RemoteService service = restAdapter.create(RemoteService.class);

		return service;
	}
}
