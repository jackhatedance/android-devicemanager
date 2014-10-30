package com.deviceyun.devicemanager;

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

public class DeviceDetailActivity extends ActionBarActivity {

	public static final int REQUEST_DEVICE_CONFIG = 1;

	private RemoteService remoteService;
	private Locale currentLocale;

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

	private Spinner driver = null;
	private DropdownList<Driver> driverDropdownList = null;

	private Spinner defaultFunctionalDevice = null;
	private DropdownList<FunctionalDevice> functionalDeviceDropdownList = null;

	private Button buttonConfigure;

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

		name = (TextView) findViewById(R.id.name);
		location = (TextView) findViewById(R.id.location);
		description = (TextView) findViewById(R.id.description);

		driver = (Spinner) findViewById(R.id.spinnerDriver);
		defaultFunctionalDevice = (Spinner) findViewById(R.id.spinnerDefaultFunctionalDevice);

		device = (Device) getIntent().getExtras().get("device");

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				loadData();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				initUI();
				super.onPostExecute(result);
			}

		}.execute();
	}

	private void loadData() {
		// load data
		remoteService = RemoteServiceFactory.getRemoteService(this);
		currentLocale = Utils.getLocale(DeviceDetailActivity.this);
		vendors = remoteService.getAllVendors(currentLocale.toString());
		deviceClasses = remoteService.getDeviceClasses(device.getVendorId(),
				currentLocale.toString());

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

	private void initUI() {
		vendorDropdownList = new DropdownList<Vendor>(this,
				android.R.layout.simple_spinner_item, vendors, vendor,
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

		vendor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				refreshDeviceClassDropdownList();
				refreshModelDropdownList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		deviceClassDropdownList = new DropdownList<DeviceClass>(this,
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

		deviceClass
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {

						refreshModelDropdownList();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}

				});

		modelDropdownList = new DropdownList<Model>(DeviceDetailActivity.this,
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

		model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				refreshDriverDropdownList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		driverDropdownList = new DropdownList<Driver>(
				DeviceDetailActivity.this,
				android.R.layout.simple_spinner_item, drivers, driver,
				new ObjectToIdValue<Driver>() {
					@Override
					public String getId(Driver obj) {

						return obj.getId();
					}

					@Override
					public String getName(Driver obj) {

						return obj.toString();
					}
				});

		buttonConfigure = (Button) findViewById(R.id.buttonConfigureDriver);
		buttonConfigure.setOnClickListener(new View.OnClickListener() {

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

		functionalDeviceDropdownList = new DropdownList<FunctionalDevice>(
				DeviceDetailActivity.this,
				android.R.layout.simple_spinner_item, functionalDevices,
				defaultFunctionalDevice,
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

		updateView();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_DEVICE_CONFIG) {
			if (resultCode == RESULT_OK) {
				Object[] array = (Object[]) data
						.getSerializableExtra(DeviceConfigurationActivity.EXTRA_DEVICE_CONFIGURATION_ITEMS);
				deviceConfigurationitems = new ArrayList<ConfigurationItem>();
				for (Object ci : array)
					deviceConfigurationitems.add((ConfigurationItem) ci);

			}
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

	private void refreshDeviceClassDropdownList() {

		// update model options

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				deviceClasses = remoteService.getDeviceClasses(
						vendorDropdownList.getSelectedObjectId(),
						currentLocale.toString());
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
				super.onPostExecute(result);
			}

		}.execute();

	}

	private void refreshModelDropdownList() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				models = remoteService.getModels(
						vendorDropdownList.getSelectedObjectId(),
						deviceClassDropdownList.getSelectedObjectId(),
						currentLocale.toString());
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
				super.onPostExecute(result);
			}

		}.execute();

	}

	private void refreshDriverDropdownList() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				drivers = remoteService.getDrivers(modelDropdownList
						.getSelectedObjectId());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				driverDropdownList = new DropdownList<Driver>(
						DeviceDetailActivity.this,
						android.R.layout.simple_spinner_item, drivers, driver,
						new ObjectToIdValue<Driver>() {
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

	private void updateView() {
		vendorDropdownList.setSelectedObjectById(device.getVendorId());
		deviceClassDropdownList
				.setSelectedObjectById(device.getDeviceClassId());

		modelDropdownList.setSelectedObjectById(device.getModelId());

		name.setText(device.getName());
		location.setText(device.getLocation());
		description.setText(device.getDescription());
		if (device.getDriverId() != null)
			driverDropdownList.setSelectedObjectById(device.getDriverId());

		// spinnerDefaultFunctionalDevice
		functionalDeviceDropdownList.setSelectedObjectById(String
				.valueOf(device.getDefaultFunctionalDeviceIndex()));
	}

	private void updateModel() {
		device.setDeviceClassId(deviceClassDropdownList.getSelectedObjectId());
		device.setModelId(modelDropdownList.getSelectedObjectId());
		device.setName(name.getText().toString());
		device.setLocation(location.getText().toString());
		device.setDescription(description.getText().toString());
		device.setDriverId(driverDropdownList.getSelectedObjectId());

		String selFuntionalDeviceId = functionalDeviceDropdownList
				.getSelectedObjectId();
		if (selFuntionalDeviceId != null)
			device.setDefaultFunctionalDeviceIndex(Integer
					.valueOf(functionalDeviceDropdownList.getSelectedObjectId()));
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
