package com.deviceyun.devicemanager.remoteservice;

import java.util.Date;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class RemoteServiceFactory {
	private static final String urlApi = "http://api.dev.deviceyun.com/api/1.0/";

	private static RemoteService remoteService =null;
	public static RemoteService getRemoteService(){
		if(remoteService==null)
			remoteService = createRemoteService();
		return remoteService;
	}
	
	private static RemoteService createRemoteService() {

		Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(
						FieldNamingPolicy.IDENTITY)
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.create();

		RestAdapter restAdapter = new RestAdapter.Builder()

		.setEndpoint(urlApi).setConverter(new GsonConverter(gson)).build();

		RemoteService service = restAdapter.create(RemoteService.class);

		return service;
	}

}
