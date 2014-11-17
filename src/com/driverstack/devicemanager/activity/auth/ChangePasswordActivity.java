package com.driverstack.devicemanager.activity.auth;

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.R.layout;
import com.driverstack.devicemanager.remoteservice.RemoteService;
import com.driverstack.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.devicemanager.remoteservice.UnauthorizedException;
import com.driverstack.devicemanager.session.Session;
import com.driverstack.devicemanager.session.SessionManager;
import com.driverstack.yunos.remote.vo.AccessToken;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends Activity {
	private TextView textViewOldPassword;
	private TextView textViewNewPassword1;
	private TextView textViewNewPassword2;

	private Button buttonSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		textViewOldPassword = (TextView) findViewById(R.id.oldPassword);
		textViewNewPassword1 = (TextView) findViewById(R.id.newPassword1);
		textViewNewPassword2 = (TextView) findViewById(R.id.newPassword2);

		buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
		buttonSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// login on server, get token key, save to local data store.
				// remoteService.
				final String oldPassword = textViewOldPassword.getText()
						.toString().trim();
				final String newPassword1 = textViewNewPassword1.getText()
						.toString();
				final String newPassword2 = textViewNewPassword2.getText()
						.toString();

				if (!newPassword1.equals(newPassword2)) {
					com.driverstack.devicemanager.utils.DialogFactory
							.getAlertDialogBuilder(getBaseContext(), "error",
									"new passwords are different", "OK", null);
				}

				SessionManager sessionManager = new SessionManager(getBaseContext());
				final Session session = sessionManager.getSession();

				new AsyncTask<Void, Void, Throwable>() {
					@Override
					protected Throwable doInBackground(Void... params) {
						try {

							String url = session.getServerUrl();
							String username = session.getUsername();

							RemoteService remoteService = RemoteServiceFactory
									.getRemoteService(url, username,
											oldPassword);

							remoteService.changePassword(newPassword1);

						} catch (Exception e) {
							return e;
						}

						return null;
					}

					protected void onPostExecute(Throwable result) {

						if (result == null) {

							finish();
						} else {
							if (result instanceof UnauthorizedException) {
								Toast.makeText(getApplicationContext(),
										"old password is wrong.",
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

	}
}
