package com.deviceyun.devicemanager.remoteservice;

import java.util.Date;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import com.deviceyun.yunos.remote.vo.Device;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class RemoteServiceFactory {
	private static final String urlApi = "http://api.dev.deviceyun.com/api/1.0/";

	static public RemoteService getRemoteService() {

		Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(
						FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.create();

		RestAdapter restAdapter = new RestAdapter.Builder()

		.setEndpoint(urlApi).setConverter(new GsonConverter(gson)).build();

		RemoteService service = restAdapter.create(RemoteService.class);

		return service;
	}

}
