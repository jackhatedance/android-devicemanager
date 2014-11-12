package com.driverstack.devicemanager.activity.operation;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.activity.support.BaseActionBarActivity;
import com.driverstack.devicemanager.activity.support.Constants;
import com.driverstack.devicemanager.remoteservice.RemoteService;
import com.driverstack.yunos.remote.vo.FunctionalDevice;

public class SwitchActivity extends BaseActionBarActivity {

	private ToggleButton button1;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch);

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
						remoteService.operateDevice(Constants.APP_ID,
								fd.getDeviceId(), fd.getIndex(), operation,
								paramMap);
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
