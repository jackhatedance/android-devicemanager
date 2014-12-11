package com.driverstack.devicemanager.activity.operation;

import java.util.HashMap;
import java.util.Map;

import retrofit.http.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.activity.support.BaseActionBarActivity;
import com.driverstack.devicemanager.activity.support.Constants;
import com.driverstack.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.devicemanager.remoteservice.StatefulSwitchRemoteService;
import com.driverstack.yunos.remote.vo.FunctionalDevice;

public class SwitchActivity extends BaseActionBarActivity {

	private ToggleButton button1;

	private StatefulSwitchRemoteService statefulSwicthRemoteService;

	private FunctionalDevice fd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);

		fd = (FunctionalDevice) getIntent().getExtras().get("functionalDevice");

		Map<String, String> pathParameters = new HashMap<String, String>();
		pathParameters.put("deviceId", fd.getDeviceId());
		pathParameters.put("functionalDeviceIndex",
				String.valueOf(fd.getIndex()));

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("appId", Constants.APP_ID);

		statefulSwicthRemoteService = RemoteServiceFactory.getRemoteService(
				this, StatefulSwitchRemoteService.class, pathParameters,
				queryParameters);

		button1 = (ToggleButton) findViewById(R.id.toggleButton1);

		initUI();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gswitch, menu);
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

	private void initUI() {

		if (fd.getArtifactName().equals("StatefulElectricitySwitch")) {
			new AsyncTask<Void, Void, Throwable>() {
				private boolean isOn;

				@Override
				protected Throwable doInBackground(Void... params) {
					Map<String, String> paramMap = new HashMap<String, String>();
					try {
						isOn = statefulSwicthRemoteService.isOn();
					} catch (Exception e) {
						return e;
					}
					return null;
				}

				protected void onPostExecute(Throwable result) {
					if (result != null) {
						Toast.makeText(getBaseContext(),
								"error:" + result.getLocalizedMessage(),
								Toast.LENGTH_SHORT).show();
					} else {

						button1.setChecked(isOn);
						setButtonListener();
					}

					super.onPostExecute(result);
				};

			}.execute();
		} else {
			setButtonListener();
		}
	}

	private void setButtonListener() {
		button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				final String operation = isChecked ? "on" : "off";
				final Map<String, String> paramMap = new HashMap<String, String>();

				new AsyncTask<Void, Void, Throwable>() {

					@Override
					protected Throwable doInBackground(Void... params) {
						try {
							remoteService.operateDevice(Constants.APP_ID,
									fd.getDeviceId(), fd.getIndex(), operation,
									paramMap);
						} catch (Exception e) {
							return e;
						}
						return null;
					}

					@Override
					protected void onPostExecute(Throwable result) {
						if (result != null) {
							Toast.makeText(getBaseContext(),
									"error:" + result.getLocalizedMessage(),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getBaseContext(), "success.",
									Toast.LENGTH_SHORT).show();
						}

						super.onPostExecute(result);
					}

				}.execute();

			}
		});
	}
}
