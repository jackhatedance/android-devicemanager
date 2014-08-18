package com.deviceyun.devicemanager;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.deviceyun.yunos.remote.vo.Device;

public class DeviceDetailActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_detail);
		
		
		TextView brand = (TextView) findViewById(R.id.brand);
		TextView product = (TextView) findViewById(R.id.product);
		TextView model = (TextView) findViewById(R.id.model);
		
		TextView name = (TextView) findViewById(R.id.name);
		TextView location = (TextView) findViewById(R.id.location);
		TextView description = (TextView) findViewById(R.id.description);
		
		TextView driverVendor = (TextView) findViewById(R.id.driverVendor);
		TextView driverName = (TextView) findViewById(R.id.driverName);
		TextView driverVersion = (TextView) findViewById(R.id.driverVersion);
		
		Device dev = (Device)getIntent().getExtras().get("device");
		
		brand.setText(dev.getHardwareType().getBrand());
		product.setText(dev.getHardwareType().getProduct());
		model.setText(dev.getHardwareType().getModel());
		
		
		name.setText(dev.getName());
		location.setText(dev.getLocation());		
		description.setText(dev.getDescription());
		
		//driverVendor.setText(dev.getDescription());
		
		//model.setText(dev.getModel().getModel());

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
}
