package com.deviceyun.devicemanager.remoteservice;

import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.okhttp.OkHttpClient;

public class RemoteServiceFactory {
	//private static final String urlApi = "http://api.dev.deviceyun.com/api/1.0/";
	 private static final String urlApi = "http://win7dev:8080/web/api/1.0/";

	private static RemoteService remoteService = null;

	static private String username = "jackding";
	static private String password = "pass";

	public static RemoteService getRemoteService() {
		if (remoteService == null)
			remoteService = createRemoteService(username, password);
		return remoteService;
	}

	public static RemoteService getRemoteService(String username,
			String password) {
		if (remoteService == null)
			remoteService = createRemoteService(username, password);
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

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(urlApi)
				.setConverter(new GsonConverter(gson))
				.setRequestInterceptor(AuthIntercepter).build();

		RemoteService service = restAdapter.create(RemoteService.class);

		return service;
	}
}
