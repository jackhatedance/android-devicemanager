package com.driverstack.devicemanager.activity.device;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.driverstack.devicemanager.R;
import com.driverstack.devicemanager.R.id;
import com.driverstack.devicemanager.R.layout;
import com.driverstack.devicemanager.R.menu;
import com.driverstack.devicemanager.activity.support.BaseActionBarActivity;
import com.driverstack.devicemanager.ui.DropdownList;
import com.driverstack.devicemanager.ui.ObjectToIdValue;
import com.driverstack.yunos.remote.vo.ConfigurationItem;
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.Driver;

public class DriverActivity extends BaseActionBarActivity {
	public static final String EXTRA_DRIVER_ID = "driverId";
	public static final String EXTRA_DRIVER_NAME = "driverName";

	private Spinner spinnerDriver;
	private Button buttonConfigure;

	private Device device;
	private DropdownList<Driver> driverDropdownList = null;

	private String oldDriverId;
	private String currentDriverId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver);

		// retrieve input data
		device = (Device) getIntent().getExtras().get("device");

		// locatre UI components
		spinnerDriver = (Spinner) findViewById(R.id.spinnerDriver);
		buttonConfigure = (Button) findViewById(R.id.buttonConfigure);

		// set event handler

		spinnerDriver
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						device.setDriverId(driverDropdownList
								.getSelectedObjectId());

						// refreshFunctionalDeviceDropdownList();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		buttonConfigure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(android.view.View v) {
				final String selDriverId = device.getDriverId();
				if (selDriverId != null) {

					final Intent myIntent = new Intent(DriverActivity.this,
							DeviceConfigurationActivity.class);

					// updateModel();
					new AsyncTask<Void, Void, List<ConfigurationItem>>() {

						@Override
						protected List<ConfigurationItem> doInBackground(
								Void... params) {
							List<ConfigurationItem> deviceConfigurationitems;

							boolean driverNotChange = true;
							if (driverNotChange) {
								deviceConfigurationitems = remoteService
										.getDeviceConfiguration(device.getId());
							} else {
								deviceConfigurationitems = remoteService
										.getDeviceInitialConfiguration(
												device.getId(), selDriverId);
							}
							return deviceConfigurationitems;
						}

						@Override
						protected void onPostExecute(
								List<ConfigurationItem> result) {
							myIntent.putExtra(
									DeviceConfigurationActivity.EXTRA_DEVICE_ID,
									device.getId());
							myIntent.putExtra(
									DeviceConfigurationActivity.EXTRA_DRIVER_ID,
									selDriverId);

							myIntent.putExtra(
									DeviceConfigurationActivity.EXTRA_DEVICE_CONFIGURATION_ITEMS,
									result.toArray(new ConfigurationItem[0]));

							startActivityForResult(myIntent,
									DeviceDetailActivity.REQUEST_DEVICE_CONFIG);
							super.onPostExecute(result);
						}

					}.execute();

				} else
					Toast.makeText(DriverActivity.this, "No driver selected",
							Toast.LENGTH_SHORT).show();
			}
		});

		// refresh UI.
		refreshDriverDropdownList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pick_driver, menu);
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
		} else if (id == R.id.action_accept) {

			Intent result = new Intent();
			
			//return driver ID
			result.putExtra(EXTRA_DRIVER_ID,
					driverDropdownList.getSelectedObjectId());
			setResult(RESULT_OK, result);

			finish();

			return true;
		} else if (id == R.id.action_cancel) {

			setResult(RESULT_CANCELED);

			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshDriverDropdownList() {
		new AsyncTask<Void, Void, List<Driver>>() {

			@Override
			protected List<Driver> doInBackground(Void... params) {
				List<Driver> drivers;

				String modelId = device.getModelId();
				if (modelId != null)
					drivers = remoteService.getDrivers(modelId);
				else
					drivers = new ArrayList<Driver>();

				return drivers;
			}

			@Override
			protected void onPostExecute(List<Driver> result) {
				driverDropdownList = new DropdownList<Driver>(
						DriverActivity.this,
						android.R.layout.simple_spinner_item, result,
						spinnerDriver, new ObjectToIdValue<Driver>() {
							@Override
							public String getId(Driver obj) {

								return obj.getId();
							}

							@Override
							public String getName(Driver obj) {

								return obj.toString();
							}
						});
				super.onPostExecute(result);
			}

		}.execute();

	}

}
