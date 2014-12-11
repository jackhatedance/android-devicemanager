package com.driverstack.devicemanager.remoteservice;

import java.util.Date;
import java.util.Map;

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
import com.driverstack.yunos.remote.exception.RemoteError;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

public class RemoteServiceFactory {

	/**
	 * for API that require authentication
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param service
	 * @return
	 */
	public static RemoteService getRemoteService(String url, String username,
			String password) {
		return createRemoteService(url, username, password,
				RemoteService.class, null, null);

	}

	/**
	 * for API that before login, such register.
	 * 
	 * @param url
	 * @param service
	 * @return
	 */
	public static RemoteService getRemoteService(String url) {
		return createRemoteService(url, null, null, RemoteService.class, null,
				null);

	}

	public static RemoteService getRemoteService(Context context) {
		return getRemoteService(context, RemoteService.class, null, null);
	}

	public static <T> T getRemoteService(Context context, Class<T> service,
			Map<String, String> pathParameters,
			Map<String, String> queryParameters) {
		SessionManager sessionManager = new SessionManager(context);
		Session session = sessionManager.getSession();

		if (session != null) {
			String key = session.getString(Session.KEY_TOKEN_KEY);
			String secret = session.getString(Session.KEY_TOKEN_SECRET);

			String url = session.getString(Session.KEY_SERVER_URL);

			return createRemoteService(url, key, secret, service,
					pathParameters, queryParameters);

		} else {
			Settings settings = new Settings(context);
			String url = settings.getEffectiveServerUrl();
			return createRemoteService(url, null, null, service, null, null);
		}

	}

	private static <T> T createRemoteService(String url, String username,
			String password, Class<T> service,
			Map<String, String> pathParameters,
			Map<String, String> queryParameters) {

		RestAdapter restAdapter = createRestAdapter(url, username, password,
				pathParameters, queryParameters);

		return restAdapter.create(service);
	}

	private static RestAdapter createRestAdapter(String url, String username,
			String password, final Map<String, String> pathParameters,
			final Map<String, String> queryParameters) {

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

					// append url parameters
					if (pathParameters != null) {
						for (String name : pathParameters.keySet()) {
							String value = pathParameters.get(name);
							requestFacade.addPathParam(name, value);
						}
					}

					if (queryParameters != null) {
						for (String name : queryParameters.keySet()) {
							String value = queryParameters.get(name);
							requestFacade.addQueryParam(name, value);
						}
					}
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

					RemoteError body = null;
					try {
						body = (RemoteError) error.getBodyAs(RemoteError.class);
					} catch (Exception e) {

						body = new RemoteError("ServerError", "ServerError",
								"server error", "server error");
					}

					String msg = String.format("%s: %s", body.getName(),
							body.getMessage());
					return new RuntimeException(msg);
				}

				return error;
			}
		});

		RestAdapter restAdapter = builder.build();

		return restAdapter;
	}

}
