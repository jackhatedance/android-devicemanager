package com.deviceyun.devicemanager.remoteservice;

import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.Date;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.okhttp.OkHttpClient;

public class RemoteServiceFactory {
	// private static final String urlApi =
	// "http://api.dev.deviceyun.com/api/1.0/";
	private static final String urlApi = "http://win7dev:8080/web/api/1.0/";

	private static RemoteService remoteService = null;

	public static RemoteService getRemoteService() {
		if (remoteService == null)
			remoteService = createRemoteService();
		return remoteService;
	}

	private static RemoteService createRemoteService() {

		Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.create();

		OkHttpClient client = new OkHttpClient();
		final java.net.CookieManager cookieManager = new java.net.CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		client.setCookieHandler(cookieManager);

		RequestInterceptor AuthIntercepter = new RequestInterceptor() {
			@Override
			public void intercept(RequestFacade requestFacade) {

				for (HttpCookie c : cookieManager.getCookieStore().getCookies()) {
					if (c.getName().contains("SESSIONID"))
						requestFacade.addHeader("Cookie", c.toString());

				}

			}
		};

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(urlApi)
				.setConverter(new GsonConverter(gson))
				.setRequestInterceptor(AuthIntercepter)
				.setClient(new OkClient(client)).build();

		RemoteService service = restAdapter.create(RemoteService.class);

		return service;
	}
}
