package com.deviceyun.devicemanager;

import java.util.List;
import java.util.Locale;

import android.content.Intent;
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
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.DeviceClass;
import com.driverstack.yunos.remote.vo.Driver;
import com.driverstack.yunos.remote.vo.FunctionalDevice;
import com.driverstack.yunos.remote.vo.Model;
import com.driverstack.yunos.remote.vo.Vendor;

public class DeviceDetailActivity extends ActionBarActivity {

	private RemoteService remoteService;
	private Locale currentLocale;

	private Device device;

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

		// load data
		remoteService = RemoteServiceFactory.getRemoteService();
		currentLocale = Utils.getLocale(this);
		vendors = remoteService.getAllVendors(currentLocale.toString());
		deviceClasses = remoteService.getDeviceClasses(device.getVendorId(),
				currentLocale.toString());
		models = remoteService.getModels(device.getVendorId(),
				device.getDeviceClassId(), currentLocale.toString());
		drivers = remoteService.getDrivers(device.getModelId());
		functionalDevices = remoteService.getFunctionalDevices(device.getId(),
				currentLocale.toString());

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
				Intent myIntent = new Intent(DeviceDetailActivity.this,
						DeviceConfigurationActivity.class);

				updateModel();

				myIntent.putExtra("device", device);
				startActivityForResult(myIntent, 1);
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
				saveModel();

				setResult(RESULT_OK);

				finish();
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
		deviceClasses = remoteService.getDeviceClasses(
				vendorDropdownList.getSelectedObjectId(),
				currentLocale.toString());

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
	}

	private void refreshModelDropdownList() {
		Toast.makeText(DeviceDetailActivity.this, "vendor chnaged",
				Toast.LENGTH_SHORT).show();

		// update model options
		models = remoteService.getModels(
				vendorDropdownList.getSelectedObjectId(),
				deviceClassDropdownList.getSelectedObjectId(),
				currentLocale.toString());

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
	}

	private void refreshDriverDropdownList() {
		// update driver options
		drivers = remoteService.getDrivers(modelDropdownList
				.getSelectedObjectId());

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
		remoteService.updateDevice(device);
	}
}
