package com.driverstack.devicemanager.activity.auth;

import org.apache.log4j.lf5.PassingLogRecordFilter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.R.id;
import com.driverstack.devicemanager.R.layout;
import com.driverstack.devicemanager.R.menu;
import com.driverstack.devicemanager.remoteservice.RemoteService;
import com.driverstack.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.yunos.remote.vo.User;

public class RegisterActivity extends ActionBarActivity {
	private EditText editTextUsername;
	private EditText editTextPassword;
	private EditText editTextfirstName;
	private EditText editTextLastName;
	private EditText editTextEmail;
	private Button buttonCreate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		editTextUsername = (EditText) findViewById(R.id.username);
		editTextPassword = (EditText) findViewById(R.id.password);
		editTextfirstName = (EditText) findViewById(R.id.firstName);
		editTextLastName = (EditText) findViewById(R.id.lastName);
		editTextEmail = (EditText) findViewById(R.id.email);

		buttonCreate = (Button) findViewById(R.id.buttonRegister);

		buttonCreate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// login on server, get token key, save to local data store.
				// remoteService.
				String username = editTextUsername.getText().toString().trim();
				String password = editTextPassword.getText().toString().trim();
				String firstName = editTextfirstName.getText().toString()
						.trim();
				String lastName = editTextLastName.getText().toString().trim();
				String email = editTextEmail.getText().toString().trim();

				final RemoteService remoteService = RemoteServiceFactory
						.getRemoteService(RegisterActivity.this);

				User user = new User(username, password, firstName, lastName,
						email);

				new AsyncTask<User, Void, Throwable>() {

					@Override
					protected Throwable doInBackground(User... params) {
						try {
							remoteService.createUser(params[0]);
							return null;
						} catch (Exception e) {
							return e;

						}

					}

					protected void onPostExecute(Throwable result) {
						if (result == null) {
							// msg box
							AlertDialog.Builder dlgAlert = new AlertDialog.Builder(
									RegisterActivity.this);

							dlgAlert.setMessage("account created successfully. press OK to login.");
							dlgAlert.setTitle("Congratulations");
							dlgAlert.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											startLoginActivity();
										}
									});
							dlgAlert.setCancelable(true);
							dlgAlert.create().show();

							
						} else {
							Toast.makeText(
									getApplicationContext(),
									"create failure:"
											+ result.getLocalizedMessage(),
									Toast.LENGTH_LONG).show();
						}
					};

				}.execute(user);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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

	private void startLoginActivity() {
		// user is not logged in redirect him to Login Activity
		Intent i = new Intent(this, LoginActivity.class);

		// Closing all the Activities from stack
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		startActivity(i);

		finish();
	}
}
