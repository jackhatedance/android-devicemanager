package com.deviceyun.devicemanager.operation_activity;

import java.util.HashMap;
import java.util.Map;

import com.deviceyun.devicemanager.Constants;
import com.deviceyun.devicemanager.R;
import com.deviceyun.devicemanager.R.id;
import com.deviceyun.devicemanager.R.layout;
import com.deviceyun.devicemanager.R.menu;
import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.FunctionalDevice;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class SwitchActivity extends ActionBarActivity {

	private ToggleButton button1;

	private RemoteService remoteService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);

		remoteService = RemoteServiceFactory.getRemoteService(this);

		final FunctionalDevice fd = (FunctionalDevice) getIntent().getExtras()
				.get("functionalDevice");

		button1 = (ToggleButton) findViewById(R.id.toggleButton1);

		button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				final String operation = isChecked ? "on" : "off";
				final Map<String, String> paramMap = new HashMap<String, String>();
				
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						remoteService.operateDevice(Constants.APP_ID,fd.getDeviceId(), fd.getIndex(),
								operation, paramMap);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						 
						super.onPostExecute(result);
					}

				}.execute();
				

			}
		});

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
}
