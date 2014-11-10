package com.deviceyun.devicemanager.activity.device;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deviceyun.devicemanager.R;
import com.deviceyun.devicemanager.R.id;
import com.deviceyun.devicemanager.R.layout;
import com.deviceyun.devicemanager.R.menu;
import com.deviceyun.devicemanager.activity.support.BaseActionBarActivity;
import com.deviceyun.devicemanager.activity.support.Constants;
import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.deviceyun.devicemanager.ui.DropdownList;
import com.deviceyun.devicemanager.ui.ObjectToIdValue;
import com.deviceyun.devicemanager.utils.Utils;
import com.driverstack.yunos.remote.vo.ConfigurationItem;
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.DeviceClass;
import com.driverstack.yunos.remote.vo.Driver;
import com.driverstack.yunos.remote.vo.FunctionalDevice;
import com.driverstack.yunos.remote.vo.Model;
import com.driverstack.yunos.remote.vo.Vendor;

public class DeviceDetailActivity extends BaseActionBarActivity {

	public static final int REQUEST_DEVICE_CONFIG = 1;
	public static final int REQUEST_CHANGE_DEVICE = 2;

	private Device device;

	/**
	 * hold value return from device config activity
	 */
	private List<ConfigurationItem> deviceConfigurationitems;

	private TextView name;
	private TextView location;
	private TextView description;

	private Spinner vendor = null;
	private DropdownList<Vendor> vendorDropdownList = null;

	private Spinner deviceClass = null;
	private DropdownList<DeviceClass> deviceClassDropdownList = null;

	private Spinner model = null;
	private DropdownList<Model> modelDropdownList = null;

	private Spinner spinnerDriver = null;
	private DropdownList<Driver> driverDropdownList = null;
	private Button buttonConfigureDriver;

	private Spinner functionalDevice = null;
	private DropdownList<FunctionalDevice> functionalDeviceDropdownList = null;

	private String oldDriverId;
	private String currentDriverId;

	List<Vendor> vendors = null;
	List<DeviceClass> deviceClasses = null;
	List<Model> models = null;
	List<Driver> drivers = null;
	List<FunctionalDevice> functionalDevices = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_detail);

		vendor = (Spinner) findViewById(R.id.vendor);
		deviceClass = (Spinner) findViewById(R.id.deviceClass);
		model = (Spinner) findViewById(R.id.model);

		name = (TextView) findViewById(R.id.name1);
		location = (TextView) findViewById(R.id.location);
		description = (TextView) findViewById(R.id.description);

		spinnerDriver = (Spinner) findViewById(R.id.spinnerDriver);
		buttonConfigureDriver = (Button) findViewById(R.id.buttonConfigureDriver);

		functionalDevice = (Spinner) findViewById(R.id.spinnerDefaultFunctionalDevice);

		device = (Device) getIntent().getExtras().get("device");
		// used to determine whether user changed driver.
		oldDriverId = device.getDriverId();

		initEventListener();

		// update simple fields, such as editText
		updateView();

		// update dropdown list fields.
		refreshVendorDropdownList();
	}

	private void loadData() {
		// load data

		vendors = remoteService.getAllVendors(currentLocale.toString());

		if (device.getVendorId() != null) {
			models = remoteService.getModels(device.getVendorId(),
					device.getDeviceClassId(), currentLocale.toString());
		} else
			models = new ArrayList<Model>();

		if (device.getModelId() != null)
			drivers = remoteService.getDrivers(device.getModelId());
		else
			drivers = new ArrayList<Driver>();

		if (device.getId() != null)
			functionalDevices = remoteService.getFunctionalDevices(
					device.getId(), currentLocale.toString());
		else
			functionalDevices = new ArrayList<FunctionalDevice>();
	}

	private void initEventListener() {

		vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				device.setVendorId(vendorDropdownList.getSelectedObjectId());

				refreshDeviceClassDropdownList();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		deviceClass
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						device.setDeviceClassId(deviceClassDropdownList
								.getSelectedObjectId());

						refreshModelDropdownList();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				device.setModelId(modelDropdownList.getSelectedObjectId());
				refreshDriverDropdownList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		spinnerDriver
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						device.setDriverId(driverDropdownList
								.getSelectedObjectId());
						refreshFunctionalDeviceDropdownList();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		buttonConfigureDriver.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String selDriverId = driverDropdownList
						.getSelectedObjectId();
				if (selDriverId != null) {

					final Intent myIntent = new Intent(
							DeviceDetailActivity.this,
							DeviceConfigurationActivity.class);

					// updateModel();
					new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							if (device.getDriverId().equals(selDriverId)) {
								deviceConfigurationitems = remoteService
										.getDeviceConfiguration(device.getId());
							} else {
								deviceConfigurationitems = remoteService
										.getDeviceInitialConfiguration(
												device.getId(), selDriverId);
							}
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							myIntent.putExtra(
									DeviceConfigurationActivity.EXTRA_DEVICE_ID,
									device.getId());
							myIntent.putExtra(
									DeviceConfigurationActivity.EXTRA_DRIVER_ID,
									selDriverId);

							myIntent.putExtra(
									DeviceConfigurationActivity.EXTRA_DEVICE_CONFIGURATION_ITEMS,
									deviceConfigurationitems
											.toArray(new ConfigurationItem[0]));

							startActivityForResult(myIntent,
									DeviceDetailActivity.REQUEST_DEVICE_CONFIG);
							super.onPostExecute(result);
						}

					}.execute();

				} else
					Toast.makeText(DeviceDetailActivity.this,
							"No driver selected", Toast.LENGTH_SHORT).show();
			}
		});

		functionalDevice
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						int index = 0;
						try {
							index = Integer
									.valueOf(functionalDeviceDropdownList
											.getSelectedObjectId());
						} catch (Exception e) {
						}

						device.setDefaultFunctionalDeviceIndex(index);

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQUEST_DEVICE_CONFIG:

			if (resultCode == RESULT_OK) {
				Object[] array = (Object[]) data
						.getSerializableExtra(DeviceConfigurationActivity.EXTRA_DEVICE_CONFIGURATION_ITEMS);

				// save driver configuration item
				deviceConfigurationitems = new ArrayList<ConfigurationItem>();
				for (Object ci : array)
					deviceConfigurationitems.add((ConfigurationItem) ci);

			}
			break;

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device_detail, menu);
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

			updateModel();
			try {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						saveModel();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						setResult(RESULT_OK);

						finish();
						super.onPostExecute(result);
					}

				}.execute();

			} catch (Exception e) {
				Toast.makeText(DeviceDetailActivity.this,
						"saved failed:" + e.getLocalizedMessage(),
						Toast.LENGTH_SHORT).show();
			}

			return true;
		} else if (id == R.id.action_cancel) {

			setResult(RESULT_CANCELED);

			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	private void refreshVendorDropdownList() {

		new AsyncTask<Void, Void, List<Vendor>>() {

			@Override
			protected List<Vendor> doInBackground(Void... params) {
				List<Vendor> vendors = remoteService.getAllVendors(

				currentLocale.toString());
				return vendors;
			}

			@Override
			protected void onPostExecute(List<Vendor> result) {

				vendorDropdownList = new DropdownList<Vendor>(
						DeviceDetailActivity.this,
						android.R.layout.simple_spinner_item, result, vendor,
						new ObjectToIdValue<Vendor>() {
							@Override
							public String getId(Vendor obj) {

								return obj.getId();
							}

							@Override
							public String getName(Vendor obj) {

								return obj.getShortName();
							}
						});

				if (vendorDropdownList.containsId(device.getVendorId()))
					vendorDropdownList.setSelectedObjectById(device
							.getVendorId());
				else
					device.setVendorId(vendorDropdownList.getSelectedObjectId());

				super.onPostExecute(result);

				refreshDeviceClassDropdownList();
			}

		}.execute();

	}

	private void refreshDeviceClassDropdownList() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				deviceClasses = remoteService.getDeviceClasses(
						device.getVendorId(), currentLocale.toString());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				deviceClassDropdownList = new DropdownList<DeviceClass>(
						DeviceDetailActivity.this,
						android.R.layout.simple_spinner_item, deviceClasses,
						deviceClass, new ObjectToIdValue<DeviceClass>() {
							@Override
							public String getId(DeviceClass obj) {

								return obj.getId();
							}

							@Override
							public String getName(DeviceClass obj) {

								return obj.getName();
							}
						});

				if (deviceClassDropdownList.containsId(device
						.getDeviceClassId()))
					deviceClassDropdownList.setSelectedObjectById(device
							.getDeviceClassId());
				else
					device.setDeviceClassId(deviceClassDropdownList
							.getSelectedObjectId());

				super.onPostExecute(result);
			}

		}.execute();

	}

	private void refreshModelDropdownList() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				models = remoteService.getModels(device.getVendorId(),
						device.getDeviceClassId(), currentLocale.toString());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				modelDropdownList = new DropdownList<Model>(
						DeviceDetailActivity.this,
						android.R.layout.simple_spinner_item, models, model,
						new ObjectToIdValue<Model>() {
							@Override
							public String getId(Model obj) {

								return obj.getId();
							}

							@Override
							public String getName(Model obj) {

								return obj.getName();
							}
						});

				if (modelDropdownList.containsId(device.getModelId()))
					modelDropdownList
							.setSelectedObjectById(device.getModelId());
				else
					device.setModelId(modelDropdownList.getSelectedObjectId());

				super.onPostExecute(result);
			}

		}.execute();

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
						DeviceDetailActivity.this,
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

	private void refreshFunctionalDeviceDropdownList() {

		new AsyncTask<Void, Void, List<FunctionalDevice>>() {
			@Override
			protected List<FunctionalDevice> doInBackground(Void... params) {
				List<FunctionalDevice> functionalDevices = null;
				if (device.getId() != null && oldDriverId != null
						&& !isDriverChanged())
					functionalDevices = remoteService.getFunctionalDevices(
							device.getId(), currentLocale.toString());
				else {
					functionalDevices = new ArrayList<FunctionalDevice>();
					FunctionalDevice defaultFD = new FunctionalDevice("",
							"Default", 0, "", "", "", "");
					functionalDevices.add(defaultFD);
				}
				return functionalDevices;
			}

			@Override
			protected void onPostExecute(List<FunctionalDevice> result) {
				functionalDeviceDropdownList = new DropdownList<FunctionalDevice>(
						DeviceDetailActivity.this,
						android.R.layout.simple_spinner_item, result,
						functionalDevice,
						new ObjectToIdValue<FunctionalDevice>() {
							@Override
							public String getId(FunctionalDevice obj) {

								return String.valueOf(obj.getIndex());
							}

							@Override
							public String getName(FunctionalDevice obj) {

								String s = String.format("%s:%s",
										obj.getOrganizationName(),
										obj.getArtifactName());
								return s;
							}
						});
				String fdId = String.valueOf(device
						.getDefaultFunctionalDeviceIndex());

				functionalDeviceDropdownList.setSelectedObjectById(fdId);

				if (functionalDeviceDropdownList.containsId(String
						.valueOf(device.getDefaultFunctionalDeviceIndex())))
					functionalDeviceDropdownList.setSelectedObjectById(String
							.valueOf(device.getDefaultFunctionalDeviceIndex()));
				else {
					int index = 0;
					try {
						index = Integer.valueOf(functionalDeviceDropdownList
								.getSelectedObjectId());
					} catch (Exception e) {
					}
					device.setDefaultFunctionalDeviceIndex(index);
				}
				super.onPostExecute(result);
			}

		}.execute();

	}

	private boolean isDriverChanged() {
		return !oldDriverId.equals(device.getDriverId());
	}

	private void updateView() {

		name.setText(device.getName());
		location.setText(device.getLocation());
		description.setText(device.getDescription());

	}

	private void updateModel() {
		device.setName(name.getText().toString());
		device.setLocation(location.getText().toString());
		device.setDescription(description.getText().toString());

	}

	private void saveModel() {
		if (device.getId() != null) {
			remoteService.updateDevice(device);
		} else {
			remoteService.addDevice(Constants.USER_ID, device);
		}

		if (deviceConfigurationitems != null) {
			remoteService.updateDeviceConfiguration(device.getId(),
					deviceConfigurationitems);
			remoteService.reloadDriver(device.getId());
		}
	}
}
