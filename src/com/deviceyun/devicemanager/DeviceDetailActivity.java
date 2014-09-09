package com.deviceyun.devicemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.deviceyun.devicemanager.remoteservice.RemoteService;
import com.deviceyun.devicemanager.remoteservice.RemoteServiceFactory;
import com.driverstack.yunos.remote.vo.Device;
import com.driverstack.yunos.remote.vo.DeviceClass;
import com.driverstack.yunos.remote.vo.Model;
import com.driverstack.yunos.remote.vo.Vendor;

public class DeviceDetailActivity extends ActionBarActivity {

	private RemoteService remoteService;
	private Locale currentLocale;

	private Spinner vendor = null;
	private DropdownList<Vendor> vendorDropdownList = null;

	private Spinner deviceClass = null;
	private DropdownList<DeviceClass> deviceClassDropdownList = null;

	private Spinner model = null;
	private DropdownList<Model> modelDropdownList = null;

	List<Vendor> vendors = null;
	List<DeviceClass> deviceClasses = null;
	List<Model> models = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_detail);

		vendor = (Spinner) findViewById(R.id.vendor);
		deviceClass = (Spinner) findViewById(R.id.deviceClass);
		model = (Spinner) findViewById(R.id.model);

		TextView name = (TextView) findViewById(R.id.name);
		TextView location = (TextView) findViewById(R.id.location);
		TextView description = (TextView) findViewById(R.id.description);

		TextView driverVendor = (TextView) findViewById(R.id.driverVendor);
		TextView driverName = (TextView) findViewById(R.id.driverName);
		TextView driverVersion = (TextView) findViewById(R.id.driverVersion);

		Device dev = (Device) getIntent().getExtras().get("device");

		// load data
		remoteService = RemoteServiceFactory.getRemoteService();
		currentLocale = getResources().getConfiguration().locale;
		vendors = remoteService.getAllVendors(currentLocale.toString());
		deviceClasses = remoteService
				.getDeviceClasses(currentLocale.toString());
		models = remoteService.getModels(dev.getVendorId(), dev.getDeviceClassId(),
				currentLocale.toString());

		// vendor.setAdapter(createVendorDataAdapter());
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

		vendorDropdownList.setSelectedObjectById(dev.getVendorId());

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

		deviceClassDropdownList.setSelectedObjectById(dev.getDeviceClassId());

		modelDropdownList = new DropdownList<Model>(this,
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

		modelDropdownList.setSelectedObjectById(dev.getModelId());

		// model.setAdapter(createModelDataAdapter());

		// vendor.setText(dev.getHardwareType().getVendor());

		// product.setText(dev.getHardwareType().getClass());
		// model.setText(dev.getHardwareType().getModel());

		name.setText(dev.getName());
		location.setText(dev.getLocation());
		description.setText(dev.getDescription());

		// driverVendor.setText(dev.getDescription());

		// model.setText(dev.getModel().getModel());

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

			Toast.makeText(DeviceDetailActivity.this, "saved!",
					Toast.LENGTH_SHORT).show();

			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	private void saveBasicInfo() {

	}
}
