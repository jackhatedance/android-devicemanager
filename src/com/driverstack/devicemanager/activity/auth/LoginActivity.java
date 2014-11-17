package com.driverstack.devicemanager.activity.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.activity.MainActivity;
import com.driverstack.devicemanager.activity.SettingsActivity;
import com.driverstack.devicemanager.preference.Settings;
import com.driverstack.devicemanager.remoteservice.RemoteService;
import com.driverstack.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.devicemanager.remoteservice.UnauthorizedException;
import com.driverstack.devicemanager.session.SessionManager;
import com.driverstack.yunos.remote.vo.AccessToken;

public class LoginActivity extends ActionBarActivity {

	private TextView textViewUsername;
	private TextView textViewPassword;
	private Button buttonLogin;
	private Button buttonRegister;

	SessionManager sessionManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		sessionManager = new SessionManager(this);

		textViewUsername = (TextView) findViewById(R.id.oldPassword);
		textViewPassword = (TextView) findViewById(R.id.newPassword1);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);

		buttonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// login on server, get token key, save to local data store.
				// remoteService.
				final String username = textViewUsername.getText().toString()
						.trim();
				final String password = textViewPassword.getText().toString()
						.trim();

				Settings settings = new Settings(LoginActivity.this);
				final String url = settings.getEffectiveServerUrl();

				new AsyncTask<Void, Void, Throwable>() {
					@Override
					protected Throwable doInBackground(Void... params) {
						try {

							RemoteService remoteService = RemoteServiceFactory
									.getRemoteService(url, username, password);

							AccessToken accessToken = remoteService
									.requestAccessToken();
							sessionManager.createSession(username,
									accessToken.getKey(),
									accessToken.getSecret(), url);

						} catch (Exception e) {
							return e;
						}

						return null;
					}

					protected void onPostExecute(Throwable result) {

						if (result == null) {

							startMainActivity();
						} else {
							if (result instanceof UnauthorizedException) {
								Toast.makeText(getApplicationContext(),
										"Password or username is wrong.",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getApplicationContext(),

								"server error:" + result.getLocalizedMessage(),
										Toast.LENGTH_LONG).show();
							}
						}
					};
				}.execute();

			}
		});

		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		buttonRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startRegisterActivity();
			}
		});
	}

	private void startMainActivity() {
		Intent i = new Intent(this, MainActivity.class);

		// Closing all the Activities from stack
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		startActivity(i);

		finish();
	}

	private void startRegisterActivity() {

		Intent i = new Intent(this, RegisterActivity.class);

		startActivity(i);

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
