package com.deviceyun.devicemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.deviceyun.devicemanager.manager.SessionManager;
import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.yunos.remote.vo.AccessToken;

public class LoginActivity extends ActionBarActivity {

	private TextView textViewUsername;
	private TextView textViewPassword;
	private Button buttonLogin;
	private Button buttonRegister;

	SessionManager dataStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		dataStore = new SessionManager(this);

		textViewUsername = (TextView) findViewById(R.id.username);
		textViewPassword = (TextView) findViewById(R.id.password);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);

		buttonLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// login on server, get token key, save to local data store.
				// remoteService.
				String username = textViewUsername.getText().toString().trim();
				String password = textViewPassword.getText().toString().trim();

				try {
					RemoteService authService = RemoteServiceFactory
							.getRemoteService(username, password);

					AccessToken accessToken = authService.requestAccessToken();

					dataStore.saveUser(username, accessToken.getKey(),
							accessToken.getSecret());
					startMainActivity();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"login failure:" + e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
				}
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
