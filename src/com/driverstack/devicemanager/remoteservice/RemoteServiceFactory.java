package com.driverstack.devicemanager.remoteservice;

import java.util.Date;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import android.content.Context;

import com.driverstack.devicemanager.preference.Settings;
import com.driverstack.devicemanager.session.Session;
import com.driverstack.devicemanager.session.SessionManager;
import com.driverstack.yunos.remote.exception.RestApiError;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class RemoteServiceFactory {
	// private static final String urlApi =
	// "http://www.driverstack.com:8080/yunos/api/1.0/";
	// private static final String urlApi =
	// "http://api.dev.driverstack.com/api/1.0/";
	// private static final String DEFAULT_URL =
	// "http://win7dev:8080/web/api/1.0/";

	public static RemoteService getRemoteService(String url, String username,
			String password) {
		RemoteService remoteService = createRemoteService(url, username,
				password);
		return remoteService;
	}

	public static RemoteService getRemoteService(String url) {
		RemoteService remoteService = createRemoteService(url, null, null);
		return remoteService;
	}

	public static RemoteService getRemoteService(Context context) {
		SessionManager sessionManager = new SessionManager(context);
		Session session = sessionManager.getSession();

		if (session != null) {
			String key = session.getString(Session.KEY_TOKEN_KEY);
			String secret = session.getString(Session.KEY_TOKEN_SECRET);

			String url = session.getString(Session.KEY_SERVER_URL);

			RemoteService remoteService = createRemoteService(url, key, secret);
			return remoteService;
		} else {
			Settings settings = new Settings(context);
			String url = settings.getEffectiveServerUrl();
			return getRemoteService(url);
		}

	}

	private static RemoteService createRemoteService(String url,
			String username, String password) {

		Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.create();

		RestAdapter.Builder builder = new RestAdapter.Builder();
		builder.setEndpoint(url).setConverter(new GsonConverter(gson));

		if (username != null) {
			final Header baseAuth = BasicScheme.authenticate(
					new UsernamePasswordCredentials(username, password),
					"UTF-8", false);

			RequestInterceptor AuthIntercepter = new RequestInterceptor() {
				@Override
				public void intercept(RequestFacade requestFacade) {

					requestFacade.addHeader(baseAuth.getName(),
							baseAuth.getValue());

				}
			};
			builder.setRequestInterceptor(AuthIntercepter);
		}

		builder.setErrorHandler(new ErrorHandler() {

			@Override
			public Throwable handleError(RetrofitError error) {

				Response response = error.getResponse();

				if (response != null && response.getStatus() == 401) {
					return new UnauthorizedException(error);
				} else if (response != null && response.getStatus() == 500) {
					RestApiError body = (RestApiError) error
							.getBodyAs(RestApiError.class);

					return new RuntimeException(body.getMessage());
				}

				return error;
			}
		});

		RestAdapter restAdapter = builder.build();

		RemoteService service = restAdapter.create(RemoteService.class);

		return service;
	}
}
