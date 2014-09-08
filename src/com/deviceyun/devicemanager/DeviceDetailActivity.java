package com.deviceyun.devicemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
	private Spinner deviceClass = null;
	private Spinner model = null;
	
	List<Vendor > vendors=null;
	List<DeviceClass> deviceClasses=null;
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

		//load data
		remoteService = RemoteServiceFactory.getRemoteService();
		currentLocale = getResources().getConfiguration().locale;
		vendors = remoteService.getAllVendors(currentLocale.toString());
		deviceClasses = remoteService.getDeviceClasses(currentLocale.toString());
		models = remoteService.getModels(dev.getVendorId(), currentLocale.toString());
				
		
		
		vendor.setAdapter(createVendorDataAdapter());
		
		deviceClass.setAdapter(createDeviceClassDataAdapter());
		
		model.setAdapter(createModelDataAdapter(dev.getVendorId()));
		
		//model.setAdapter(createModelDataAdapter());
		
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
		}
		return super.onOptionsItemSelected(item);
	}

	private ArrayAdapter<String> createVendorDataAdapter() {

		

		List<String> vendorNames = new ArrayList<String>();
		for (Vendor v : vendors)
			vendorNames.add(v.getShortName());

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, vendorNames);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


		return dataAdapter;
	}

	private ArrayAdapter<String> createDeviceClassDataAdapter() {
		List<DeviceClass> deviceClasses = remoteService.getDeviceClasses(currentLocale
				.toString());

		List<String> names = new ArrayList<String>();
		for (DeviceClass devCls : deviceClasses)
			names.add(devCls.getName());

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, names);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return dataAdapter;
	}
	
	private ArrayAdapter<String> createModelDataAdapter(String vendorId) {
		List<Model> models = remoteService.getModels(vendorId,currentLocale
				.toString());

		List<String> names = new ArrayList<String>();
		for (Model m : models)
			names.add(m.getName());

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, names);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		return dataAdapter;
	}


}
